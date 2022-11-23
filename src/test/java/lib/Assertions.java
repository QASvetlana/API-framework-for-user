package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;

// все проверки вынесли в отдельный класс
public class Assertions {
    public static void assertJsonByName(Response Response, String name, int expectedValue) {
        // на вход получаем ответ сервера и вытягиваем из него текст
        Response.then().assertThat().body("$", hasKey(name));

        // получем имя
        int value = Response.jsonPath().getInt(name);
        // сравниваем ожидаемое значение и от сервера
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");


    }
}
