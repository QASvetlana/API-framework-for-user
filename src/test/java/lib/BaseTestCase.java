package lib;

import io.restassured.http.Headers;
import io.restassured.response.Response;

import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertTrue;

// повторы переносим в отдельный класс и будем использовать в тестах
public class BaseTestCase {

    protected String getHeader(Response Response, String name) {
        // в метод передаем header
        Headers headers = Response.getHeaders();
        // убеждаемся, что в соответствующие поля пришли значения
        assertTrue(headers.hasHeaderWithName(name), "Response doesn't have header with name" + name);
        // если значения есть, то мы их возвращаем, если значений нет, то тест будет падать
        return headers.getValue(name);

    }

    protected String getCookie(Response Response, String name) {
        Map<String, String> cookies = Response.getCookies();

        assertTrue(cookies.containsKey(name), "Response doesn't have cookie with name" + name);
        return cookies.get(name);

    }

    protected int getIntFromJson(Response Response, String name) {
        Response.then().assertThat().body("$", hasKey(name));
        return Response.jsonPath().getInt(name);
    }
}
