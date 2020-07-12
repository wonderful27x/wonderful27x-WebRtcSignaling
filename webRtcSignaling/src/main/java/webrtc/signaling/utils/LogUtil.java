package webrtc.signaling.utils;

import webrtc.signaling.model.Config;

/**
 * 日志工具里
 */
public class LogUtil {

    public static void logPrint(String message){
        if (Config.LOG_ENABLE){
            System.out.println(message);
        }
    }
}
