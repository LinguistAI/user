package app.linguistai.bmvp.consts;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ServiceUris {
    public static final String ML_SERVICE_PROFILE_APP = "/profile";
    public static final String ML_SERVICE_UPDATE_PROFILE = ML_SERVICE_PROFILE_APP + "/update";
    public static final String AWS_SERVICE_REGISTER_TO_SNS = "/sns";
    public static final String AWS_SERVICE_SEND_NOTIFICATION = AWS_SERVICE_REGISTER_TO_SNS + "/send-notification";
}
