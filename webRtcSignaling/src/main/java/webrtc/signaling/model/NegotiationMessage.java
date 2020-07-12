package webrtc.signaling.model;

import webrtc.signaling.type.RoomType;

/**
 * 数据协商消息对象
 */
public class NegotiationMessage {
    public String userId;    //user id
    public String roomId;    //房间id
    public RoomType roomType;//房间类型
    public String sdp;       //媒体协商、网络协商数据
}
