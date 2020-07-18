package webrtc.signaling.core;

import com.google.gson.Gson;
import java.util.List;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import webrtc.signaling.annotation_interface.RoomKeyFactory;
import webrtc.signaling.model.BaseMessage;
import webrtc.signaling.model.Connection;
import webrtc.signaling.model.Event;
import webrtc.signaling.model.NegotiationMessage;
import webrtc.signaling.model.Message;
import webrtc.signaling.model.Room;
import webrtc.signaling.model.User;
import webrtc.signaling.type.DeviceType;
import webrtc.signaling.type.MessageType;
import webrtc.signaling.type.RoomType;
import webrtc.signaling.utils.IdCreator;
import webrtc.signaling.utils.LogUtil;

/**
 * @author wonderful
 * @date 2020-7-?
 * @version 1.0
 * @description 服务端信令交换webSocket-信令服务核心代码-完全面向对象，todo 对象的创建比较频繁，性能消耗较大，后期考虑优化-复用池
 * 我的理解是每一个webSocket连接成功后都会创建一个SignalingWebSocketService.this对象,
 * 在很多网上的教程中也是直接在容器中add(this)
 * TODO 特别注意，这套信令服务和客户端有很多实体是一一对应的，务必保持一致性，否则会出现一些很难排查到的错误
 * @license Apache License 2.0
 */
@ServerEndpoint("/webRtcSignaling/{userId}/{deviceCode}")
public class SignalingWebSocketService implements RoomKeyFactory {

    private RoomManager roomManager;              //房间管理者，里面包含了所有的房间
    private ConnectionManager connectionManager;  //连接管理者，里面包含了所有的连接用户
    private String userId;                        //当前连接用户的id
    private Gson gson;

    public SignalingWebSocketService(){
        LogUtil.logPrint("webSocket 对象创建成功");
        roomManager = RoomManager.getInstance();
        connectionManager = ConnectionManager.getInstance();
        gson = new Gson();
    }

    /**
     * 连接建立成功调用的方法
     * @param session 可选的参数。session为与某个客户端的连接会话，需要通过它来给客户端发送数据
     * @param userId 建立连接时传递的参数，与注解@ServerEndpoint中{userId}对应
     * @param deviceCode 建立连接时传递的参数，与注解@ServerEndpoint中{device}对应
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId, @PathParam("deviceCode")int deviceCode){
        LogUtil.logPrint("session code: " + session.hashCode() + " userId: " + userId + " deviceCode: " + DeviceType.getDeviceType(deviceCode).getType());
        createUserConnection(session,userId,deviceCode);
    }

    /**
     * 连接关闭调用的方法
     * @param session 可选的参数
     */
    @OnClose
    public void onClose(Session session){
        LogUtil.logPrint("webSocket onClose");
        handleMessage(MessageType.LEAVE,null);
    }

    /**
     * 收到客户端消息后调用的方法
     * @param message 客户端发送过来的消息
     * @param session 可选的参数
     */
    @OnMessage
    public void onMessage(Session session,String message){
        LogUtil.logPrint("onMessage: " + message);
        Message messageObject = new Message(message);
        Event event = new Event();
        event.objA = messageObject;
        event.objB = message;
        handleMessage(messageObject.getMessageType(),event);
    }

    /**
     * 发生错误时调用
     * @param session 可选的参数
     * @param error 异常信息
     */
    @OnError
    public void onError(Session session,Throwable error){
        LogUtil.logPrint("error - session code " + session.hashCode() + " : " + error.getMessage());
    }

    //当有人建立了webSocket连接时，创建一个用户连接对象
    private void createUserConnection(Session session,String userId,int deviceCode){
        LogUtil.logPrint("createUserConnection");
        //构建user信息
        userId = IdCreator.getInstance().createId(userId);
        DeviceType deviceType = DeviceType.getDeviceType(deviceCode);

        //构建连接信息
        Connection connection = new Connection();
        connection.setCreateTime(String.valueOf(System.currentTimeMillis()));
        connection.setSession(session);

        //设置到用户中
        User user = new User();
        user.setUserId(userId);
        user.setDeviceType(deviceType);
        user.setConnection(connection);

        //添加到容器中
        connectionManager.put(userId,user);

        //保存userId
        this.userId = userId;

        //连接成功后给客户端发送用户信息
        Event event = new Event();
        event.objA = connectionManager.cloneUser(userId);
        handleMessage(MessageType.CONNECT_OK,event);
    }

    //消息分发
    private void handleMessage(MessageType messageType,Event event){
        LogUtil.logPrint("handleMessage-messageType: " + messageType.getType());
        switch (messageType){
            case CONNECT_OK:
                connectOk(event);
                break;
            case LEAVE:
                someoneLeave();
                break;
            case JOIN:
                someoneJoin(event);
                break;
            case OFFER:
                sendOffer(event);
                break;
            case ANSWER:
                answerOffer(event);
                break;
            case CANDIDATE:
                sendCandidate(event);
                break;
        }
    }

    /**
     * 有人加入了房间，服务器需要做两件事情
     * 一：告诉加入者当前的房间信息，其中最重要的就是房间里的成员id
     * 客户端需要这个id来和房间里的成员交换音视频通话前的必要信息
     * 如果加入者加入的房间不存在，服务器还需要创建这样一个房间
     * 二：通知房间里的所有人有新人来了，让他们做好准备
     * 对于客户端来说其中一和二是两种不同的消息类型，必须区分开来，将一设计成COME类型，二设计成JOIN类型
     * 但是请注意，对于服务端来说只要有人加入房间他收到的类型就只有一种JOIN
     * @param event 上层传递的事件消息
     */
    private void someoneJoin(Event event){
        LogUtil.logPrint("someoneJoin");
        //从event中取出事件信息并转换成对象
        Message message = (Message) event.objA;

        //将Message中的message字段转换成对象，这是Message中的有效数据
        //请注意Event和Message是不同的两个概念，Event只做本端的数据传输，而Message才是C/S两端交换的信息
        BaseMessage<NegotiationMessage, Object> joinMessage = message.transForm(new BaseMessage<NegotiationMessage, Object>() {});

        String userId = joinMessage.getMessage().userId;      //获取申请者id
        String roomId = joinMessage.getMessage().roomId;      //获取申请加入的房间号
        RoomType roomType = joinMessage.getMessage().roomType;//获取申请加入的房间类型,只有在房间不存在创建的时候起作用

        //合成房间钥匙
        String roomKey = roomKeyBuild(roomType,roomId);

        //根据房间id从容器中取出房间信息
        Room room = roomManager.get(roomKey);

        //如果房间不存在则创建一个房间
        if (room == null){
            room = new Room();
            room.setRoomType(roomType);                                       //房间类型
            room.setRoomId(roomId);                                           //房间号
            String createTime = String.valueOf(System.currentTimeMillis());   //创建时间
            room.setCreateTime(createTime);                                   //创建时间
            room.setCreateUserId(userId);                                     //创建用户id
            roomManager.put(roomKey,room);                                    //向容器添加一个房间
        }

        //将用户添加到房间里,并保存用户所在房间key
        room.addMemberId(userId);
        connectionManager.get(userId).setRoomKey(roomKey);

        //给加入者发送房间信息，消息类型COME
        BaseMessage<Room,Object> baseMessage = new BaseMessage<Room, Object>() {};
        baseMessage.setMessageType(MessageType.COME);
        baseMessage.setMessage(room);
        String jsonData = baseMessage.toJson();
        LogUtil.logPrint("someoneJoin,send as COME: " + jsonData);
        connectionManager.getConnection(userId).sendMessage(jsonData);

        //给房间里的其他人发送加入者的信息，消息类型Join
        BaseMessage<User,Object> userMessage = new BaseMessage<User, Object>() {};
        userMessage.setMessageType(MessageType.JOIN);
        userMessage.setMessage(connectionManager.cloneUser(userId));
        jsonData = userMessage.toJson();
        LogUtil.logPrint("someoneJoin,send as JOIN: " + jsonData);
        for (String id:room.getMembers()){
            if (id.equals(userId))continue;
            connectionManager.getConnection(id).sendMessage(jsonData);
        }
    }

    //有人离开房间，通知房间里的所有人
    private void someoneLeave(){
        LogUtil.logPrint("someoneLeave");
        //先做清理工作再发送信息，因为清理工作更加重要，防止发送信息异常时造成清理无法执行
        User remove = connectionManager.remove(userId);
        /**如果移除失败后续操作很有可能造成致命的异常，如向一个已经关闭的session发送消息，这里仅仅打印一个警告，后续需要考虑如何处理*/
        if (remove == null){
            LogUtil.logPrint("warning: user " + userId + " has left but fail to remove from service!!!");
            return;
        }
        //获取用户所在的房间的id
        String roomKey = remove.getRoomKey();
        //如果roomId为null说明用户没有加入任何房间
        if (roomKey == null){
            LogUtil.logPrint("user " + userId + " who was not in any room has left");
            return;
        }
        //获取房间
        Room room = roomManager.get(roomKey);
        if (room == null){
            LogUtil.logPrint("warning: user " + userId + " has left but fail to get his room message!!!");
            return;
        }
        //获取房间成员
        List<String> members = room.getMembers();
        if (members == null){
            LogUtil.logPrint("warning: user " + userId + " has left but fail to get his room members!!!");
            return;
        }

        //从房间成员中移除离开用户
        members.remove(userId);

        //组装信息，最关键的是消息类型和离开用户的id
        BaseMessage<User,Object> baseMessage = new BaseMessage<User, Object>() {};
        baseMessage.setMessageType(MessageType.LEAVE);
        baseMessage.setMessage(remove);
        String jsonData = baseMessage.toJson();
        LogUtil.logPrint("someoneLeave,leave message: " + jsonData);

        //发送消息
        for (String id:members){
            connectionManager.getConnection(id).sendMessage(jsonData);
        }
    }

    //向一方发送自己的媒体协商数据-主动
    private void sendOffer(Event event){
        LogUtil.logPrint("sendOffer");
        messageForward(event);
    }

    //向一方响应自己的媒体协商数据-被动
    private void answerOffer(Event event){
        LogUtil.logPrint("answerOffer");
        messageForward(event);
    }

    //向一方发送自己的网络协商数据
    private void sendCandidate(Event event){
        LogUtil.logPrint("sendCandidate");
        messageForward(event);
    }

    //转发消息
    //注意从消息中取出的userId是目的id，即这条消息是要转发给这个用户的
    //而对于消息的接收者来说，他需要知道这条消息是谁发来的，因此需要替换一下id
    //而上述的信息交换方式并不是唯一的，完全可以让客户端发送一个目的id和自己的id
    private void messageForward(Event event){
        Message message = (Message) event.objA;
        BaseMessage<NegotiationMessage,Object> baseMessage = message.transForm(new BaseMessage<NegotiationMessage, Object>() {});
        //通过这个id可以获取消息接收者的session会话
        String userId = baseMessage.getMessage().userId;
        //将userId替换为自己的id，让接收者知道消息的发送者
        baseMessage.getMessage().userId = this.userId;
        //通过id取出session会话，将消息转发
        connectionManager.getConnection(userId).sendMessage(baseMessage.toJson());
    }

    //连接成功后给客户端发送用户信息
    private void connectOk(Event event){
        LogUtil.logPrint("connectOk");
        BaseMessage<User,Object> baseMessage = new BaseMessage<User, Object>() {};
        User user = (User) event.objA;
        baseMessage.setMessage(user);
        baseMessage.setMessageType(MessageType.CONNECT_OK);
        String jsonData = baseMessage.toJson();
        connectionManager.getConnection(userId).sendMessage(jsonData);
        LogUtil.logPrint("connectOk,send message: " + jsonData);
    }

    //合成房间钥匙
    @Override
    public String roomKeyBuild(RoomType roomType, String roomId) {
        if (roomType == null || roomId == null){
            throw new IllegalArgumentException("房间钥匙合成失败，房间类型或房间号为null！！！");
        }
        return roomType.getType() + "-" + roomId;
    }
}
