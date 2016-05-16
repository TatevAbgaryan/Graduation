package graduation.tatev.myapplication.Utils;

import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;
import java.util.Map;

import graduation.tatev.myapplication.components.GraphEdge;

public interface OnTaskCompleted {
    void onTaskCompleted(List<PolylineOptions> polyLines);
}