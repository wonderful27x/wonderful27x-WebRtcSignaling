package webrtc.signaling.model;

import javax.websocket.Session;

/**
 * 与WebSocket的连接对象
 */
public class Connection {

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
        session.getAsyncRemote().sendText(message);
    }
}
