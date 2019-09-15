import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import static io.restassured.config.EncoderConfig.encoderConfig;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.hasItems;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class RestTest {
    private static final Logger LOGGER = Logger.getLogger(RestTest.class.getName());

    @Test
    void isGrzesiekOnMemberList() {
        RestAssured
                .given()
                .port(9099)

                .when()
                .get("/api/members")

                .then()
                .statusCode(200)
                .body("name", hasItem("Grzesiek"));
    }

    @Test
    void isGrzesiekHasTaeCertificate() {
        RestAssured.port = 9099;
        Response response = RestAssured.given().get("/api/members");
        ArrayList<String> grzesieksCertificates = response.path("find {it.name==\"Grzesiek\"}.certificates");
        assert (grzesieksCertificates.contains("ISTQB CTAL TAE"));
    }

    @Test
    void hasKonradThreeAndHalfYearsOfExpierience() {
        Response response = RestAssured.given().port(8080).get("api/member/1");
        JsonPath jsonPath = response.jsonPath();
        LOGGER.log(Level.INFO, jsonPath.prettify());
        assertEquals(3.5F, jsonPath.get("yearsOfExperience"));

    }


    @Test
    void isKonradPythonLiker() {
        RestAssured
                .given()
                .port(9099)

                .when()
                .get("api/member/1")

                .then()
                .log().all()
                .statusCode(200)
                .body("programmingLanguages", hasItem("Python"));
    }

    @Test
    void isKonradKnowUiPath() {
        RestAssured
                .given()
                .port(9099)

                .when()
                .log().all()
                .get("api/member/1")

                .then()
                .statusCode(200)
                .body("knownTechnologies", hasItems("UIPath", "JMeter"));
    }

    @Test
    void isItPossibleToAddTraining() {
        String payload = "{\n" +
                "  \"price\": 2500,\n" +
                "  \"trainerName\": \"Janek\",\n" +
                "  \"trainingName\": \"ISTQB Certified Agile Tester Extension\"\n" +
                "}";
        Response response = RestAssured
                .given()
                .config(RestAssured.config()
                        .encoderConfig(encoderConfig()
                                .encodeContentTypeAs("application/json;charset=utf-8", ContentType.TEXT)))
                .port(9099)
                .header("content-type", "application/json;charset=utf-8")
                .log().all()

                .when()
                .body(payload)
                .post("api/training");
        assertEquals(201, response.getStatusCode());
        String responseBody = response.body().asString();
        LOGGER.log(Level.INFO, responseBody);
        assertNotNull(responseBody);


    }
}
