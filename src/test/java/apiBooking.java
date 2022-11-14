import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

public class apiBooking {

    public static String tokenBooking;
    public static String idBooking;
    public static String codBooking;

    @BeforeAll
    static void setup(){
        RestAssured.baseURI = "https://restful-booker.herokuapp.com";
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.requestSpecification = new RequestSpecBuilder()
                                            .setContentType(ContentType.JSON)
                                            .setAccept("application/json")
                                            .build();
    }

    @Test
    public void runTest(){
        token();
        getBookingIds();
        getBooking();
        createBooking();
        updateBooking();
        partialUpdateBooking();
        deleteBooking();
        healthCheck();
    }


    public void token(){

        tokenBooking =
                given()
                .body("{\n" +
                        "    \"username\" : \"admin\",\n" +
                        "    \"password\" : \"password123\"\n" +
                        "}")
                .post("auth")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("token",notNullValue())
                .extract()
                .jsonPath().getString("token");

    }


    public void getBookingIds(){


        codBooking =      given()
                .contentType(ContentType.JSON)
                .get("booking")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .body("bookingid",notNullValue())
                .extract()
                .jsonPath().getString("bookingid[0]");

    }

    public void getBooking(){

                given()
                .get("booking/" + codBooking)
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    public void createBooking(){

       idBooking =
                given()
                .body("{\n" +
                        "    \"firstname\" : \"Jim\",\n" +
                        "    \"lastname\" : \"Brown\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .post("booking")
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
               .jsonPath().getString("bookingid");

       //System.out.println("hola: "+ tokenBooking+" y cod " + idBooking);
    }

    public void updateBooking(){

         String response =
                 given()
                .when()
                .body("{\n" +
                        "    \"firstname\" : \"Patrick\",\n" +
                        "    \"lastname\" : \"Valencia\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .header("Cookie","token="+tokenBooking)
                .put("booking/"+idBooking)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("firstname");


         assertThat(response,equalTo("Patrick"));

    }

    public void partialUpdateBooking(){

         String response =    given()
                .when()
                .body("{\n" +
                        "    \"firstname\" : \"James\",\n" +
                        "    \"lastname\" : \"Brown\"\n" +
                        "}")
                .header("Cookie","token="+tokenBooking)
                .patch("booking/"+idBooking)
                .then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .jsonPath().getString("firstname")
                 ;

         assertThat(response,equalTo("James"));

    }

    public void deleteBooking(){

        given()
                .header("Cookie","token=" + tokenBooking)
                .delete("booking/"+idBooking)
                .then()
                .statusCode(HttpStatus.SC_CREATED);

    }

    public void healthCheck(){

                 given()
                .get("/ping")
                .then()
                .statusCode(HttpStatus.SC_CREATED);

    }


}
