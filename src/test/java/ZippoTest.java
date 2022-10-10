import POJO.Location;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;


public class ZippoTest {


    @Test
    public void test() {

        given()
                // hazırlık işlemlerini yapacağız (token,send body,parametreler)
                .when()
                // link i ve metodu veriyoruz
                .then();
        // assertion ve verileri ele alma extract
    }


    @Test
    public void statusCodeTEst() {

        given()
                // hazırlık işlemlerini yapacağız (tekon,send body,parametreler)
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body() // log.all() bütün respons u gösterir
                .statusCode(200) // status kontrolü
        ;
    }

    @Test
    public void contentTypeTest() {

        given()
                // hazırlık işlemlerini yapacağız (tekon,send body,parametreler)
                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body() // log.all() bütün respons u gösterir
                .statusCode(200) // status kontrolü
                .contentType(ContentType.JSON)
        ;
    }

    @Test
    public void checkStateInResponseBody() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body() // bodyleri görmeme yarıyor
                .body("country", equalTo("United States")) // body.country == United States
                .statusCode(200)
                .contentType(ContentType.JSON)
        ;
    }

    @Test
    public void bodyJsonPathTest2() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body() // bodyleri görmeme yarıyor
                .body("places[0].state", equalTo("California"))
                .statusCode(200)
        ;
    }

    @Test
    public void bodyJsonPathTest3() {

        given()

                .when()
                .get("http://api.zippopotam.us/tr/01000")
                .then()
                .log().body() // bodyleri görmeme yarıyor
                .body("places.'place name'", hasItem("Çaputçu Köyü")) // bir index verilmezse dizinin bütün elemanlarında arıyor
                .statusCode(200)
        // "places.'place name'" bu bilgiler "Çaputçu Köyü" bu item e sahip mii
        ;
    }

    @Test
    public void bodyArrayHasSizeTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places", hasSize(1)) // verilen path deki listin size kontrolü
                .statusCode(200)

        ;
    }

    @Test
    public void combiningTest() {

        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")
                .then()
                .log().body()
                .body("places", hasSize(1))
                .body("places.state", hasItem("California"))
                .body("places[0].'place name'", equalTo("Beverly Hills"))
                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest() {

        given()
                .pathParam("Country", "us")
                .pathParam("ZipKod", 90210)
                .log().uri()

                .when()
                .get("http://api.zippopotam.us/{Country}/{ZipKod}")
                .then()
                .log().body()

                .statusCode(200)
        ;
    }

    @Test
    public void pathParamTest2() {
        // 90210 dan 90213 ye kadar test sonuclarında places size nın hepsinde 1 geldiğini test ediniz
        for (int i = 90210; i <= 90213; i++) {

            given()
                    .pathParam("Country", "us")
                    .pathParam("ZipKod", i)
                    .log().uri()


                    .when()
                    .get("http://api.zippopotam.us/{Country}/{ZipKod}")
                    .then()
                    .log().body()
                    .body("places", hasSize(1))
                    .statusCode(200)
            ;
        }
    }

    @Test
    public void queryParamTest() {

        given()
                .param("page", 1)
                .log().uri() // request linki

                .when()
                .get("https://gorest.co.in/public/v1/users")
                .then()
                .log().body()
                .body("meta.pagination.page", equalTo(1))

                .statusCode(200)
        ;
    }

    @Test
    public void queryParamTest2() {

        for (int pageNo = 1; pageNo <= 10; pageNo++) {
            given()
                    .param("page", pageNo)
                    .log().uri() // request linki

                    .when()
                    .get("https://gorest.co.in/public/v1/users")

                    .then()
                    .log().body()
                    .body("meta.pagination.page", equalTo(pageNo))

                    .statusCode(200)
            ;
        }
    }

    RequestSpecification requestSpecs;
    ResponseSpecification responseSpecs;

    @BeforeClass
    void Setup(){

        baseURI="https://gorest.co.in/public/v1";

        requestSpecs=new RequestSpecBuilder()
                .log(LogDetail.URI)
                .setAccept(ContentType.JSON)
                .build();
        responseSpecs=new ResponseSpecBuilder()
                .expectStatusCode(200)
                .expectContentType(ContentType.JSON)
                .log(LogDetail.BODY)
                .build();
    }

    @Test
    public void requestResponseSpecification() {

        given()
                .param("page", 1)
                .spec(requestSpecs)

                .when()
                .get("/users")

                .then()
                .body("meta.pagination.page",equalTo(1))
                .spec(responseSpecs)
        ;
    }

    @Test
    public void extractingJsonPath() {

        String placeName=
        given()

                .when()
                .get("http://api.zippopotam.us/us/90210")

                .then()
                //.log().body()
                .statusCode(200)
                .extract().path("places[0].'place name'")
                // extract metodu ile given ile başlayan satır, bir değer döndürür hale geldi, en sonda extract olmalı
        ;
        System.out.println("placeName = " + placeName);
    }

    @Test
    public void extractingJsonPathInt() {

        int limit=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("meta.pagination.limit")
                ;
        System.out.println("limit = " + limit);
        Assert.assertEquals(limit,10,"test sonucu");
    }

    @Test
    public void extractingJsonPathInt2() {

        int id=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data[2].id")
                ;
        System.out.println("id = " + id);
    }

    @Test
    public void extractingJsonPathIntList() {

        List<Integer> idler=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data.id") // data daki bütün id leri verir [] silinince
                ;
        System.out.println("idler = " + idler);
        Assert.assertTrue(idler.contains(3045));
    }

    @Test
    public void extractingJsonPathStringList() {

        List<String> nameler=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().path("data.name") // data daki bütün id leri verir [] silinince
                ;
        System.out.println("nameler = " + nameler);
        Assert.assertTrue(nameler.contains("Ganapati Gupta"));
    }

    @Test
    public void extractingJsonPathResponseAll() {

        Response body=
                given()

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        //.log().body()
                        .statusCode(200)
                        .extract().response() // bütün body alındı
                ;
        List<Integer> idler=body.path("data.id");
        List<String > isimler=body.path("data.name");
        int limit=body.path("meta.pagination.limit");

        System.out.println("idler = " + idler);
        System.out.println("isimler = " + isimler);
        System.out.println("limit = " + limit);
    }


    @Test
    public void extractingJsonPOJO() { // OJO : Json Object i (Plain Old Java Object)

        Location yer=
                given()

                        .when()
                        .get("http://api.zippopotam.us/us/90210")

                        .then()
                        .statusCode(200)
                        .extract().as(Location.class) // Location şablonu
                ;
        System.out.println("yer = " + yer);

        System.out.println("yer.getCountry() = "+yer.getCountry());
        System.out.println("yer.getPlaces().get(0).getPlacename() = "+yer.getPlaces().get(0).getPlacename());
    }

//    "post code": "90210",
//            "country": "United States",
//            "country abbreviation": "US",
//
//            "places": [
//    {
//        "place name": "Beverly Hills",
//            "longitude": "-118.4065",
//            "state": "California",
//            "state abbreviation": "CA",
//            "latitude": "34.0901"
//    }
//    ]
//
//    class Location{
//        String postcode;
//        String country;
//        String countryabbreviation;
//        ArrayList<Place> places
//    }
//
//    class Place{
//        String placename;
//        String longitude;
//        String state;
//        String stateabbreviation
//        String latitude;


}

