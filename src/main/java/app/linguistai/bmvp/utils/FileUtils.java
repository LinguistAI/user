package app.linguistai.bmvp.utils;

import app.linguistai.bmvp.request.wordbank.QPredefinedWordList;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.InputStream;

public class FileUtils {

    public static QPredefinedWordList readPredefinedWordListFromYamlFile(String filePath) throws Exception {
        InputStream inputStream = FileUtils.class.getClassLoader().getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("File not found: " + filePath);
        }
        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        return objectMapper.readValue(inputStream, QPredefinedWordList.class);
    }
}