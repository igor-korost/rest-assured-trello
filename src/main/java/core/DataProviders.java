package core;

import org.testng.annotations.DataProvider;
import static constants.TestData.*;

public class DataProviders {

    @DataProvider
    public Object[][] boardNames() {
        int random = (int) (Math.random()*1000);
        return new Object[][]{
                {BOARD_NAME + " with random #" + random},
                {BOARD_NAME_SPECIAL_CHARACTERS + " with random #" + random},
                {BOARD_NAME_RUSSIAN + " with random #" + random},
        };
    }

    @DataProvider
    public Object[][] boardNewNames() {
        int random = (int) (Math.random()*1000);
        return new Object[][]{
                {BOARD_NEW_NAME + " 1 with random #" + random},
                {BOARD_NEW_NAME + " 2 with random #" + random},
                {BOARD_NEW_NAME + " 3 with random #" + random}
        };
    }
}
