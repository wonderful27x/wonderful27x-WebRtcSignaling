package webrtc.signaling.model;

import java.io.IOException;
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
    //TODO 这里使用getBasicRemote异步发送消息
    //TODO 之前使用getAsyncRemote同步发送有时会出现问题，
    //TODO 但是在网上看到一些说法，在高并发的情况下两种情况都有可能发生异常 （The remote endpoint was in state [TEXT_FULL_WRITING] which is an invalid state for called method）
    //TODO 这里有待进一步测试和研究，感觉webSocket有点坑，直接封装socket可能更好些
    public void sendMessage(String message){
        if (session.isOpen()){
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                e.printStackTrace();
                LogUtil.logPrint("error when send message,session code: " + session.hashCode() + " error message: " + e.getMessage());
            }
        }else {
            LogUtil.logPrint("error: try to send message with a closed session,hash code: " + session.hashCode() + " message: " + message);
        }
    }
}
