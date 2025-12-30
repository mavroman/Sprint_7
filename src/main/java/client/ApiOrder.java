package client;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import model.Order;

import static io.restassured.RestAssured.given;

public class ApiOrder {

    public ApiOrder() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru/";
    }

    // Создание нового заказа
    public Response createOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .body(order)
                .when()
                .post("/api/v1/orders");
    }

    // Получение заказа по track
    public Response getOrderByTrack(int track) {
        return given()
                .queryParam("t", track)
                .when()
                .get("/api/v1/orders/track");
    }

    // Отмена заказа по track
    public Response cancelOrder(int track) {
        return given()
                .queryParam("track", track)
                .when()
                .put("/api/v1/orders/cancel");
    }

    // Получение списка всех заказов
    public Response getAllOrders() {
        return given()
                .when()
                .get("/api/v1/orders");
    }

}
