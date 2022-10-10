package GoRest;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

public class GoRestUsersTests {

    @BeforeClass
    void Setup() {
        // RestAssured kendi statik değişkeni tanımlı değer atanıyor.
        baseURI = "https://gorest.co.in/public/v2/";
    }

    @Test(enabled = false)
    public void createUser() {

        int userID =
                given()
                        // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametreleri
                        .header("Authorization", "Bearer ba22a4e75519e928b02dbb0c3007a74427eb5723e53f7676bf284b0b3f081510")
                        .contentType(ContentType.JSON)
                        .body("{\"name\":\"" + getRandomName() + "\", \"gender\":\"male\", \"email\":\"" + getRandomEmail() + "\", \"status\":\"active\"}")

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
        System.out.println("userID = " + userID);
    }

    public String getRandomEmail() {
        return RandomStringUtils.randomAlphabetic(8).toLowerCase() + "@gmail.com";
    }

    public String getRandomName() {
        return RandomStringUtils.randomAlphabetic(8);
    }

    @Test(enabled = false)
    public void createUserMap() {

        Map<String, String> newUser = new HashMap<>();
        newUser.put("name", getRandomName());
        newUser.put("gender", "male");
        newUser.put("email", getRandomEmail());
        newUser.put("status", "active");

        int userID =
                given()
                        // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametreleri
                        .header("Authorization", "Bearer ba22a4e75519e928b02dbb0c3007a74427eb5723e53f7676bf284b0b3f081510")
                        .contentType(ContentType.JSON)
                        .body(newUser)

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        .extract().path("id");
        System.out.println("userID = " + userID);
    }

    int userID = 0;

    @Test
    public void createUserObject() {

        User newUser = new User();
        newUser.setName(getRandomName());
        newUser.setGender("male");
        newUser.setEmail(getRandomEmail());
        newUser.setStatus("active");

        userID =
                given()
                        // api metoduna gitmeden önceki hazırlıklar : token, gidecek body, parametreleri
                        .header("Authorization", "Bearer ba22a4e75519e928b02dbb0c3007a74427eb5723e53f7676bf284b0b3f081510")
                        .contentType(ContentType.JSON)
                        .body(newUser)

                        .when()
                        .post("users")

                        .then()
                        .log().body()
                        .statusCode(201)
                        .contentType(ContentType.JSON)
                        //.extract().path("id")
                        .extract().jsonPath().getInt("id")
        ;

        // path : class veya tip dönüşümüne imkan veremeyen direkt veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.

        System.out.println("userID = " + userID);
    }

    @Test(dependsOnMethods = "createUserObject", priority = 1)
    public void updateUserObject() {

        Map<String, String> updateUser = new HashMap<>();
        updateUser.put("name", "denem name");

        given()
                .header("Authorization", "Bearer ba22a4e75519e928b02dbb0c3007a74427eb5723e53f7676bf284b0b3f081510")
                .contentType(ContentType.JSON)
                .body(updateUser)
                .pathParam("userID", userID)

                .when()
                .put("users/{userID}")

                .then()
                .log().body()
                .statusCode(200)
                .body("name", equalTo("denem name"))
        ;


        System.out.println("userID = " + userID);

    }

    @Test(dependsOnMethods = "updateUserObject", priority = 2)
    public void getUserByID() {

        Map<String, String> updateUser = new HashMap<>();
        updateUser.put("name", "denem name");

        given()
                .header("Authorization", "Bearer ba22a4e75519e928b02dbb0c3007a74427eb5723e53f7676bf284b0b3f081510")
                .contentType(ContentType.JSON)
                .pathParam("userID", userID)

                .when()
                .get("users/{userID}")

                .then()
                .log().body()
                .statusCode(200)
                .body("id", equalTo(userID))
        ;


        System.out.println("userID = " + userID);
    }

    @Test(dependsOnMethods = "updateUserObject", priority = 3)
    public void deleteUserByID() {


        given()
                .header("Authorization", "Bearer ba22a4e75519e928b02dbb0c3007a74427eb5723e53f7676bf284b0b3f081510")
                .contentType(ContentType.JSON)
                .pathParam("userID", userID)

                .when()
                .delete("users/{userID}")

                .then()
                .log().body()
                .statusCode(204)
        ;
    }

    @Test(dependsOnMethods = "deleteUserByID")
    public void deleteUserByIdNegative() {
        given()
                .header("Authorization", "Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")
                .contentType(ContentType.JSON)
                .log().body()
                .pathParam("userID", userID)

                .when()
                //.delete("users/{userID}") // kapatınca hata kalkıyor acınca hata var

                .then()
                .log().body()
                .statusCode(404)
        ;
    }

    @Test
    public void getUser() {
        Response response =
                given()
                        .header("Authorization", "Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")

                        .when()
                        .get("users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();

        // TODO : 3 usersın id sini alınız (path ve jsonPath ile ayrı yapınız)

        int idUser3path = response.path("[2].id");
        int idUser3JsonPath = response.jsonPath().getInt("[2].id");
        System.out.println("idUser3path = " + idUser3path);
        System.out.println("idUser3JsonPath = " + idUser3JsonPath);

        // TODO : Tüm gelen veriyi bir nesneye atınız
        User[] usersPath = response.as(User[].class);
        System.out.println("Arrays.toString(usersPath) = " + Arrays.toString(usersPath));

        List<User> users = response.jsonPath().getList("", User.class);
    }

    @Test
    public void getUserByIDExtract() {

        // TODO : GetUserByID testinde dönen user ı bir nesneye atınız.

        User user =
                given()
                        .header("Authorization", "Bearer ba22a4e75519e928b02dbb0c3007a74427eb5723e53f7676bf284b0b3f081510")
                        .contentType(ContentType.JSON)
                        .pathParam("userID", 3414)

                        .when()
                        .get("users/{userID}")

                        .then()
                        .log().body()
                        .statusCode(200)
                        //.extract().as(User.class)
                        .extract().jsonPath().getObject("", User.class);


        System.out.println("user = " + user);
    }

    @Test
    public void getUsersV1() {
        Response response =
                given()
                        .header("Authorization", "Bearer 523891d26e103bab0089022d20f1820be2999a7ad693304f560132559a2a152d")

                        .when()
                        .get("https://gorest.co.in/public/v1/users")

                        .then()
                        .log().body()
                        .statusCode(200)
                        .extract().response();
         // response.as() tüm gelen response uygun nesnelerin yapılması gerekiyor

        List<User> dataUsers=response.jsonPath().getList("data",User.class);
        System.out.println("dataUsers = " + dataUsers);
        // Daha önceki örneklerde Clas dönüşümleri için tüm yapıya karşılık gelen
        // gereken tüm classları yazarak dönüştürüp istediğimiz elemanlara ulaşıyorduk.
        // Burada ise aradaki bir veriyi clasa dönüştürerek bir list olarak almamıza
        // imkan veren JSONPATH i kullandık.Böylece tek class ise veri alınmış oldu
        // diğer class lara gerek kalmadan

        // path : class veya tip dönüşümüne imkan veremeyen direk veriyi verir. List<String> gibi
        // jsonPath : class dönüşümüne ve tip dönüşümüne izin vererek , veriyi istediğimiz formatta verir.

    }
}

