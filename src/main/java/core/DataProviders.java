package core;

import org.testng.annotations.DataProvider;

import java.util.UUID;

public class DataProviders {

    public static final String boardName = generateString();
    public static final String boardNewName = generateString();

    // random alpha-numeric String generator (UUID)
    public static String generateString() {
        return UUID.randomUUID().toString();
    }

    @DataProvider
    public Object[][] boardNames() {
        return new Object[][]{
                {generateString()},
                {generateString()},
        };
    }
}

