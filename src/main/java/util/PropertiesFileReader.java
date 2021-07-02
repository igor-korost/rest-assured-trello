package util;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertiesFileReader {

    public static String getProperty(String name) {
        String propertiesPath = "src/main/resources/apiToken.properties";
        String value = "";
        Properties properties = new Properties();

        try (InputStream inputStream = new FileInputStream(propertiesPath)) {
            properties.load(inputStream);
            value = properties.getProperty(name);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("IO error with properties file");
        }
        return value;
    }
}
