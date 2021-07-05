package core;

import beans.Board;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import util.PropertiesFileReader;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static constants.AuthFields.KEY_FIELD;
import static constants.AuthFields.TOKEN_FIELD;
import static constants.BoardFields.BOARD_NAME_FIELD;

public class TrelloServiceObj {
    public static final URI TRELLO_URI = URI.create(PropertiesFileReader.getProperty("TRELLO_URI"));
    public static final String API_TOKEN = PropertiesFileReader.getProperty("API_TOKEN");
    public static final String API_KEY = PropertiesFileReader.getProperty("API_KEY");
    public static final String allBoardsPath = "/members/me/boards";

    private Method requestMethod;
    private Map<String, String> parameters;
    private String path;

    public TrelloServiceObj(Method method, String path, Map<String, String> parameters) {
        this.requestMethod = method;
        this.parameters = parameters;
        this.path = path;
    }

    public static RequestBuilder requestBuilder() {
        return new RequestBuilder();
    }

    public static class RequestBuilder {
        private Method requestMethod;
        private String path = ""; // default value
        private Map<String, String> parameters = new HashMap<>();

        public RequestBuilder setMethod(Method method) {
            requestMethod = method;
            return this;
        }

        public RequestBuilder setName(String name) {
            parameters.put(BOARD_NAME_FIELD, name);
            return this;
        }

        public RequestBuilder setPath(String path) {
            this.path = path;
            return this;
        }

        public TrelloServiceObj buildRequest() {
            return new TrelloServiceObj(requestMethod, path, parameters);
        }
    }

    // Create Board Java object from response
    public static Board createBoard(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<Board>() {
        }.getType());
    }

    public static List<Board> createBoardList(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<List<Board>>() {
        }.getType());
    }

    // Request  specification
    public static RequestSpecification requestSpecification() {
        return new RequestSpecBuilder()
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .addQueryParam(KEY_FIELD, API_KEY)
                .addQueryParam(TOKEN_FIELD, API_TOKEN)
                .setBaseUri(TRELLO_URI)
                .build();
    }

    // Response specification
    public static ResponseSpecification goodResponseSpecification() {
        return new ResponseSpecBuilder()
                .expectContentType(ContentType.JSON)
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    // Send request
    public Response sendRequest() {
        return RestAssured
                .given(requestSpecification()).log().all()
                .queryParams(parameters)
                .request(requestMethod, path)
                .prettyPeek();
    }
}