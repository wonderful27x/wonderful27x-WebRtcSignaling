package webrtc.signaling.annotation_interface;

import webrtc.signaling.model.Event;

/**
 * @author wonderful
 * @date 2020-7-18
 * @version 1.0
 * @description 房间钥匙 工厂
 * @license Apache License 2.0
 */
public interface RoomKeyFactory {
    public String roomKeyBuild(Event event);
}
