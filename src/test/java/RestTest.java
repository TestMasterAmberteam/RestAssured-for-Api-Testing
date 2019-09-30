import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
    void isFranekOnMemberList() {
        RestAssured
                .given()
                .port(9099)
                .log().headers()

                .when()
                .get("/api/members")

                .then()
                .statusCode(200)
                .body("name", hasItem("Franek"));
    }

    @Test
    void isFranekHasTaeCertificate() {
        RestAssured.port = 9099;
        Response response = RestAssured.given().get("/api/members");
        ArrayList<String> grzesieksCertificates = response.path("find {it.name==\"Franek\"}.certificates");
        assert (grzesieksCertificates.contains("ISTQB CTAL TAE"));
    }

    @Test
    void hasStaszekThreeAndHalfYearsOfExpierience() {
        Response response = RestAssured.given().port(9099).get("api/member/1");
        JsonPath jsonPath = response.jsonPath();
        LOGGER.log(Level.INFO, jsonPath.prettify());
        assertEquals("3.5", jsonPath.get("yearsOfExperience").toString());

    }


    @Test
    void isStaszekPythonLiker() {
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
    void isStaszekKnowUiPath() {
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

    @Test
    void basicAuth(){
        RestAssured
                .given()
                .port(9099)
                .auth().basic("AmberTeam", "AmberPassword")

                .when()
                .get("api/security/servers")

                .then()
                .statusCode(200).assertThat()
                .log().all();
    }

    @Test
    void hashMapInBody() {
        Map<String, Object> map = new HashMap<>();
        map.put("price", 2100);
        map.put("trainerName", "Staszek");
        map.put("trainingName", "Rest Assured");

        RestAssured.port = 9099;
        RestAssured
                .given()
                .contentType(ContentType.JSON)

                .when()
                .body(map)
                .log().all()
                .post("api/training")


                .then()
                .statusCode(201).assertThat()
                .log().all();
    }
}
