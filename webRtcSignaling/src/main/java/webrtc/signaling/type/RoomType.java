package webrtc.signaling.type;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 房间类型
 * @license Apache License 2.0
 */
public enum RoomType {

    NORMAL(0,"NORMAL"),                //普通房间，支持音视频
    AUDIO_ONLY(1,"AUDIO_ONLY"),        //只支持音频
    VIDEO_ONLY(2,"VIDEO_ONLY"),        //只支持视频
    DEFAULT(-1,"DEFAULT");             //默认值

    private int code;
    private String type;

    RoomType(int code,String type){
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static RoomType getRooType(int code){
        for (RoomType type:values()){
            if (type.getCode() == code){
                return type;
            }
        }
        return DEFAULT;
    }
}
