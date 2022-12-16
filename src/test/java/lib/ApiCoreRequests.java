package lib;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.Header;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

import java.util.Map;

import static io.restassured.RestAssured.given;

// вынесли запросы в отдельный класс, чтобы не перегружать тесты степами
public class ApiCoreRequests {
    @Step("Make a GET-request without auth cookie and token")
    public Response makeGetRequestWithoutCookieAndToken(String url) {
        return given()
                .filter(new AllureRestAssured())
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token and auth cookie")
    public Response makeGetRequest(String url, String token, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with auth cookie only")
    public Response makeGetRequestWithCookie(String url, String cookie) {
        return given()
                .filter(new AllureRestAssured())
                .cookie("auth_sid", cookie)
                .get(url)
                .andReturn();
    }

    @Step("Make a GET-request with token only")
    public Response makeGetRequestWithToken(String url, String token) {
        return given()
                .filter(new AllureRestAssured())
                .header(new Header("x-csrf-token", token))
                .get(url)
                .andReturn();
    }

    @Step("Make a POST-request")
    public Response makePostRequest(String url, Map<String, String> authData) {
        return given()
                .filter(new AllureRestAssured())
                .body(authData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with incorrect email")
    public Response makePostRequestCreateUserWithIncorrectEmail(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with short name")
    public Response makePostRequestCreateUserWithShortName(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with long name")
    public Response makePostRequestCreateUserWithLongName(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request with empty field")
    public Response makePostRequestCreateUserRequiredEmptyField(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .andReturn();
    }

    @Step("Make a POST-request Create user")
    public JsonPath makePostRequestCreateUser(String url, Map<String, String> userData) {
        return given()
                .filter(new AllureRestAssured())
                .body(userData)
                .post(url)
                .jsonPath();
    }

    @Step("Make a PUT-request Edit user")
    public Response makePutRequestCreateUser(String url, String userId, Map<String, String> editData, String token, String cookie) {
        return given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .body(editData)
                .put(url + userId)
                .andReturn();
    }

    @Step("Make a GET-request Edit user")
    public Response makeGetRequestUserData(String url, String userId, String token, String cookie) {
        return given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .get(url + userId)
                .andReturn();
    }

    @Step("Make a PUT-request Edit user being unauthorized")
    public Response makePutRequestCreateUserBeingUnauthorized(String url, Map<String, String> editData) {
        return given()
                .body(editData)
                .put(url)
                .andReturn();
    }

    @Step("Make a PUT-request Edit user")
    public Response makePutRequestCreateUserWithoutUserId(String url, Map<String, String> editData, String token, String cookie) {
        return given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .body(editData)
                .put(url)
                .andReturn();
    }

    @Step("Make a Delete-request for user")
    public Response makeDeleteRequestUser(String url, String userId, String token, String cookie) {
        return given()
                .header("x-csrf-token", token)
                .cookie("auth_sid", cookie)
                .delete(url + userId)
                .andReturn();


    }
}





