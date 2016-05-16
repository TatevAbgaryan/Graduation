package graduation.tatev.myapplication.Utils;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graduation.tatev.myapplication.MapsActivity;
import graduation.tatev.myapplication.components.GraphEdge;

/**
 * Created by Tatka on 4/30/2016.
 */
public class RouteUtlis {
    private OnTaskCompleted listener;

    public RouteUtlis(OnTaskCompleted listener) {
        this.listener = listener;
    }

    List<PolylineOptions> polylines;
    private List<GraphEdge> edges;

    public void drawRoutes(List<GraphEdge> edges) {
        polylines = new ArrayList<>();
        this.edges = edges;
        List<String> urls = new ArrayList<>();
        for (GraphEdge edge : edges) {
            LatLng origin = new LatLng(edge.getStartPoint().getLatitude(), edge.getStartPoint().getLongitude());
            LatLng dest = new LatLng(edge.getEndPoint().getLatitude(), edge.getEndPoint().getLongitude());
            urls.add(getDirectionsUrl(origin, dest));
        }
        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(urls);
    }


    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("Exception download url", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    /**
     * A class to download data from Google Directions URL
     */
    private class DownloadTask extends AsyncTask<List<String>, Void, List<String>> {

        // Downloading data in non-ui thread
        @Override
        protected List<String> doInBackground(List<String>... urls) {

            // For storing data from web service
            List<String> data = new ArrayList<>();
            for (int i = 0; i < urls[0].size(); i++) {
                try {
                    // Fetching the data from web service
                    data.add(downloadUrl(urls[0].get(i)));
                } catch (Exception e) {
                    Log.d("Background Task", e.toString());
                }
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(List<String> result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /**
     * A class to parse the Google Directions in JSON format
     */
    private class ParserTask extends AsyncTask<List<String>, Integer, List<List<List<HashMap<String, String>>>>> {


        // Parsing the data in non-ui thread
        @Override
        protected List<List<List<HashMap<String, String>>>> doInBackground(List<String>... jsonData) {

            JSONObject jObject;
            List<List<List<HashMap<String, String>>>> routes = new ArrayList<>();
            for (int i = 0; i < jsonData[0].size(); i++) {
                try {
                    jObject = new JSONObject(jsonData[0].get(i));
                    DirectionsJSONParser parser = new DirectionsJSONParser();

                    // Starts parsing data
                    routes.add(parser.parse(jObject));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<List<HashMap<String, String>>>> results) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            for (int k = 0; k < results.size(); k++) {
                List<List<HashMap<String, String>>> result = results.get(k);
                // Traversing through all the routes
                for (int i = 0; i < result.size(); i++) {
                    points = new ArrayList<LatLng>();
                    lineOptions = new PolylineOptions();

                    // Fetching i-th route
                    List<HashMap<String, String>> path = result.get(i);

                    // Fetching all the points in i-th route
                    for (int j = 0; j < path.size(); j++) {
                        HashMap<String, String> point = path.get(j);

                        double lat = Double.parseDouble(point.get("lat"));
                        double lng = Double.parseDouble(point.get("lng"));
                        LatLng position = new LatLng(lat, lng);

                        points.add(position);
                    }

                    // Adding all the points in the route to LineOptions
                    lineOptions.addAll(points);
                    lineOptions.width(4);
                    lineOptions.color(Color.parseColor("#5F9F9F"));
                }
                polylines.add(lineOptions);
            }
            listener.onTaskCompleted(polylines);

            // Drawing polyline in the Google Map for the i-th route
            //MapsActivity.poliLines.put(edge, lineOptions);
        }
    }


}
