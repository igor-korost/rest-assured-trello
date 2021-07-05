import core.DataProviders;
import helpers.BoardsList;
import io.restassured.http.Method;
import io.restassured.response.Response;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static core.DataProviders.boardName;
import static core.DataProviders.boardNewName;
import static core.TrelloServiceObj.createBoard;
import static core.TrelloServiceObj.requestBuilder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static constants.Endpoints.*;

public class TrelloApiTest {
    List<String> boardIdList = new ArrayList<>();
    List<String> boardNameList = new ArrayList<>();

    @BeforeMethod
    public void setUp() {
        // Create Board
        Response response = requestBuilder()
                .setMethod(Method.POST)
                .setName(boardName)
                .setPath(BOARD_PATH)
                .buildRequest()
                .sendRequest();

        String responseBoardId = createBoard(response).getId();
        assertThat("Response id is null", responseBoardId, is(notNullValue()));

        String responseBoardName = createBoard(response).getName();
        assertThat("Response name is not equal to name send in request",responseBoardName, equalTo(boardName));

        // Save generated board name and board id  from response
        boardIdList.add(createBoard(response).getId());
        boardNameList.add(boardName);
    }

    @AfterMethod
    public void tearDown() {
        // Delete all created boards saved in List
        for (String boardId : boardIdList) {
            Response response = requestBuilder()
                    .setMethod(Method.DELETE)
                    .setPath(BOARD_PATH + boardId)
                    .buildRequest()
                    .sendRequest();
        }
        boardIdList.clear();
        boardNameList.clear();
    }

    @Test(dataProvider = "boardNames", dataProviderClass = DataProviders.class)
    public void createBoardTest(String boardName) {
        Response response = requestBuilder()
                .setMethod(Method.POST)
                .setName(boardName)
                .setPath(BOARD_PATH)
                .buildRequest()
                .sendRequest();

        // Save board id and name from response
        boardIdList.add(createBoard(response).getId());
        boardNameList.add(createBoard(response).getName());

        // Assert server board names equal to generated names (compare Lists)
        List<String> boardNamesOnServer = BoardsList.getBoardNameList();
        assertThat("Board names on server and generated names are not equal",
                boardNamesOnServer, containsInAnyOrder(boardNameList.toArray()));
    }

    @Test
    public void getBoardByIdTest() {
        // get name and id generated at SetUp
        String boardId = boardIdList.get(0);
        String boardName = boardNameList.get(0);

        Response response = requestBuilder()
                .setMethod(Method.GET)
                .setPath(BOARD_PATH + boardId)
                .buildRequest()
                .sendRequest();
        // compare generated name with response name
        String responseBoardName = createBoard(response).getName();
        assertThat("Response name and generated name are not equal", responseBoardName, equalTo(boardName));
    }

    @Test
    public void renameBoardTest() {
        String boardId = boardIdList.get(0);

        Response response = requestBuilder()
                .setMethod(Method.PUT)
                .setPath(BOARD_PATH + boardId)
                .setName(boardNewName)
                .buildRequest()
                .sendRequest();

        // Assert response name equals new name
        String responseBoardName = createBoard(response).getName();
        assertThat("Create response name and generated name are not equal", responseBoardName, equalTo(boardNewName));

        // Assert name on server equals new name
        Response boardGetResponse = requestBuilder()
                .setMethod(Method.GET)
                .setPath(BOARD_PATH + boardId)
                .buildRequest()
                .sendRequest();

        String getBoardName = createBoard(boardGetResponse).getName();
        assertThat("Name(s) from server are not equal to generated",
                responseBoardName, equalTo(boardNewName));

    }

    @Test
    public void deleteBoardTest() {
        String boardId = boardIdList.get(0);
        // delete board saved in List
        requestBuilder()
                .setMethod(Method.DELETE)
                .setPath(BOARD_PATH + boardId)
                .buildRequest()
                .sendRequest();

        // Assert there is no deleted boards on server (compare Lists)
        List<String> boardNamesOnServer = BoardsList.getBoardNameList();
        assertThat("Deleted boards are still present on server",
                boardNamesOnServer, not(containsInAnyOrder(boardNameList.toArray())));
    }
}
