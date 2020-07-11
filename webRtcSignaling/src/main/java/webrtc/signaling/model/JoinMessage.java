package webrtc.signaling.model;

import webrtc.signaling.type.RoomType;

/**
 * 申请加入房间的消息
 */
public class JoinMessage {
    public String userId;    //申请人id
    public String roomId;    //申请加入的房间id
    public RoomType roomType;//房间类型
}
