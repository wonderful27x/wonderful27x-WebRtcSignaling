package webrtc.signaling.model;

import java.util.ArrayList;
import java.util.List;
import webrtc.signaling.type.RoomType;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 房间实体
 * @license Apache License 2.0
 */
public class Room {

    private RoomType roomType;        //房间类型
    private String roomId;            //房间id
    private String createTime;        //创建时间
    private String createUserId;      //创建用户id
    private List<String> membersId;   //房间里的成员id，包括创建用户

    public Room(){
        membersId = new ArrayList<>();
    }

    public RoomType getRoomType() {
        return roomType;
    }

    public void setRoomType(RoomType roomType) {
        this.roomType = roomType;
    }

    public String getRoomId() {
        return roomId;
    }

    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public List<String> getMembers() {
        return membersId;
    }

    public void addMemberId(String userId){
        this.membersId.add(userId);
    }

    public void addMembersId(List<String> membersId){
        this.membersId.addAll(membersId);
    }

    public void remove(String userId){
        membersId.remove(userId);
    }
}
