package Internship;


import Internship.Model.Country;
import io.restassured.http.ContentType;
import io.restassured.http.Cookies;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class ProjeInternship {

    // {"username": "richfield.edu","password": "Richfield2020!","rememberMe": "true"}


    Cookies cookies;

    @Test
    public void Login(){
        baseURI="https://demo.mersys.io/";

        Map<String,String> account=new HashMap<>();
        account.put("username","richfield.edu");
        account.put("password","Richfield2020!");
        account.put("rememberMe","true");


        cookies=
        given()
                .contentType(ContentType.JSON)
                .body(account)

                .when()
                .post("auth/login")


                .then()
                .statusCode(200)
                .extract().response().getDetailedCookies()
        ;
    }
    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase();
    }
    public String getRandomCode() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase();
    }
    public String  getRandomCapacity(){return RandomStringUtils.randomNumeric(4);}
    String countryName;
    String countryCode;
    String countryID;
    String countryCapacity;



    @Test
    public void createCountry(){

        Map<String,String> school=new HashMap<>();
        school.put("id","5fe07e4fb064ca29931236a5");

        countryName=getRandomName();
        countryCode=getRandomCode();
        countryCapacity=getRandomCapacity();
        Country country=new Country();
        country.setName(countryName);
        country.setCode(countryCode);
        country.setCapacity(countryCapacity);
        country.setSchool(school);
        country.setType("CLASS");


        countryID=
                given()

                        .cookies(cookies)
                        .contentType(ContentType.JSON)
                        .body(country)
                        .when()
                        .post("school-service/api/location")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .extract().jsonPath().getString("id")


        ;
    }


    public void createCountryNegative(){

        Country country=new Country();
        country.setName(countryName);
        country.setCode(countryCode);


        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .post("school-service/api/location")

                .then()
                .log().body()
                .statusCode(400)
                .body("message",equalTo("The Country with Name \""+countryName+"\" already exists."))
        ;
    }

    public void updateCountry(){

        countryName=getRandomName();
        Country country=new Country();
        country.setId(countryID);
        country.setName(countryName);
        country.setCode(countryCode);

        given()

                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .put("school-service/api/location")


                .then()
                .statusCode(200)
                .body("name",equalTo(countryName))
        ;
    }

    public void deleteCountry(){


        given()
                .cookies(cookies)
                .pathParam("countryID",countryID)

                .when()
                .delete("school-service/api/location{countryID}")

                .then()
                .statusCode(200)
        ;
    }

    public void deleteCountryNegative(){

        given()
                .cookies(cookies)
                .pathParam("countryID",countryID)

                .when()
                .delete("school-service/api/location{countryID}")

                .then()
                .statusCode(400)
        ;
    }

    public void updateCountryNegative(){

        countryName=getRandomName();
        Country country=new Country();
        country.setId(countryID);
        country.setName(countryName);
        country.setCode(countryCode);

        given()
                .cookies(cookies)
                .contentType(ContentType.JSON)
                .body(country)

                .when()
                .put("school-service/api/location")


                .then()
                .statusCode(400)
                .body("message", equalTo("Country not found"))

        ;
    }
}
