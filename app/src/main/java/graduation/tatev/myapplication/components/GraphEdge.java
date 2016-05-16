package graduation.tatev.myapplication.components;

import java.util.Objects;

public class GraphEdge {

    private Terminal startPoint;
    private Terminal endPoint;

    public GraphEdge(Terminal t1, Terminal t2) {
        startPoint = t1;
        endPoint = t2;
    }

    public Terminal getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Terminal startPoint) {
        this.startPoint = startPoint;
    }

    public Terminal getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(Terminal endPoint) {
        this.endPoint = endPoint;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof GraphEdge)) return false;

        if (!this.startPoint.equals(((GraphEdge) obj).startPoint) && !this.startPoint.equals(((GraphEdge) obj).endPoint))
            return false;
        if (!this.endPoint.equals(((GraphEdge) obj).endPoint) && !this.endPoint.equals(((GraphEdge) obj).startPoint))
            return false;
        return true;
    }
    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + endPoint.hashCode();
        result = 31 * result + startPoint.hashCode();
        return result;
       // return Objects.hashCode(this);
    }
}