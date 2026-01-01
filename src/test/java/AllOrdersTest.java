import client.ApiOrder;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.hamcrest.core.IsInstanceOf.instanceOf;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class AllOrdersTest {
    private ApiOrder apiOrder = new ApiOrder();

    @Test
    @DisplayName("GET /api/v1/orders возвращает успешный ответ со списком заказов")
    public void getAllOrdersReturnsSuccess() {
        Response response = executeGetAllOrderRequest();

        verifyResponseSuccess(response);
    }

    @Step("Выполнение запроса GET /api/v1/orders")
    private Response executeGetAllOrderRequest() {
        return apiOrder.getAllOrders();
    }

    @Step("Проверка успешного ответа")
    private void verifyResponseSuccess(Response response) {
        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("$", hasKey("orders"))
                .body("orders", notNullValue())
                .body("orders", instanceOf(ArrayList.class))
                .body("$", hasKey("pageInfo"));
    }

}
