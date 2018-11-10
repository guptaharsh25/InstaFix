package ca.harshgupta.seg2105_project.user_data_packets;

public class Service {
    String name;
    Double rate;
    String id;

    public Service (){

    }

    public Service(String id, String name, double rate) {
        this.name = name;
        this.rate = rate;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
