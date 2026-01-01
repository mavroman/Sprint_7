import client.ApiOrder;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import model.Order;
import model.OrderResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class OrderCreateTest {

    private ApiOrder apiOrder = new ApiOrder();
    private List<Integer> createdOrderTracks = new ArrayList<>();
    private int track;

    // Базовые тестовые данные
    private static final String FIRST_NAME = "Рома";
    private static final String LAST_NAME = "Ма";
    private static final String ADDRESS = "Пушкина 1";
    private static final int METRO_STATION = 2;
    private static final String PHONE = "+7 800 355 35 35";
    private static final int RENT_TIME = 5;
    private static final String COMMENT = "Роллы + суши";

    // Генерируем дату доставки на завтра
    private static String getDeliveryDate() {
        return LocalDate.now().plusDays(1)
                .format(DateTimeFormatter.ISO_DATE);
    }

    // Метод для создания базового заказа
    private Order createBaseOrder() {
        return createBaseOrder(null);
    }

    private Order createBaseOrder(List<String> colors) {
        Order order = new Order();
        order.setFirstName(FIRST_NAME);
        order.setLastName(LAST_NAME);
        order.setAddress(ADDRESS);
        order.setMetroStation(METRO_STATION);
        order.setPhone(PHONE);
        order.setRentTime(RENT_TIME);
        order.setDeliveryDate(getDeliveryDate());
        order.setComment(COMMENT);
        order.setColor(colors);
        return order;
    }

    // Параметризация для тестирования цветов — BLACK / GREY
    static Stream<Arguments> colorVariantsBackAndGrey() {
        return Stream.of(
                arguments("Один цвет BLACK", Arrays.asList("BLACK")), // описание и список с одним цветом
                arguments("Один цвет GREY", Arrays.asList("GREY")),
                arguments("Оба цвета", Arrays.asList("BLACK", "GREY")),
                arguments("Без указания цвета", null),
                arguments("Пустой массив цветов", Collections.emptyList())
        );
    }

    @ParameterizedTest(name = "Создание заказа: {0}")
    @MethodSource("colorVariantsBackAndGrey")
    @DisplayName("Создание заказа с разными вариантами цвета")
    public void createOrderWithColorVariantsBackAndGrey(String testCaseName, List<String> colors) {
        // Arrange создаем заказ с указанными цветами
        Order order = createBaseOrder(colors);

        // Act отправляем запрос на создание заказа
        Response response = apiOrder.createOrder(order);

        // Assert
        verifyCreationResponse(response, testCaseName);

        saveOrderTrack(response);

        System.out.println("Создан заказ с track: " + track + " (вариант: " + testCaseName + ")");
    }

    @Step("Проверка ответа создания заказа: {testCaseName}")
    private void verifyCreationResponse(Response response, String testCaseName) {
        assertEquals(201, response.statusCode(), "Статус 201 для: " + testCaseName);
        response.then().body("track", notNullValue());
    }

    @Step("Сохранение track заказа")
    private void saveOrderTrack(Response response) {
        OrderResponse orderResponse = response.as(OrderResponse.class);
        track = orderResponse.getTrack();
        createdOrderTracks.add(track);
    }

    @AfterEach
    public void tearDown() {
        for (Integer track : createdOrderTracks) {
            apiOrder.cancelOrder(track);
        }
    }

}
