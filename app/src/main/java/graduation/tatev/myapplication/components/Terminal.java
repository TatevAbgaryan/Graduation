package graduation.tatev.myapplication.components;

/**
 * Created by tatev on 02.02.2016.
 */
public class Terminal {

    private int terminalID;
    private float latitude;
    private float longitude;
    private int duration;
    private String name;

    public int getTerminalID() {
        return terminalID;
    }

    public void setTerminalID(int terminalID) {
        this.terminalID = terminalID;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Terminal)) return false;

        if (!this.name.equals(((Terminal) obj).name))
            return false;
        if (this.latitude != ((Terminal) obj).latitude)
            return false;
        if (this.longitude != ((Terminal) obj).longitude)
            return false;
        if (this.terminalID != ((Terminal) obj).terminalID)
            return false;
        return true;
    }

}
