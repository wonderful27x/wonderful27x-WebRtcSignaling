package webrtc.signaling.model;


/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 消息传递对象，端内使用，不会涉及网络传输
 * @license Apache License 2.0
 */
public class Event {
    public int code;
    public String message;
    public Object objA;
    public Object objB;
    public Object objC;
}
