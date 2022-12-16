package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.ApiCoreRequests;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;


public class UserDeleteTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Epic("Удаление пользователей")
    @DisplayName("Удаление созданного пользователя")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("example.com")
    public void deleteJustCreatedTest() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestUser("https://playground.learnqa.ru/api/user/", userId, header, cookie);


        Response responseUserData = apiCoreRequests
                .makeGetRequestUserData("https://playground.learnqa.ru/api/user/", userId, header, cookie);
        Assertions.assertResponseTextEquals(responseUserData, "User not found");
    }

    @Test
    @Epic("Удаление пользователей")
    @DisplayName("Удаление пользователя, авторизованным другим пользователем")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("example.com")
    public void deleteAnotherUserTest() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.getString("id");

        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth2 = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/api/user/", userData2);

        String userId2 = responseCreateAuth2.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestUser("https://playground.learnqa.ru/api/user/", userId2, header, cookie);

        Response responseUserData = apiCoreRequests
                .makeGetRequestUserData("https://playground.learnqa.ru/api/user/", userId2, header, cookie);
        Assertions.assertJsonByName(responseUserData, "username", "learnqa");

    }

    @Test
    @Epic("Удаление пользователей")
    @DisplayName("Удаление пользователя по ID 2")
    @Severity(SeverityLevel.CRITICAL)
    @TmsLink("example.com")
    public void deleteForbiddenForUserWithId2Test() {

        Map<String, String> authData = new HashMap<>();
        authData.put("email", "vinkotov@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseDeleteUser = apiCoreRequests
                .makeDeleteRequestUser("https://playground.learnqa.ru/api/user/", "2", header, cookie);
        Assertions.assertResponseTextEquals(responseDeleteUser, "Please, do not delete test users with ID 1, 2, 3, 4 or 5.");

    }

}
