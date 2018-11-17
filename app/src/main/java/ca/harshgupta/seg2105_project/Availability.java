package ca.harshgupta.seg2105_project;

import java.util.Date;

public class Availability {

    private String date;
    private String timeStart;
    private String timeEnd;
    private int key;
    public Availability (){

    }
    public Availability(int key, String date, String timeStart, String timeEnd) {
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.key = key;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeStart() {
        return timeStart;
    }

    public void setTimeStart(String timeStart) {
        this.timeStart = timeStart;
    }

    public String getTimeEnd() {
        return timeEnd;
    }

    public void setTimeEnd(String timeEnd) {
        this.timeEnd = timeEnd;
    }

    public int getKey() {
        return key;
    }

    public void setKey(int key) {
        this.key = key;
    }


}
