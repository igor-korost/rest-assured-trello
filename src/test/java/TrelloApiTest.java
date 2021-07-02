
import core.DataProviders;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static core.TrelloServiceObj.boardSchemaObject;
import static core.TrelloServiceObj.requestBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TrelloApiTest {
    List<String> boardIdList = new ArrayList<>();
    List<String> boardNameList = new ArrayList<>();

    @Test(dataProvider = "boardNames", dataProviderClass = DataProviders.class, priority = 1)
    public void createBoardTest(String boardName) {

        Response response = requestBuilder()
                .setMethod(Method.POST)
                .setName(boardName)
                .buildRequest()
                .sendRequest();

        // Assert response board name and  board id equals to request board id and name
        String responseBoardId = boardSchemaObject(response).getId();
        String responseBoardName = boardSchemaObject(response).getName();

        assertThat(responseBoardId, is(notNullValue()));
        assertThat(responseBoardName, equalTo(boardName));

        // Save response for future reference
        boardIdList.add(responseBoardId);
        boardNameList.add(responseBoardName);
    }

    @Test(priority = 2)
    public void getBoardByIdTest() {
        if (boardIdList.size() == 0) {
            assertThat("Board list should be created first", false);
        }
        for (int i = 0; i < boardIdList.size(); i++) {
            Response response = requestBuilder()
                    .setMethod(Method.GET)
                    .setBoardId(boardIdList.get(i))
                    .buildRequest()
                    .sendRequest();
            // Assert response board name equals to previously generated board names
            String responseBoardName = boardSchemaObject(response).getName();
            assertThat(responseBoardName, equalTo(boardNameList.get(i)));
        }
    }

    @Test(dataProvider = "boardNewNames", dataProviderClass = DataProviders.class, priority = 2)
    public void updateBoardTest(String newName) {
        if (boardIdList.size() == 0) {
            assertThat("Board list should be created first", false);
        }
        for (String boardId : boardIdList) {
            requestBuilder()
                    .setMethod(Method.PUT)
                    .setBoardId(boardId)
                    .setName(newName)
                    .buildRequest()
                    .sendRequest();
        }
    }

    @Test(priority = 3)
    public void deleteBoardTest() {
        if (boardIdList.size() == 0) {
            assertThat("Board list should be created first", false);
        }

        for (String boardId : boardIdList) {
            requestBuilder()
                    .setMethod(Method.DELETE)
                    .setBoardId(boardId)
                    .buildRequest()
                    .sendRequest();
        }

        // Try to get deleted boards
        for (String boardId : boardIdList) {
            Response response = requestBuilder()
                    .setMethod(Method.GET)
                    .setBoardId(boardId)
                    .buildRequest()
                    .sendRequest();

            assertThat(response.getStatusCode(), equalTo(HttpStatus.SC_NOT_FOUND));
        }
    }
}
