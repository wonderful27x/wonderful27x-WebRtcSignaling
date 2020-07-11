package webrtc.signaling.type;

/**
 * 消息类型,右边的注释是站在客户端的角度来看的
 */
public enum  MessageType {

    CONNECT_OK(0,"CONNECT_OK"),//webSocket连接成功
    COME(1,"COME"),            //自己加入房间，即自己主动加入一个房间
    JOIN(2,"JOIN"),            //有新人加入房间，即有新人加入自己所在的房间
    LEAVE(3,"LEAVE"),          //有人离开房间，即有人离开了自己所在的房间
    OFFER(4,"OFFER"),          //有人发起了媒体协商，即有人向自己发送了他的媒体协商数据
    ANSWER(5,"ANSWER"),        //响应媒体协商，即自主动给别人发送了媒体协商数据后对方给自己响应他的媒体协商数据
    CANDIDATE(6,"CANDIDATE"),  //有人发起了网络协商，即有人向自己发送了他的网络协商数据
    DEFAULT(-1,"DEFAULT");     //默认值

    private int code;
    private String type;

    MessageType(int code,String type){
        this.code = code;
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public String getType() {
        return type;
    }

    public static MessageType getMessageType(int code){
        for (MessageType type:values()){
            if (type.getCode() == code){
                return type;
            }
        }
        return DEFAULT;
    }
}