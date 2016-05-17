package graduation.tatev.myapplication;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import graduation.tatev.myapplication.components.Container;
import graduation.tatev.myapplication.components.Terminal;
import graduation.tatev.myapplication.components.Truck;
import graduation.tatev.myapplication.events.BaseEvent;

public class ActivityDES extends AppCompatActivity implements OnMapReadyCallback {
    private static int SIMULATION_DURATION = 3 * 7 * 24 * 60; //three week in minutes
    private static int UNIT = 15; //three week in minutes
    private static int TRUCK_CAPACITY = 2; //three week in minutes
    private List<BaseEvent> initialEvents = new ArrayList<>();
    private static double[][] shortestDurationsMatrix;
    private static int[][] shortestRoutesMatrix;
    private List<Terminal> terminals;
    private Map<Terminal, List<List<BaseEvent>>> terminalListMap;
    private Date startDate = new Date();
    private List<Truck> truckList;
    private GoogleMap mMap;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //setContentView(R.layout.activity_main);
        GoogleMap mMap = null;
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
              //  Utils.fillAllPairShortestPaths(ActivityDES.this, shortestDurationsMatrix, shortestRoutesMatrix,);
                Utils.getEventList(ActivityDES.this, initialEvents, startDate);

                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                Log.d("onPostExecute", "onPostExecute");
                beginSimulation();
            }
        }.execute();


    }

    public void beginSimulation() {
        truckList = new ArrayList<>();
        Random random = new Random();
        terminalListMap = new HashMap<>();
        for (Terminal terminal : terminals) {
            List<List<BaseEvent>> terminalEvents = Arrays.asList((List<BaseEvent>[]) new ArrayList[SIMULATION_DURATION / UNIT]);
            for (BaseEvent event : initialEvents) {
                if (event.getDepartureTerminal().equals(terminal)) {
                    event.setType(BaseEvent.Type.DEPARTURE);
                    int tripDuration = (int) shortestDurationsMatrix[terminals.indexOf(event.getDepartureTerminal())][terminals.indexOf(event.getDestinationTerminal())];
                    event.getConteiner().setRecoveryTime(new Date(event.getStartTime().getTime() + tripDuration * 1000));
                    int randomDelay = random.nextInt(60) - 30; // [-30,30] minute delay
                    event.getConteiner().setArrivalTime(new Date(event.getStartTime().getTime() + (tripDuration + randomDelay) * 1000));
                    long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(event.getStartTime().getTime() - startDate.getTime());
                    int bucketIndex = (int) Math.ceil(diffInMinutes / UNIT);
                    if (terminalEvents.get(bucketIndex) == null) {
                        List<BaseEvent> list = new ArrayList<BaseEvent>();
                        list.add(event);
                        terminalEvents.set(bucketIndex, list);
                    } else {
                        terminalEvents.get(bucketIndex).add(event);
                    }
                } else if (event.getDestinationTerminal().equals(terminal)) {
                    event.setType(BaseEvent.Type.ARRIVAL);
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
            }
            terminalListMap.put(terminal, terminalEvents);
        }
        int currentBucket = 0;
        while (currentBucket < SIMULATION_DURATION) {
            for (Terminal terminal : terminals) {
                List<BaseEvent> currentEvents = terminalListMap.get(terminal).get(currentBucket / UNIT);
                Map<Terminal, List<Container>> nowInTerminal = new HashMap<>();
                List<Truck> trucksFromCurrentTerminal = new ArrayList<>();
                if (currentEvents == null) continue;
                for (BaseEvent event : currentEvents) {
                    if (event.getDestinationTerminal() != terminal) {
                        Terminal nextTerminal = terminals.get(shortestRoutesMatrix[terminals.indexOf(terminal)][terminals.indexOf(event.getDestinationTerminal())]);
                        Container passContainer = new Container(event.getConteiner());
                        passContainer.setInitialTerminal(terminal);
                        passContainer.setFinalTerminal(nextTerminal);
                        if (nowInTerminal.containsKey(nextTerminal))
                            nowInTerminal.get(nextTerminal).add(passContainer);
                        else {
                            ArrayList<Container> list = new ArrayList<>();
                            list.add(passContainer);
                            nowInTerminal.put(nextTerminal, list);
                        }
                        // schedule next event
                        BaseEvent next = new BaseEvent(event);
                        next.setDestinationTerminal(nextTerminal);
                        double durationToNextTerminal = shortestDurationsMatrix[terminals.indexOf(terminal)][terminals.indexOf(nextTerminal)];
                        int nextBucketIndex = (int) Math.ceil((((currentBucket + 1) * UNIT + durationToNextTerminal)) / UNIT) - 1;
                        //  terminalListMap.get(nextTerminal).get(nextBucketIndex).add(next);
                    }
                    terminalListMap.get(terminal).get(currentBucket / UNIT).remove(event);
                }
                for (Map.Entry<Terminal, List<Container>> entry : nowInTerminal.entrySet()) {
                    for (Container container : entry.getValue()) {
                        Truck truck = new Truck();
                        truck.setRecoveryTime(new java.sql.Date(container.getRecoveryTime().getTime()));
                        truck.setArrivalTime(new java.sql.Date(container.getArrivalTime().getTime()));
                        truck.setInitialTerminal(container.getInitialTerminal());
                        truck.setFinalTerminal(container.getFinalTerminal());
                        truck.setCoontainerCount(TRUCK_CAPACITY);
                        trucksFromCurrentTerminal.addAll(new ArrayList<Truck>(Collections.nCopies((int) Math.floor(container.getSize() / (float) TRUCK_CAPACITY), truck)));
                        if (container.getSize() % TRUCK_CAPACITY != 0) {
                            truck.setCoontainerCount(1);
                            trucksFromCurrentTerminal.add(truck);
                        }
                    }
                }
                truckList.addAll(trucksFromCurrentTerminal);
            }
            currentBucket += UNIT;
        }
    }

//    public void collectContainersByDestination(List<Container> existingContainers, Container newContainer) {
//        for (Container container : existingContainers) {
//            if (container.getInitialTerminal().equals(newContainer.getInitialTerminal()))
//                container.setSize(container.getSize() + newContainer.getSize());
//            return;
//        }
//        existingContainers.add(newContainer);
//    }

    public static double[][] getShortestDurationsMatrix() {
        return shortestDurationsMatrix;
    }

    public static int[][] getShortestRoutesMatrix() {
        return shortestRoutesMatrix;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

// Add a marker in Sydney, Australia, and move the camera.
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
