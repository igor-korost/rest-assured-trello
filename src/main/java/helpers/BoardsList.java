package helpers;

import beans.Board;
import io.restassured.http.Method;
import io.restassured.response.Response;

import java.util.ArrayList;
import java.util.List;

import static core.TrelloServiceObj.createBoardList;
import static core.TrelloServiceObj.requestBuilder;
import static constants.Endpoints.*;

public class BoardsList {

    public static List<String> getBoardIdList() {
        Response allBoards = requestBuilder()
                .setMethod(Method.GET)
                .setPath(ALL_BOARD_PATH)
                .buildRequest()
                .sendRequest();
        List<Board> boardList = createBoardList(allBoards);
        List<String> boardIdList = new ArrayList<>();

        for (Board board : boardList) {
            boardIdList.add(board.getId());
        }
        return boardIdList;
    }

    public static List<String> getBoardNameList() {
        Response allBoards = requestBuilder()
                .setMethod(Method.GET)
                .setPath(ALL_BOARD_PATH)
                .buildRequest()
                .sendRequest();
        List<Board> boardList = createBoardList(allBoards);
        List<String> boardNameList = new ArrayList<>();

        for (Board board : boardList) {
            boardNameList.add(board.getName());
        }
        return boardNameList;
    }
}

