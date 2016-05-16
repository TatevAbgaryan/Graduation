package graduation.tatev.myapplication.events;

import graduation.tatev.myapplication.components.Container;
import graduation.tatev.myapplication.components.Terminal;

import java.util.Date;

/**
 * Created by Tatka on 4/21/2016.
 */
public class BaseEvent {
    public static enum Type {ARRIVAL, DEPARTURE}

    private Date startTime;
    private Terminal departureTerminal;
    private Container conteiner;
    private Terminal destinationTerminal;
    private Type type;

    public BaseEvent() {
    }

    public BaseEvent(BaseEvent event) {
        this.startTime = event.getStartTime();
        this.departureTerminal = event.getDepartureTerminal();
        this.conteiner = event.getConteiner();
        this.destinationTerminal = event.getDestinationTerminal();
        this.type = event.getType();
    }

    public Container getConteiner() {
        return conteiner;
    }

    public void setConteiner(Container conteiner) {
        this.conteiner = conteiner;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public Terminal getDepartureTerminal() {
        return departureTerminal;
    }

    public void setDepartureTerminal(Terminal departureTerminal) {
        this.departureTerminal = departureTerminal;
    }

    public Terminal getDestinationTerminal() {
        return destinationTerminal;
    }

    public void setDestinationTerminal(Terminal destinationTerminal) {
        this.destinationTerminal = destinationTerminal;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

}
