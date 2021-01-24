import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.hamcrest.Matchers;
import users.model.Users;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static io.restassured.http.ContentType.*;
import static org.apache.http.HttpStatus.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


public class ApiTests {

    @BeforeAll
    static void setUp() {
        RestAssured.filters(new AllureRestAssured());
        RestAssured.baseURI = "https://reqres.in/api";
    }

    @Test
    void testEntitiesPerPage() {
        Users users = given()
                .queryParam("page", 2)

                .when()
                .get("/users")

                .then()
                .statusCode(SC_OK)
                .contentType(JSON)
                .body("per_page", is(6))
                .body("data.id", hasSize(6))
                .extract().as(Users.class);

        int usersPerPage = users.getPerPage();
        int actualUsersOnPage = users.getData().size();

        assertThat(usersPerPage, equalTo(actualUsersOnPage));
    }

    @Test
    void testEntitiesPerPageOtherWay() {
        Response response = given()
                .queryParam("page", 2)

                .when()
                .get("/users");

        int usersPerPage = response.path("per_page");
        int actualUsersOnPage = response.path("data.id.size()");

        assertThat(usersPerPage, equalTo(actualUsersOnPage));
    }

    @Test
    void testUserNotFound() {
        given()
                .when()
                .get("/users/23")

                .then()
                .statusCode(SC_NOT_FOUND)
                //Несколько способов проверки, что тело пустое
                .body("isEmpty()", is(true))
                .body("size()", is(0));
    }

    @Test
    void testCreateUser() {
        String request = """
                {
                    "name": "morpheus",
                    "job": "leader"
                }""";

        given()
                .body(request)
                .contentType(JSON)

                .when()
                .post("/users")

                .then()
                .statusCode(SC_CREATED)
                .contentType(JSON)
                .body("name", is("morpheus"))
                .body("job", is("leader"))
                .body("id", not(empty()));
    }
}
