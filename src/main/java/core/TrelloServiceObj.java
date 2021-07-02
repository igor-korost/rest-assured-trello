package core;

import beans.BoardSchema;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import constants.BoardFields;
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
import java.util.Map;

import static constants.BoardFields.*;

public class TrelloServiceObj {
    public static final URI TRELLO_URI = URI.create("https://api.trello.com/1/boards");
    public static final String API_TOKEN = PropertiesFileReader.getProperty("API_TOKEN");
    public static final String API_KEY = PropertiesFileReader.getProperty("API_KEY");

    private Method requestMethod;
    private Map<String, String> parameters;
    private String boardID;


    public TrelloServiceObj(Method method, String boardID, Map<String, String> parameters) {
        this.requestMethod = method;
        this.parameters = parameters;
        this.boardID = boardID;
    }

    public static RequestBuilder requestBuilder() {
        return new RequestBuilder();
    }

    public static class RequestBuilder {
        private Method requestMethod;
        private String boardID = ""; // default value
        private Map<String, String> parameters = new HashMap<>();

        public RequestBuilder setMethod(Method method) {
            requestMethod = method;
            return this;
        }

        public RequestBuilder setName(String name) {
            parameters.put(BOARD_NAME_FIELD, name);
            return this;
        }

        public RequestBuilder setBoardId(String id) {
            boardID = id;
            return this;
        }

        public TrelloServiceObj buildRequest() {
            return new TrelloServiceObj(requestMethod, boardID, parameters);
        }
    }

    // Create Board Java object from response
    public static BoardSchema boardSchemaObject(Response response) {
        return new Gson().fromJson(response.asString().trim(), new TypeToken<BoardSchema>() {
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
        String boardIdPath = "";
        if (boardID.length() > 0) {
            boardIdPath = boardID + "/";
        }
        return RestAssured
                .given(requestSpecification()).log().all()
                .queryParams(parameters)
                .request(requestMethod, boardIdPath)
                .prettyPeek();
    }
}