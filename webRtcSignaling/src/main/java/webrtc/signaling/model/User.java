package webrtc.signaling.model;

import webrtc.signaling.type.DeviceType;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 用户实体
 * @license Apache License 2.0
 */
public class User implements Cloneable{

    private String userId;         //用户id
    private String userName;       //用户名
    private String roomKey;        //用户所在房间key
    private DeviceType deviceType; //使用的终端类型
    private Connection connection; //webSocket连接对象

    public User(){}

    public User(String userId, String userName) {
        this.userId = userId;
        this.userName = userName;
    }

    //TODO 在进行json转换时如果包含session会出现死循环，这里直接不给connection赋值
    @Override
    public User clone() {
        try {
            User user = (User) super.clone();
            user.userId = this.userId;
            user.userName = this.userName;
            user.roomKey = this.roomKey;
            user.deviceType = this.deviceType;
            user.connection = null;
            return user;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return new User();
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

    public String getRoomKey() {
        return roomKey;
    }

    public void setRoomKey(String roomKey) {
        this.roomKey = roomKey;
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
