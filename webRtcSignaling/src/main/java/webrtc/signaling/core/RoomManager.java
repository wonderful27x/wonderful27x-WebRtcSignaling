package webrtc.signaling.core;

import java.util.concurrent.ConcurrentHashMap;
import webrtc.signaling.model.Room;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 房间管理类
 * @license Apache License 2.0
 */
public class RoomManager {

    private static RoomManager roomManager = null;
    //聊天房间线程安全集合key:房间类型+房间id（type-id） value：房间对象
    private ConcurrentHashMap<String, Room> chatRooms;

    private RoomManager(){
        chatRooms = new ConcurrentHashMap<>();
    }

    //单列模式
    public static RoomManager getInstance(){
        if (roomManager == null){
            synchronized (RoomManager.class){
                if (roomManager == null){
                    roomManager = new RoomManager();
                }
            }
        }
        return roomManager;
    }

    public void put(String key,Room value){
        chatRooms.put(key,value);
    }

    public Room get(String key){
        return chatRooms.get(key);
    }

    public int roomSize(){
        return chatRooms.size();
    }
}
