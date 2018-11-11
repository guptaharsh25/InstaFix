package ca.harshgupta.seg2105_project;

public class Service {
    private String id;
    private String name;
    private double rate;

    public Service(){}

    public Service(String name, double rate){
        this.name = name;
        this.rate = rate;
    }
    public Service(String id, String name, double rate){
        this.id = id;
        this.name = name;
        this.rate = rate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

}
