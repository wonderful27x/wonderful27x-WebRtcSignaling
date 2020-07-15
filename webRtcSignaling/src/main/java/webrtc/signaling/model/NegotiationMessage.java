package webrtc.signaling.model;

import webrtc.signaling.type.RoomType;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 数据协商消息对象，媒体协商、网络协商数据交换的载体，当然也可以传输其他信息
 * @license Apache License 2.0
 */
public class NegotiationMessage {
    public String userId;    //user id
    public String roomId;    //房间id
    public RoomType roomType;//房间类型
    public String sdp;       //媒体协商、网络协商数据
}
