package webrtc.signaling.model;

import javax.websocket.Session;
import webrtc.signaling.utils.LogUtil;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 与WebSocket的连接对象，Session具有会话功能
 * @license Apache License 2.0
 */
public class Connection{

    private String createTime;//创建时间
    private Session session;  //会话session

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    //通过session发送信息
    public void sendMessage(String message){
        if (session.isOpen()){
            session.getAsyncRemote().sendText(message);
        }else {
            LogUtil.logPrint("error: try to send message with a closed session,hash code: " + session.hashCode() + " message: " + message);
        }
    }
}
