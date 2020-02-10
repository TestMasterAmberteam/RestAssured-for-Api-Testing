import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.hamcrest.CoreMatchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class RestTest {
    private static final Logger LOGGER = Logger.getLogger(RestTest.class.getName());
    private static final String API_VERSION = "/v1";
    @BeforeEach
    void setup(){
        RestAssured.basePath = "/api/rest";
    }



    @Test
    void getTrainingsList() {
        RestAssured
                .given()
                .port(9999)
                .log().headers()

                .when()
                .get(API_VERSION + "/trainings/all")

                .then()
                .statusCode(200).assertThat();
    }

    @Test
    void isAkademiaVATOnTrainingList() {
        RestAssured
                .given()
                .port(9999)

                .when()
                .get(API_VERSION + "/trainings/all")

                .then().log().all()
                .statusCode(200).assertThat()
                .body("name", hasItem("AKADEMIA VAT – PO KOMPLEKSOWEJ NOWELIZACJI USTAWY KURS 4-DNIOWY"));
    }


    
    @Test
    void isBbeMaxParticipansEqualThirty() {
        Response response = RestAssured.given().port(9999).get(API_VERSION + "/training/3");
        JsonPath jsonPath = response.jsonPath();
        LOGGER.log(Level.INFO, jsonPath.prettify());
        assertEquals(30, jsonPath.getInt("maxParticipants"));
    }

    @Test
    void isItPossibleToAddTraining() {
        String payload = "{\n" +
                "  \"name\": \"KraQA\",\n" +
                "  \"price\": 0,\n" +
                "  \"trainer\": \"grześ\",\n" +
                "  \"maxParticipants\": 200,\n" +
                "  \"place\":\"Kraków\"\n" +
                "}";


        Response response = RestAssured
                .given()
                .port(9999)
                .header("content-type", "application/json;charset=utf-8")
                .log().all()

                .when()
                .body(payload)
                .post(API_VERSION + "/training");
        assertEquals(201, response.getStatusCode());
        String responseBody = response.body().prettyPrint();
        LOGGER.log(Level.INFO, responseBody);
        assertNotNull(responseBody);
    }

    @Test
    void hasMapInBody() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "Rest Assured w służbie testów API");
        map.put("price", 0);
        map.put("trainer", "Grześ Ha i Lukasz B");
        map.put("maxParticipants", 20);
        map.put("place", "ConSelenium v3");

        RestAssured.port = 9999;
        RestAssured
                .given()
                .contentType(ContentType.JSON)

                .when()
                .body(map)
                .log().body()
                .log().uri().log().ifValidationFails().log().method()
                .post(API_VERSION + "/training")

                .then().log().body()
                .statusCode(201).assertThat();
    }
    @Test
    void pojoInBody(){
        TrainingModel training = new TrainingModel();
        training.setMaxParticipants(20).setName("Szkolenie").setPlace("warszawa").setPrice(100).setTrainer("grześ");
        RestAssured.port = 9999;
        RestAssured
                .given()
                .contentType(ContentType.JSON)

                .when()
                .body(training)
                .log().body()
                .log().uri().log().ifValidationFails().log().method()
                .post(API_VERSION + "/training")

                .then().log().body()
                .statusCode(201).assertThat();
    }


    @Test
    void basicAuth() {
        RestAssured
                .given()
                .port(9999)
                .auth().basic("AmberTeam", "AmberPassword")

                .when()
                .get("/security/people/all")

                .then()
                .statusCode(200).assertThat()
                .log().all();
    }
}