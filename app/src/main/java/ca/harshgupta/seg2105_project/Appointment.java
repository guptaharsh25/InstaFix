package ca.harshgupta.seg2105_project;

import java.sql.Time;
import java.time.LocalTime;

public class Appointment {

   enum DayOfWeek {
       MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY, SATURDAY, SUNDAY;
   }

    private String spID;
    private String clientID;
    private DayOfWeek day;
    private int startTime;
    private int endTime;
    private boolean isOrderCompleted;

    public Appointment(String spID, String clientID, DayOfWeek day, int startTime) {
        this.spID = spID;
        this.clientID = clientID;
        this.day = day;
        this.startTime = startTime;
        this.endTime = startTime + 1;
        this.isOrderCompleted = false;
    }

    public String getSpID() {
        return spID;
    }

    public String getClientID() {
        return clientID;
    }

    public DayOfWeek getDay() {
        return day;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public void setSpID(String spID) {
        this.spID = spID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public void setDay(DayOfWeek day) {
        this.day = day;
    }

    public void setStartTime(int startTime) {
        this.startTime = startTime;
        setEndTime();
    }

    private void setEndTime() {
        this.endTime = startTime + 1;
    }

    public void setOrderCompleted(boolean orderCompleted) {
        isOrderCompleted = orderCompleted;
    }

    public boolean isOrderCompleted() {
        return isOrderCompleted;
    }
}
