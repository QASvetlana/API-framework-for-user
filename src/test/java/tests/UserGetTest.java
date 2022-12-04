package tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserGetTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Description("Unauthorized request for data - we received only username")
    @DisplayName("Test negative log user")
    @Test
    public void testGetUserDataNotAuth() {
        Response responseUserData = apiCoreRequests
                .makeGetRequestWithoutCookieAndToken("https://playground.learnqa.ru/api/user/2");
        // проверяем, что в неавторизованном запросе придет только имя
        Assertions.assertJsonHasField(responseUserData, "username");
        Assertions.assertJsonHasNotField(responseUserData, "firstName");
        Assertions.assertJsonHasNotField(responseUserData, "lastName");
        Assertions.assertJsonHasNotField(responseUserData, "email");
    }

    @Description("Authorized request - were authorized by a user with ID 2 and made a request to get the data of the same user")
    @DisplayName("Test positive log user")
    @Test
    public void testGetUserDetailsAuthAsSameUser() {
        Map<String, String> authData = new HashMap<>();
        //определили почту и пароль
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // залогинились
        Response responseGetAuth = apiCoreRequests
                .makePostRequestCreateUserWithShortName("https://playground.learnqa.ru/api/user/login", authData);

        // вынимаем из полученного запроса хедер и куки
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        // полученные куки и хедер подставляем в запрос, чтобы пользователь был авторизован
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/2", header, cookie);

        // проверяем наличие нужных нам полей в ответе
        //   String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertJsonHasField(responseUserData, "email");
    }

    @Description("Logged in by one user, but try to get the data of another user")
    @DisplayName("Test negative log user")
    @Test
    public void testGetUserDetailsAuthAsAnotherUser() {
        Map<String, String> authData = new HashMap<>();
        //определили почту и пароль
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        // залогинились
        Response responseGetAuth = apiCoreRequests
                .makePostRequestCreateUserWithShortName("https://playground.learnqa.ru/api/user/login", authData);

        // вынимаем из полученного запроса хедер и куки
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");
        // авторизовались одним пользователем, но получаем данные другого (т.е. с другим ID)
        Response responseUserData = apiCoreRequests
                .makeGetRequest("https://playground.learnqa.ru/api/user/3", header, cookie);

        // проверяем наличие нужных нам полей в ответе
        //   String[] expectedFields = {"username", "firstName", "lastName", "email"};
        Assertions.assertResponseTextEqualsForUserNotFound(responseUserData, "User not found");
    }
}
