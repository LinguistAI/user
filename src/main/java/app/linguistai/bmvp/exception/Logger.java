package app.linguistai.bmvp.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import app.linguistai.bmvp.model.enums.LogType;
import lombok.Data;
 
@Data
public class Logger {
    private Logger() {}
    
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yy HH:mm:ss");
    
    public static void log(String msg, LogType logType) {
        String now = LocalDateTime.now().format(formatter);

        System.out.println(String.format("[%s] [%s]: %s", logType.getLogType(), now, msg));
    }

    public static void info(String msg) {
        log(msg, LogType.INFO);
    }
    
    public static void warn(Exception e) {
        log(e.getLocalizedMessage(), LogType.WARN);
    }
   
    public static void warn(String msg) {
        log(msg, LogType.WARN);
    }
    
    public static void error(Exception e) {
        log(e.getLocalizedMessage(), LogType.ERROR); // TODO how to handle cases where we also need to print the stack trace
    }
}
