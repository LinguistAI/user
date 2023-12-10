package app.linguistai.bmvp.exception;

import lombok.Data;
 
@Data
public class ExceptionLogger {
    private static final boolean NEXT_LINE_FLAG = false; 
    
    public static String log(Exception e) {
        String msg = "";
        msg += (e.getMessage().equals(e.getLocalizedMessage())) ? e.getLocalizedMessage()
                : e.getLocalizedMessage() + ": " + e.getMessage();
        if (NEXT_LINE_FLAG) msg += "\n";
        return msg;
    }

    public static String warn(Exception e) {
        String msg = "[WARNING] ";
        msg += (e.getMessage().equals(e.getLocalizedMessage())) ? e.getLocalizedMessage()
                : e.getLocalizedMessage() + ": " + e.getMessage();
        if (NEXT_LINE_FLAG) msg += "\n";
        return msg;
    }

    public static String error(Exception e) {
        String msg = "[ERROR] ";
        msg += (e.getMessage().equals(e.getLocalizedMessage())) ? e.getLocalizedMessage()
                : e.getLocalizedMessage() + ": " + e.getMessage();
        if (NEXT_LINE_FLAG) msg += "\n";
        return msg;
    }
}
