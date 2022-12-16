package tests;

import io.qameta.allure.Epic;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.TmsLink;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.ApiCoreRequests;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    private final ApiCoreRequests apiCoreRequests = new ApiCoreRequests();

    @Test
    @Epic("Редактирование пользователя")
    @DisplayName("Изменение данных пользователя, будучи авторизованными")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("example.com")
    // создаем пользователя
    // редактируем пользователя
    // проверяем, что успешно его отредактировали с помощью метода получения данных о пользователе
    // комплексный тест: 1. создание пользователя. 2. авторизация. 3. редактирование. 4. получение данных
    public void testEditJustCreatedTest() {
        //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/api/user/", userData);

        // в переменную сохраняем ид пользователя, чтобы дальше с ним работать
        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        // из сгенерированных данных достаем почту и пароль
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);
        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseEditUser = apiCoreRequests
                .makePutRequestCreateUser("https://playground.learnqa.ru/api/user/", userId, editData, header, cookie);

        Response responseUserData = apiCoreRequests
                .makeGetRequestUserData("https://playground.learnqa.ru/api/user/", userId, header, cookie);

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }

    @Test
    @Epic("Редактирование пользователя")
    @DisplayName("Попытаемся изменить данные пользователя, будучи неавторизованными")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("example.com")
    public void changeUserDataBeingUnauthorizedTest() {
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = apiCoreRequests
                .makePutRequestCreateUserBeingUnauthorized("https://playground.learnqa.ru/api/user/", editData);

        Assertions.assertResponseTextNotEquals(responseEditUser, "Auth token not supplied");
    }

    @Test
    @Epic("Редактирование пользователя")
    @DisplayName("Попытаемся изменить данные пользователя, будучи авторизованным другим пользователем")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("example.com")
    public void EditBeingAuthorizedWithAnotherUserTest() {
        // создаем первого пользователя
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/api/user/", userData);
        String userId = responseCreateAuth.getString("id");

        // создаем второго пользователя
        Map<String, String> userData2 = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth2 = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/api/user/", userData2);
        String userId2 = responseCreateAuth2.getString("id");

        // авторизация первым пользователем
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        // редактируем второго пользователя
        String newName = "Changed Name2";
        Map<String, String> editData = new HashMap<>();
        editData.put("username", newName);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseEditUser = apiCoreRequests
                .makePutRequestCreateUser("https://playground.learnqa.ru/api/user/", userId2, editData, header, cookie);

        // проверяем, что не смогли отредактировать данные второго пользователя, будучи авторизованными первым
        Response responseUserData = apiCoreRequests
                .makeGetRequestUserData("https://playground.learnqa.ru/api/user/", userId2, header, cookie);

        Assertions.assertJsonByName(responseUserData, "username", "learnqa");

    }

    @Test
    @Epic("Редактирование пользователя")
    @DisplayName("Попытаемся изменить email пользователя, будучи авторизованными тем же пользователем, на новый email без символа @")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("example.com")
    public void editEmailBeingAuthoriseTest() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        userData.put("email", "q@example.com");
        userData.put("password", "1234");

        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/api/user/", userData);

        Map<String, String> authData = new HashMap<>();

        authData.put("email", "q@example.com");
        authData.put("password", "1234");

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String newEmail = "qexample.com";
        Map<String, String> editData = new HashMap<>();
        editData.put("email", newEmail);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseEditUser = apiCoreRequests
                .makePutRequestCreateUserWithoutUserId("https://playground.learnqa.ru/api/user/", editData, header, cookie);
        Assertions.assertResponseJsonTextEquals(responseEditUser, "Invalid email format");
    }

    @Test
    @Epic("Редактирование пользователя")
    @DisplayName("Попытаемся изменить firstName пользователя, будучи авторизованными тем же пользователем, на очень короткое значение в один символ")
    @Severity(SeverityLevel.NORMAL)
    @TmsLink("example.com")
    public void editForShortNameJustCreatedTest() {
        Map<String, String> userData = DataGenerator.getRegistrationData();
        JsonPath responseCreateAuth = apiCoreRequests
                .makePostRequestCreateUser("https://playground.learnqa.ru/api/user/", userData);

        String userId = responseCreateAuth.getString("id");

        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = apiCoreRequests
                .makePostRequest("https://playground.learnqa.ru/api/user/login", authData);

        String newName = "S";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        String header = this.getHeader(responseGetAuth, "x-csrf-token");
        String cookie = this.getCookie(responseGetAuth, "auth_sid");

        Response responseEditUser = apiCoreRequests
                .makePutRequestCreateUser("https://playground.learnqa.ru/api/user/", userId, editData, header, cookie);
        Assertions.assertJsonByName(responseEditUser, "error",
                "Too short value for field firstName");

    }
}
