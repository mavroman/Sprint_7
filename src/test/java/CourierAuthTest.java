import client.ApiClient;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Courier;
import model.CourierCreds;
import model.CourierLoginResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CourierAuthTest {

    private ApiClient apiClient = new ApiClient();
    private Courier courier;
    private String id;
    private String testLogin;



    @BeforeEach
    public void  setUp() {
        // Создаем уникального курьера для каждого теста
        String timestamp = String.valueOf(System.currentTimeMillis());
        testLogin = "mavr_" + timestamp; // Уникальный логин для каждого запуска
        courier = new Courier(testLogin, "123", "test");
        Response response = apiClient.createCourier(courier);
        assertEquals(201, response.statusCode(), "Не удалось создать тестового курьера");

    }

    @Test
    @DisplayName("Успешная авторизация курьера с обязательными полями")
    public void courierAuthSuccessTest() {
        // Действие Arrange: авторизация
        CourierCreds courierCreds = CourierCreds.credsForm(courier);

        // Act выполняем авторизацию
        Response loginResponse = apiClient.loginCourier(courierCreds);

        // Assert: проверяем результаты
        verifySuccessResponse(loginResponse);
        saveCourierId(loginResponse);
    }

    @Step("Проверка успешного ответа")
    private void verifySuccessResponse(Response response) {
        assertEquals(200, response.statusCode(), "Курьер должен успешно авторизоваться");
        response.then().body("id", notNullValue());
    }

    @Step("Сохранение id курьера")
    private void saveCourierId(Response response) {
        CourierLoginResponse loginData = response.as(CourierLoginResponse.class);
        id = loginData.getId();
        assertNotNull(id, "ID курьера должен быть возвращен");
    }

    // Авторизация без логина
    @Test
    @DisplayName("Авторизация без логина возвращает ошибку")
    public void courierAuthWithoutLoginFailsTest() {
        testAuthFail(null, "123", 400, "Недостаточно данных для входа");
    }

    // Авторизация без пароля (отсутствует обязательное поле)
    @Test
    @DisplayName("Авторизация без пароля возвращает ошибку")
    public void courierAuthWithoutPasswordFailsTest() {
        testAuthFail(testLogin, null, 400, "Недостаточно данных для входа");
    }

    // Авторизация с неверным логином
    @Test
    @DisplayName("Авторизация с неверным логином возвращает ошибку 404")
    public void courierAuthWithWrongLoginFailsTest() {
        testAuthFail("wrong_login", "123", 404, "Учетная запись не найдена");
    }

    // Авторизация с неверным паролем
    @Test
    @DisplayName("Авторизация с неверным паролем возвращает ошибку 404")
    public void courierAuthWithWrongPasswordFailsTest() {
        testAuthFail(testLogin, "wrong_pass", 404, "Учетная запись не найдена");
    }

    @Step("Тестирование неуспешной авторизации")
    private void testAuthFail(String login, String password, int expectedStatus, String expectedMessage) {
        CourierCreds credentials = new CourierCreds(login, password);
        Response response = apiClient.loginCourier(credentials);

        assertEquals(expectedStatus, response.statusCode());
        response.then().body("message", equalTo(expectedMessage));
    }

    @AfterEach
    public void tearDown() {
        apiClient.deleteCourier(id);
    }
}
