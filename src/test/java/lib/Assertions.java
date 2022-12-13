package lib;

import io.restassured.response.Response;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.not;
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

    public static void assertJsonByName(Response Response, String name, String expectedValue) {
        // на вход получаем ответ сервера и вытягиваем из него текст
        Response.then().assertThat().body("$", hasKey(name));

        // получим имя
        String value = Response.jsonPath().getString(name);
        // сравниваем ожидаемое значение и от сервера
        assertEquals(expectedValue, value, "JSON value is not equal to expected value");
    }

    // сравниваем текст ответа
    public static void assertResponseTextEquals(Response Response, String expectedAnswer) {
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Response text is not as expected"
        );
    }

    // сравниваем код ответа
    public static void assertResponseCodeEquals(Response Response, int expectedStatusCode) {
        assertEquals(
                expectedStatusCode,
                Response.statusCode(),
                "Response status code is not as expected"
        );
    }

    public static void assertJsonHasField(Response Response, String expectedFieldName) {
        Response.then().assertThat().body("$", hasKey(expectedFieldName));
    }

    // проверяем, что все поля имеют значения через цикл for
    public static void assertJsonHasFields(Response Response, String[] expectedFieldNames) {
        for (String expectedFieldName : expectedFieldNames) {
            Assertions.assertJsonHasField(Response, expectedFieldName);
        }
    }

    public static void assertJsonHasNotField(Response Response, String unexpectedFieldName) {
        Response.then().assertThat().body("$", not(hasKey(unexpectedFieldName)));
    }
    // сравниваем текст ответа
    public static void assertResponseTextEqualsForUserNotFound(Response Response, String expectedAnswer) {
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "User not found"
        );
    }
    // сравниваем текст ответа
    public static void assertResponseTextNotEquals(Response Response, String expectedAnswer) {
        assertEquals(
                expectedAnswer,
                Response.asString(),
                "Auth token not supplied"
        );
    }
    public static void assertJsonByNameWithDifferentAuth(Response Response, String expectedAnswer) {
        assertEquals(expectedAnswer,
                Response.asString(),
                "\"username\": \"learnqa\"");
    }

}

