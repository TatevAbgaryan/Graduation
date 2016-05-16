package graduation.tatev.myapplication.components;


import java.sql.Date;

/**
 * Created by tatev on 02.02.2016.
 */
public class Truck {

    private int truckID;
    private int speed;
    private Date startTime;
    private Date recoveryTime;
    private Date arrivalTime;
    private Terminal initialTerminal;
    private Terminal finalTerminal;
    private int coontainerCount;

    public Truck(){

    }
    public Truck(Truck truck){
        this.truckID = truck.truckID;
        this.speed = truck.speed;
        this.startTime = truck.startTime;
        this.recoveryTime = truck.recoveryTime;
        this.arrivalTime = truck.arrivalTime;
        this.arrivalTime = truck.arrivalTime;
        this.finalTerminal = truck.finalTerminal;
        this.coontainerCount = truck.coontainerCount;
    }
    public int getCoontainerCount() {
        return coontainerCount;
    }

    public void setCoontainerCount(int coontainerCount) {
        this.coontainerCount = coontainerCount;
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
    public int getTruckID() {
        return truckID;
    }

    public void setTruckID(int truckID) {
        this.truckID = truckID;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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

}
