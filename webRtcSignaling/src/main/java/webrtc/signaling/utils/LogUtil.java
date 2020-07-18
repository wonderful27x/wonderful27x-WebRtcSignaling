package webrtc.signaling.utils;

import java.util.logging.Logger;
import webrtc.signaling.model.Config;

/**
 * 日志工具里
 */
public class LogUtil {

    private static Logger log = Logger.getLogger("webRtc");

    public static void logPrint(String message){
        if (Config.LOG_ENABLE){
            //System.out.println(message);
            log.info(message);
        }
    }
}