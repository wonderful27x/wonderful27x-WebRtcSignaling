package webrtc.signaling.type;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 终端类型
 * @license Apache License 2.0
 */
public enum DeviceType {

    COMPUTER(0,"NORMAL"),         //电脑
    PHONE(1,"AUDIO_ONLY"),        //手机
    DEFAULT(-1,"DEFAULT");        //默认值

    private int code;
    private String type;

    DeviceType(int code, String type){
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static DeviceType getDeviceType(int code){
        for (DeviceType type:values()){
            if (type.getCode() == code){
                return type;
            }
        }
        return DEFAULT;
    }
}
