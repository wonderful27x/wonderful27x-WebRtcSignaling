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
        User user = connections.get(key);
        if (user != null){
            return user.clone();
        }
        return null;
    }

    public Connection getConnection(String userId){
        return connections.get(userId).getConnection();
    }

    public void remove(String userId){
        User remove = connections.remove(userId);
        if (remove != null){
            remove.setConnection(null);
        }
    }
}
