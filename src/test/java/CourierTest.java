import client.ApiClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Courier;
import model.CourierLoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static model.CourierCreds.credsForm;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.*;

public class CourierTest {

    private ApiClient apiClient = new ApiClient();
    private String id;

    @Test
    @DisplayName("Проверка успешного создания курьера")
    @Step("Тест создания курьера")
    public void createCourierTest() {

        Courier courier = new Courier("mavr2", "123", "roma");

        Response response = apiClient.createCourier(courier);

        assertEquals(201, response.statusCode(), "Неверный код ответа при создании курьера");

        response.then()
                .body("ok", equalTo(true));

        Response loginResponse = apiClient.loginCourier(credsForm(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();

    }


    @Test
    @DisplayName("Проверка на невозможность создать двух одинаковых курьеров")
    @Step("Тест создания дубликата курьера")
    public void createDoubleCourierFailTest() {
        Courier courier = new Courier("mavrom1", "123", "roma");

        Response firstResponse = apiClient.createCourier(courier);

        assertEquals(201, firstResponse.statusCode(), "Неверный код ответа при создании курьера");

        Response secondResponse = apiClient.createCourier(courier);

        assertEquals(409, secondResponse.statusCode(), "Неверный код ответа при создании двух одинаковых курьеров");

        secondResponse.then()
                .body("message", equalTo("Этот логин уже используется. Попробуйте другой."));

        Response loginResponse = apiClient.loginCourier(credsForm(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();

    }


    @Test
    @DisplayName("Проверка создания курьера с обязательными полями")
    @Step("Тест создания курьера с обязательными полями")
    public void createCourierWithRequiredFieldsTest() {
        Courier courier = new Courier("mavr3", "123", null);

        Response response = apiClient.createCourier(courier);

        assertEquals(201, response.statusCode(), "Курьер со всеми обязательными полями должен создаваться успешно");

        Response loginResponse = apiClient.loginCourier(credsForm(courier));
        id = loginResponse.as(CourierLoginResponse.class).getId();
    }

    @Test
    @DisplayName("Проверка создания курьера без обязательного поля 'login")
    @Step("Тест создания курьера без логина")
    public void createCourierWithoutLogin() {
        Courier courier = new Courier(null, "123", "roma");
        Response response = apiClient.createCourier(courier);
        assertEquals(400, response.statusCode(), "Курьер без обязательного поля 'login' не должен создаваться успешно");
        response.then()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));

    }

    @Test
    @DisplayName("Проверка создания курьера без обязательного поля 'password")
    @Step("Тест создания курьера без пароля")
    public void createCourierWithoutPassword() {
        Courier courier = new Courier("mavr", null, "roma");
        Response response = apiClient.createCourier(courier);
        assertEquals(400, response.statusCode(), "Курьер без обязательного поля 'password' не должен создаваться успешно");
        response.then()
                .body("message", equalTo("Недостаточно данных для создания учетной записи"));
    }


    @AfterEach
    public void tearDown() {
        apiClient.deleteCourier(id);
    }


}
