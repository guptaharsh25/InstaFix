package ca.harshgupta.seg2105_project;

public class Review {
    private int rate;
    private String review;

    public Review (int rate, String review){
        this.rate = rate;
        this.review = review;
    }

    public int getRate (){
        return rate;
    }

    public String getReview (){
        return review;
    }

}
