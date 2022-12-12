package tests;

import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import lib.Assertions;
import lib.BaseTestCase;
import lib.DataGenerator;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class UserEditTest extends BaseTestCase {
    @Test
    // создаем пользователя
    // редактируем пользователя
    // проверяем, что успешно его отредактировали с помощью метода получения данных о пользователе
    // комплексный тест: 1. создание пользователя. 2. авторизация. 3. редактирование. 4. получение данных
    public void testEditJustCreatedTest() {
    //GENERATE USER
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
    // в переменную сохраняем ид пользователя, чтобы дальше с ним работать
        String userId = responseCreateAuth.getString("id");

        //LOGIN
        Map<String, String> authData = new HashMap<>();
        // из сгенерированных данных достаем почту и пароль
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        //EDIT
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        //GET
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId)
                .andReturn();

        Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
    @Test

    public void changeUserDataBeingUnauthorizedTest() {
        String newName = "Changed Name";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/")
                .andReturn();
        Assertions.assertResponseTextNotEquals(responseEditUser, "Auth token not supplied");


    }
    @Test
    // создаем первого пользователя

    public void EditBeingAuthorizedWithAnotherUserTest() {
        Map<String, String> userData = DataGenerator.getRegistrationData();

        JsonPath responseCreateAuth = RestAssured
                .given()
                .body(userData)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String userId = responseCreateAuth.getString("id");

        Map<String, String> userData2 = DataGenerator.getRegistrationData();

        // создаем второго пользователя
        JsonPath responseCreateAuth2 = RestAssured
                .given()
                .body(userData2)
                .post("https://playground.learnqa.ru/api/user/")
                .jsonPath();
        String userId2 = responseCreateAuth.getString("id");

        // авторизация первым пользователем
        Map<String, String> authData = new HashMap<>();
        authData.put("email", userData.get("email"));
        authData.put("password", userData.get("password"));

        Response responseGetAuth = RestAssured
                .given()
                .body(authData)
                .post("https://playground.learnqa.ru/api/user/login")
                .andReturn();

        // редактируем второго пользователя
        String newName = "Changed Name2";
        Map<String, String> editData = new HashMap<>();
        editData.put("firstName", newName);

        Response responseEditUser = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .body(editData)
                .put("https://playground.learnqa.ru/api/user/" + userId2)
                .andReturn();

        // проверяем, что не смогли отредактировать данные второго пользователя, будучи авторизованными первым
        Response responseUserData = RestAssured
                .given()
                .header("x-csrf-token", this.getHeader(responseGetAuth, "x-csrf-token"))
                .cookie("auth_sid", this.getCookie(responseGetAuth, "auth_sid"))
                .get("https://playground.learnqa.ru/api/user/" + userId2)
                .andReturn();

          Assertions.assertJsonByName(responseUserData, "firstName", newName);
    }
}
