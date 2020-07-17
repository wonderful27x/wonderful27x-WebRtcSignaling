package webrtc.signaling.core;

import java.util.concurrent.ConcurrentHashMap;
import webrtc.signaling.model.Connection;
import webrtc.signaling.model.User;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 连接管理类,实际管理的是连接的用户，而用户中保存了webSocket连接
 * @license Apache License 2.0
 */
public class ConnectionManager {

    private static ConnectionManager connectionManager = null;
    //key：用户id，value:连接用户
    private ConcurrentHashMap<String, User> connections;

    private ConnectionManager(){
        connections = new ConcurrentHashMap<>();
    }

    public static ConnectionManager getInstance(){
        if (connectionManager == null){
            synchronized (ConnectionManager.class){
                if (connectionManager == null){
                    connectionManager = new ConnectionManager();
                }
            }
        }
        return connectionManager;
    }

    public int connectionSize(){
        return connections.size();
    }

    public void put(String key,User value){
        connections.put(key,value);
    }

    public User get(String key){
        return connections.get(key);
    }

    /***
     * 从容器中克隆出一个User连接对象
     * 这么设计主要有两个目的，一是为了数据安全，如果仅仅是为了获取连接用户的信息推荐调用这个方法，
     * 这样在操作对象时不会改变当前存活的连接对象的数据，
     * 二是为了解决json转换时出现的死循环问题，User的克隆将会忽略session，避免了json转换时session导致死循环问题
     * 如果需要修改连接对象的数据或为了获取session请务必调用get方法
     * @param userId 用户id
     * @return 返回克隆体
     */
    public User cloneUser(String userId){
        User user = connections.get(userId);
        if (user != null){
            return user.clone();
        }
        return null;
    }

    public Connection getConnection(String userId){
        return get(userId).getConnection();
    }

    public User remove(String userId){
        User remove = connections.remove(userId);
        if (remove != null){
            remove.setConnection(null);
        }
        return remove;
    }
}
