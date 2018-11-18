package ca.harshgupta.seg2105_project;

import java.util.Date;

public class Availability {

    private String date;
    private String timeStart;
    private String timeEnd;
    private int key;

    private double startTimeDouble;
    private double endTimeDouble;
    public Availability (){

    }
    public Availability(String date, String timeStart, String timeEnd) {
        this.date = date;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
    }



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
        if (this.getDate().equals("Sunday"))
            this.key = 0;
        else if (this.getDate().equals("Monday"))
            this.key = 1;
        else if (this.getDate().equals("Tuesday"))
            this.key = 2;
        else if (this.getDate().equals("Wednesday"))
            this.key = 3;
        else if (this.getDate().equals("Thursday"))
            this.key = 4;
        else if (this.getDate().equals("Friday"))
            this.key = 5;
        else if (this.getDate().equals("Saturday"))
            this.key = 6;

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

    public void setStartTimeDouble(double value){this.startTimeDouble = value; }
    public void setEndTimeDouble(double value){this.endTimeDouble = value; }

    public double getStartTimeDouble(){return startTimeDouble;}
    public double getEndTimeDouble(){return endTimeDouble;}

}
