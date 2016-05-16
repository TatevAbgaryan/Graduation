package graduation.tatev.myapplication.components;

/**
 * Created by Tatka on 4/29/2016.
 */
public class Animation {
    private Terminal initialTerminal;
    private Terminal finalTerminal;
    private int offset;
    private double duration;
    private boolean isDelayedAnim;
    private int countOfTrucks; //to show on the top how many trucks are going at the same time,same terminals

    public int getCountOfTrucks() {
        return countOfTrucks;
    }

    public void setCountOfTrucks(int countOfTrucks) {
        this.countOfTrucks = countOfTrucks;
    }


    public boolean isDelayedAnim() {
        return isDelayedAnim;
    }

    public void setIsDelayedAnim(boolean isDelayedAnim) {
        this.isDelayedAnim = isDelayedAnim;
    }

    public double getDuration() {
        return duration;
    }

    public void setDuration(double duration) {
        this.duration = duration;
    }

    public Terminal getInitialTerminal() {
        return initialTerminal;
    }

    public void setInitialTerminal(Terminal initialTerminal) {
        this.initialTerminal = initialTerminal;
    }

    public Terminal getFinalTerminal() {
        return finalTerminal;
    }

    public void setFinalTerminal(Terminal finalTerminal) {
        this.finalTerminal = finalTerminal;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

}
