package webrtc.signaling.annotation_interface;

import webrtc.signaling.model.Event;

/**
 * @author wonderful
 * @date 2020-7-19
 * @version 1.0
 * @description 校验接口
 * @license Apache License 2.0
 */
public interface PermissionCheck {
    //校验房间是否是可加入的
    public boolean joinPermissionCheck(Event event);
}
