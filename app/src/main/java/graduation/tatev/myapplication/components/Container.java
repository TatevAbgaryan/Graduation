package graduation.tatev.myapplication.components;

import java.util.Date;

/**
 * Created by Tatka on 4/24/2016.
 */
public class Container {

    private Terminal initialTerminal;
    private Terminal finalTerminal;
    private Date recoveryTime;
    private Date arrivalTime;
    private int size;

    public Container() {
    }
    public Container(Container container){
        this.initialTerminal = container.getInitialTerminal();
        this.finalTerminal = container.getFinalTerminal();
        this.recoveryTime = container.getRecoveryTime();
        this.arrivalTime = container.getArrivalTime();
        this.size = container.getSize();
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

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }


    public Date getRecoveryTime() {
        return recoveryTime;
    }

    public void setRecoveryTime(Date recoveryTime) {
        this.recoveryTime = recoveryTime;
    }

    public Date getArrivalTime() {
        return arrivalTime;
    }

    public void setArrivalTime(Date arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public boolean isDelayed() {
        return recoveryTime.before(arrivalTime);
    }
}
