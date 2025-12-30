package client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCreds;

import static io.restassured.RestAssured.given;

public class ApiClient {

    public ApiClient() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    // создаем курьера
    public Response createCourier(Courier courier) {
        return given()
                .header("Content-type", "application/json") // заполняем header
                .body(courier) // передаем тело запроса
                .when()
                .post("api/v1/courier");
    }

    // логин курьера в системе
    public Response loginCourier(CourierCreds courierCreds) {
        return given()
                .header("Content-type", "application/json") // заполни header
                .body(courierCreds) // передаем тело запроса
                .when()
                .post("api/v1/courier/login");
    }

    // удаляем курьера
    public Response deleteCourier(String id) {
        return given()
                .header("Content-type", "application/json")
                .when()
                .delete("api/v1/courier" + "/" + id);
    }

}
