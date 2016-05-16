package graduation.tatev.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import graduation.tatev.myapplication.Utils.OnTaskCompleted;
import graduation.tatev.myapplication.Utils.RouteUtlis;
import graduation.tatev.myapplication.Utils.Utils;
import graduation.tatev.myapplication.components.Animation;
import graduation.tatev.myapplication.components.Container;
import graduation.tatev.myapplication.components.GraphEdge;
import graduation.tatev.myapplication.components.Terminal;
import graduation.tatev.myapplication.components.Truck;
import graduation.tatev.myapplication.events.BaseEvent;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, OnTaskCompleted {

    private static int SIMULATION_DURATION = 3 * 7 * 24 * 60; //three week in minutes
    private static float UNIT = 15; //three week in minutes
    private static float TRUCK_CAPACITY = 2; //three week in minutes
    private static float TIME_SCALE = 7200; // max route duration is aproximatly 5 days = 7200 min -> 1 min animation
    private static int MILLISECOND = 60000;
    private List<BaseEvent> initialEvents = new ArrayList<>();
    private static double[][] shortestDurationsMatrix;
    private static int[][] shortestRoutesMatrix;
    private List<Terminal> terminals;
    private Map<Terminal, List<List<BaseEvent>>> terminalListMap;
    private Date startDate = new Date();
    private List<Truck> truckList;
    private List<Animation> animationList;
    private GoogleMap mMap;
    private List<GraphEdge> graphEdges;
    public List<PolylineOptions> poliLines;
    private Bitmap terminalBitmap, truckBitmap, delayedTruckBitmap;
    private Handler animHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_maps);

        terminalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.color_icons_green_home);
        terminalBitmap = Bitmap.createScaledBitmap(terminalBitmap, 30, 45, true);
        truckBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.truck);
        truckBitmap = Bitmap.createScaledBitmap(truckBitmap, 30, 30, true);
        delayedTruckBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.delayed_truck);
        delayedTruckBitmap = Bitmap.createScaledBitmap(delayedTruckBitmap, 30, 30, true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    terminals = ConnectionService.getTerminalDao().getAllTerminals();

                    shortestDurationsMatrix = new double[terminals.size()][terminals.size()];
                    shortestRoutesMatrix = new int[terminals.size()][terminals.size()];
                } catch (Exception e) {
                    Log.d("ExaptionGetTermianls", e.toString());
                }
                graphEdges = new ArrayList<GraphEdge>();
                Utils.fillAllPairShortestPaths(MapsActivity.this, shortestDurationsMatrix, shortestRoutesMatrix, graphEdges);
                Utils.getEventList(MapsActivity.this, initialEvents, startDate);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                Log.d("onPostExecute", "onPostExecute");
                List<Marker> terminalMarkers = new ArrayList<>();
                for (Terminal terminal : terminals) {
                    terminalMarkers.add(createMarker(BitmapDescriptorFactory.fromBitmap(terminalBitmap), terminal.getLatitude(), terminal.getLongitude(), terminal.getName()));
                }
                drawGraph(terminalMarkers);
                beginSimulation();
            }
        }.execute();
    }

    private void drawGraph(List<Marker> terninalMarkers) {
        RouteUtlis utlis = new RouteUtlis(this);
        utlis.drawRoutes(graphEdges);
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : terninalMarkers) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        int padding = 0; // offset from edges of the map in pixels
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private Marker createMarker(BitmapDescriptor bitmap, double latitude, double longitude, String title) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .icon(bitmap);
        Marker marker = mMap.addMarker(markerOptions);
        marker.showInfoWindow();
        return marker;
    }

    public void beginSimulation() {
        truckList = new ArrayList<>();
        animationList = new ArrayList<>();
        Random random = new Random();
        terminalListMap = new HashMap<>();
        for (Terminal terminal : terminals) {
            List<List<BaseEvent>> terminalEvents = Arrays.asList((List<BaseEvent>[]) new ArrayList[(int) (SIMULATION_DURATION / UNIT)]);
            for (BaseEvent event : initialEvents) {
                if (event.getDepartureTerminal().equals(terminal)) {
                    event.setType(BaseEvent.Type.DEPARTURE);
                    int tripDuration = (int) shortestDurationsMatrix[terminals.indexOf(event.getDepartureTerminal())][terminals.indexOf(event.getDestinationTerminal())];
                    event.getConteiner().setRecoveryTime(new Date(event.getStartTime().getTime() + tripDuration * MILLISECOND));
                    int randomDelay = random.nextInt(60) - 30; // [-30,30] minute delay
                    event.getConteiner().setArrivalTime(new Date(event.getStartTime().getTime() + (tripDuration + randomDelay) * MILLISECOND));
                    long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(event.getStartTime().getTime() - startDate.getTime());
                    int bucketIndex = (int) Math.ceil(diffInMinutes / UNIT);
                    if (terminalEvents.get(bucketIndex) == null) {
                        List<BaseEvent> list = new ArrayList<BaseEvent>();
                        list.add(event);
                        terminalEvents.set(bucketIndex, list);
                    } else {
                        terminalEvents.get(bucketIndex).add(event);
                    }
                }
// else if (event.getDestinationTerminal().equals(terminal)) {
//                    event.setType(BaseEvent.Type.ARRIVAL);
//                    long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(event.getStartTime().getTime() - startDate.getTime());
//                    int bucketIndex = (int) Math.ceil(diffInMinutes / UNIT);
//                    if (terminalEvents.get(bucketIndex) == null) {
//                        List<BaseEvent> list = new ArrayList<BaseEvent>();
//                        list.add(event);
//                        terminalEvents.set(bucketIndex, list);
//                    } else {
//                        terminalEvents.get(bucketIndex).add(event);
//                    }
//                }
            }
            terminalListMap.put(terminal, terminalEvents);
        }
        int currentBucket = 0;
        while (currentBucket < SIMULATION_DURATION) {
            for (Terminal terminal : terminals) {
                List<BaseEvent> currentEvents = terminalListMap.get(terminal).get((int) (currentBucket / UNIT));
                if (currentEvents == null) continue;
                Map<Terminal, List<Container>> nowInTerminal = new HashMap<>();
                List<Truck> trucksFromCurrentTerminal = new ArrayList<>();
                for (BaseEvent event : currentEvents) {
                    // if it's destination terminal just remove event from list, otherwise detect next terminal for current containers
                    // group all containers in terminal by next terminal to understand how many trucks we need to be add in simulation
                    // and schedule next event.
                    if (event.getDestinationTerminal() != terminal) {
                        Terminal nextTerminal = terminals.get(shortestRoutesMatrix[terminals.indexOf(terminal)][terminals.indexOf(event.getDestinationTerminal())]);
                        // count containers in terminal from this event
                        Container passContainer = new Container(event.getConteiner());
                        passContainer.setInitialTerminal(terminal);
                        passContainer.setFinalTerminal(nextTerminal);
                        // group by destination terminals
                        if (nowInTerminal.containsKey(nextTerminal))
                            nowInTerminal.get(nextTerminal).add(passContainer);
                        else {
                            ArrayList<Container> list = new ArrayList<>();
                            list.add(passContainer);
                            nowInTerminal.put(nextTerminal, list);
                        }
                        // scheduling next event
                        BaseEvent next = new BaseEvent(event);
                        next.setDepartureTerminal(nextTerminal);
                        double durationToNextTerminal = shortestDurationsMatrix[terminals.indexOf(terminal)][terminals.indexOf(nextTerminal)];
                        int nextBucketIndex = (int)(currentBucket/UNIT + (int)Math.ceil(durationToNextTerminal/UNIT));
                        if (terminalListMap.get(nextTerminal).get(nextBucketIndex) == null) {
                            List<BaseEvent> list = new ArrayList<BaseEvent>();
                            list.add(next);
                            terminalListMap.get(nextTerminal).set(nextBucketIndex, list);
                        } else {
                            terminalListMap.get(nextTerminal).get(nextBucketIndex).add(next);
                        }
                    }
                    terminalListMap.get(terminal).get((int) (currentBucket / UNIT)).remove(event);

                    //and now for each Terminal I have Terminal -> container1, container2... which need to be in th same "NextTerminal"
                    //so count of containers is enough to decide how many trucks I need to add
                    //but i must reuse trucks which are allready in simaltion and now in Terminal
                    for (Map.Entry<Terminal, List<Container>> entry : nowInTerminal.entrySet()) {
                        int sumOfContainers = 0;
                        int sumOfDelayedContainers = 0;
                        for (Container container : entry.getValue()) {
                            sumOfContainers += container.getSize();
                            if (container.isDelayed())
                                sumOfDelayedContainers += container.getSize();
                        }
                        Animation animation = new Animation();
                        animation.setInitialTerminal(terminal);
                        animation.setFinalTerminal(entry.getKey());
                        animation.setDuration(MILLISECOND * (shortestDurationsMatrix[terminals.indexOf(terminal)][terminals.indexOf(entry.getKey())] / TIME_SCALE));
                        int offset = (int) TimeUnit.MILLISECONDS.toMinutes(nowInTerminal.get(entry.getKey()).get(0).getArrivalTime().getTime() - startDate.getTime());
                        offset = (int) (offset / TIME_SCALE);
                        animation.setOffset(offset * MILLISECOND);
                        if (sumOfDelayedContainers != 0) {
                            animation.setCountOfTrucks((int) Math.ceil(sumOfDelayedContainers / TRUCK_CAPACITY));
                            animation.setIsDelayedAnim(true);
                            animationList.add(animation);
                        }
                        if (sumOfDelayedContainers != sumOfContainers) {
                            animation.setCountOfTrucks((int) Math.ceil(sumOfContainers / TRUCK_CAPACITY) - (int) Math.ceil(sumOfDelayedContainers / TRUCK_CAPACITY));
                            animation.setIsDelayedAnim(false);
                            animationList.add(animation);
                        }

                        Truck truck = new Truck();
                        // as initial and final terminals are the same for all containers
                        // to set truck's arrival can use arrival time of any  container let's 0 as it's exists for sure
                        truck.setArrivalTime(new java.sql.Date(nowInTerminal.get(entry.getKey()).get(0).getArrivalTime().getTime()));
                        truck.setInitialTerminal(terminal);
                        truck.setFinalTerminal(entry.getKey());

                        for (int i = 0; i < Math.ceil(sumOfContainers / TRUCK_CAPACITY); i++)
                            trucksFromCurrentTerminal.add(truck);
                    }
                    // this is list of all trucks we need to send from current terminal
                    // merge this with trucks allready in terminal
                    syncTrucksInTerminal(terminal, trucksFromCurrentTerminal, currentBucket);
                    // remaining trucks add to list
                    truckList.addAll(trucksFromCurrentTerminal);
                }
            }
            Log.d("currentBucket", currentBucket + "");
            currentBucket += UNIT;

        }
        Log.d("truckCount", truckList.size() + "");


    }


    // merged trucks if there are trucks in terminal
    private void syncTrucksInTerminal(Terminal currentTerminal, List<Truck> trucksFromCurrentTerminal, int bucket) {
        int k = trucksFromCurrentTerminal.size() - 1;
        for (int i = 0; i < truckList.size(); i++) {
            if (k != -1) {
                Date arrival = new Date(truckList.get(i).getArrivalTime().getTime());
                long arrivalInMinutes = TimeUnit.MILLISECONDS.toMinutes(arrival.getTime() - startDate.getTime());
                // first condition-> truck's targert is current terminal, second -> it will be in terminal just at the observing time
                if (truckList.get(i).getFinalTerminal().equals(currentTerminal) && (int) Math.ceil(arrivalInMinutes / UNIT) == bucket) {
                    truckList.set(truckList.indexOf(truckList.get(i)), trucksFromCurrentTerminal.get(k));
                    trucksFromCurrentTerminal.remove(trucksFromCurrentTerminal.get(k));
                    k--;
                }
            }
        }
    }


    @Override
    public void onTaskCompleted(List<PolylineOptions> polyLines) {
        Log.d("completed", polyLines.toString());
        this.poliLines = polyLines;
        for (PolylineOptions polylineOptions : polyLines) {
            mMap.addPolyline(polylineOptions);
        }
        showTruckMotion();
    }

    class TruckAnimation implements Runnable {
        private static final float STEP = 100;
        long duration;
        Marker truckMarker;
        List<LatLng> points;
        int currentStep = 0;
        int timeInterval;

        TruckAnimation(GraphEdge edge, int duration, int countOfTrucks, BitmapDescriptor truck) {
            this.duration = duration;
            truckMarker = createMarker(truck, edge.getStartPoint().getLatitude(), edge.getStartPoint().getLongitude(), countOfTrucks + "");
            truckMarker.showInfoWindow();
            points = poliLines.get(graphEdges.indexOf(edge)).getPoints();
            timeInterval = (int) (duration / (points.size() / STEP));
        }

        @Override
        public void run() {
            truckMarker.setPosition(points.get(currentStep));
            currentStep += STEP;
            if (currentStep <= points.size())
                animHandler.postDelayed(this, timeInterval);
            // else
            //   truckMarker.setVisible(false);
        }
    }

    private void showTruckMotion() {
        animHandler = new Handler();
        for (Animation animInfo : animationList) {
            BitmapDescriptor icon = animInfo.isDelayedAnim() ? BitmapDescriptorFactory.fromBitmap(delayedTruckBitmap) : BitmapDescriptorFactory.fromBitmap(truckBitmap);
            TruckAnimation anim = new TruckAnimation(new GraphEdge(animInfo.getInitialTerminal(), animInfo.getFinalTerminal()),
                    (int) animInfo.getDuration(), animInfo.getCountOfTrucks(), icon);
            animHandler.postDelayed(anim, animInfo.getOffset());
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

    }

}
