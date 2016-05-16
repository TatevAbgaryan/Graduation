package graduation.tatev.myapplication.Utils;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import graduation.tatev.myapplication.ConnectionService;
import graduation.tatev.myapplication.components.Container;
import graduation.tatev.myapplication.components.GraphEdge;
import graduation.tatev.myapplication.components.Terminal;
import graduation.tatev.myapplication.dao.TerminalDao;
import graduation.tatev.myapplication.events.BaseEvent;

/**
 * Created by Tatka on 4/16/2016.
 */
public class Utils {
    private static int MID_SPEED = 80;

    public static void getEventList(Context context, List<BaseEvent> eventList, Date simulationStartDate) {
        TerminalDao terminalDao = ConnectionService.getTerminalDao();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
            JSONArray containers = obj.getJSONArray("containers");
            for (int i = 0; i < containers.length(); i++) {
                JSONObject jsonobject = containers.getJSONObject(i);
                BaseEvent event = new BaseEvent();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.ENGLISH);
                Date date = simpleDateFormat.parse(jsonobject.getString("startTime"));
                if (i == 0 || date.before(simulationStartDate))
                    simulationStartDate.setTime(date.getTime());
                event.setStartTime(date);
                event.setDepartureTerminal(terminalDao.getTerminalByName(jsonobject.getString("initialTermianl")));
                event.setDestinationTerminal(terminalDao.getTerminalByName(jsonobject.getString("destination")));
                Container container = new Container();
                container.setSize(Integer.parseInt(jsonobject.getString("quantity")));
                event.setConteiner(container);
                eventList.add(event);
            }
        } catch (Exception e) {
            Log.d("exaptionParsinGraph", e.toString());
        }
    }

    public static void fillAllPairShortestPaths(Context context, double[][] shortesDurations, int[][] shortestRoutes,List<GraphEdge> graphEdges) {
        Connection connection = ConnectionService.getConnection();
        TerminalDao terminalDao = ConnectionService.getTerminalDao();
        try {
            JSONObject obj = new JSONObject(loadJSONFromAsset(context));
            final JSONArray graph = obj.getJSONArray("graphEdges");

            try {
                for (int i = 0; i < graph.length(); i++) {
                    JSONObject object = graph.getJSONObject(i);
                    GraphEdge edge = new GraphEdge(terminalDao.getTerminalByName(object.getString("source")), terminalDao.getTerminalByName(object.getString("target")));
                    graphEdges.add(edge);
                }
                final List<Terminal> terminals = terminalDao.getAllTerminals();

                // fill matrixes with initial values
                for (int i = 0; i < shortestRoutes.length; i++)
                    for (int j = 0; j < shortestRoutes.length; j++) {
                        shortestRoutes[i][j] = j;
                        GraphEdge graphEdge = new GraphEdge(terminals.get(i), terminals.get(j));
                        if (i == j) {
                            shortesDurations[i][j] = 0;
                        } else if (graphEdges.contains(graphEdge)) {
                            CallableStatement callableStatement =
                                    connection.prepareCall("{? = call dbo.getDistance(?,?)}");
                            callableStatement.setInt(2, terminals.get(i).getTerminalID());
                            callableStatement.setInt(3, terminals.get(j).getTerminalID());
                            callableStatement.registerOutParameter(1, Types.INTEGER);
                            callableStatement.execute();

                            shortesDurations[i][j] = (callableStatement.getInt(1) / MID_SPEED) * 60   // TODO: 4/24/2016 hour
                                    + terminals.get(i).getDuration() + terminals.get(j).getDuration(); // minutes
                        } else {
                            shortesDurations[i][j] = Double.POSITIVE_INFINITY;
                        }

                    }

            } catch (Exception e) {
                Log.d("exaptionParsinGraph", e.toString());

            }

        } catch (Exception e) {
            Log.d("jsonExaption", e.toString());
        }

        // all pair shortest paths floyd-Warshal

//        shortesDurations = new double[][]{{0, 11, 30, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY},
//                {11, 0, 41, 12, 2},
//                {30, 41, 0, 19, Double.POSITIVE_INFINITY},
//                {Double.POSITIVE_INFINITY, 12, 19, 0, 11},{Double.POSITIVE_INFINITY,2,Double.POSITIVE_INFINITY,11,9}};
//        shortestRoutes = new int[][]{{0, 1, 2, 3, 4}, {0, 1, 2, 3, 4}, {0, 1, 2, 3, 4}, {0, 1, 2, 3, 4}, {0, 1, 2, 3, 4}};
        for (int i = 0; i < shortesDurations.length; i++) {
            // compute shortest paths using only 0, 1, ..., i as intermediate vertices
            for (int v = 0; v < shortesDurations.length; v++) {
                for (int w = 0; w < shortesDurations.length; w++) {
                    if (shortesDurations[v][w] > shortesDurations[v][i] + shortesDurations[i][w]) {
                        shortesDurations[v][w] = shortesDurations[v][i] + shortesDurations[i][w];
                        shortestRoutes[v][w] = shortestRoutes[v][i];
                    }
                }
            }
        }
    }


    public static String loadJSONFromAsset(Context context) {
        String json = null;
        try {
            InputStream is = context.getAssets().open("containers.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
