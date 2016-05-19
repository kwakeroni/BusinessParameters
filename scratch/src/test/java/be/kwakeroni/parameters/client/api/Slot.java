package be.kwakeroni.parameters.client.api;

/**
 * (C) 2016 Maarten Van Puymbroeck
 */
public class Slot {

    private final int hour;
    private final boolean halfPast;

    private Slot(int hour, boolean halfPast){
        this.hour = hour;
        this.halfPast = halfPast;
    }

    public static Slot atHour(int hour){
        return new Slot(hour, false);
    }

    public static Slot atHalfPast(int hour){
        return new Slot(hour, true);
    }

    public String toString(){
        return hour + ((halfPast)? ".5" : ".0");
    }
}
