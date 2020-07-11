package webrtc.signaling.model;

import webrtc.signaling.type.DeviceType;

/**
 * 用户实体
 */
public class User {

    private DeviceType deviceType;       //使用的终端类型
    private String userId;         //用户id
    private String userName;       //用户名
    private Connection connection; //webSocket连接对象

    public User(){}

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public DeviceType getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(DeviceType deviceType) {
        this.deviceType = deviceType;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
