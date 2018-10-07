package ca.harshgupta.seg2105_project.user_data_packets;


public class SignInData {
    private String username;
    private String password;

    public SignInData(String username, String password){
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
