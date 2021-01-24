import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import users.model.Users;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
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
        Users users =
                given()
                        .queryParam("page", 2)

                        .when()
                        .get("/users")

                        .then()
                        .statusCode(200)
                        .contentType(ContentType.JSON)
                        .extract().as(Users.class);

        int usersPerPage = users.getPerPage();
        int actualUsersOnPage = users.getData().size();

        assertThat(usersPerPage, equalTo(actualUsersOnPage));
    }
}
