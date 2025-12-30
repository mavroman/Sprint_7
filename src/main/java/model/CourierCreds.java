package model;

public class CourierCreds {

    private String login;
    private String password;

    public CourierCreds(String login, String password) {
        this.login = login;
        this.password = password;
    }

    public static CourierCreds credsForm(Courier courier) {
        return new CourierCreds(courier.getLogin(), courier.getPassword());
    }
}
