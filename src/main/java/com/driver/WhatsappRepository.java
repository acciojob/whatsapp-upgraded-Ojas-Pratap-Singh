package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class WhatsappRepository {


    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name, String mobile) throws Exception {
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }
        userMobile.add(mobile);
        return "SUCCESS";
    }

    public Group createGroup(List<User> users) {
        // The list contains at least 2 users where the first user is the admin. A group has exactly one admin.
        // If there are only 2 users, the group is a personal chat and the group name should be kept as the name of the second user(other than admin)
        // If there are 2+ users, the name of group should be "Group count". For example, the name of first group would be "Group 1", second would be "Group 2" and so on.
        // Note that a personal chat is not considered a group and the count is not updated for personal chats.
        // If group is successfully created, return group.

        //For example: Consider userList1 = {Alex, Bob, Charlie}, userList2 = {Dan, Evan}, userList3 = {Felix, Graham, Hugh}.
        //If createGroup is called for these userLists in the same order, their group names would be "Group 1", "Evan", and "Group 2" respectively.

        Group group=null;

        if(users.size()==2){
            String name=users.get(1).getName();
            group= new Group(name,2);
        }
        else{
            customGroupCount++;
            String name="Group "+customGroupCount;
            group= new Group(name,users.size());
        }
        groupUserMap.put(group,users);
        return group;


    }

    public int createMessage(String content) {
        messageId++;
        Message message = new Message(messageId,content);
        return messageId;
    }

    public int sendMessage(Message message, User sender, Group group) throws Exception{
        if(groupUserMap.containsKey(group)==false){
            throw new Exception("Group does not exist");
        }else{
            List<User> userList = groupUserMap.get(group);
            if(userList.contains(sender)==false){
                throw new Exception("You are not allowed to send message");
            }else{
                List<Message> messageList = new ArrayList<>();
                if(groupMessageMap.containsKey(group)){
                    messageList = groupMessageMap.get(group);
                }
                messageList.add(message);
                groupMessageMap.put(group,messageList);
            }
        }

        return groupMessageMap.get(group).size();
    }

    public String changeAdmin(User approver, User user, Group group) throws Exception {
        if(!groupUserMap.containsKey(group)){
            throw new Exception("Group does not exist");
        }
        else {
            List<User> listUsers= groupUserMap.get(group);
            if(!listUsers.get(0).equals(approver)){
                throw  new Exception("Approver does not have rights");
            }
            if(!listUsers.contains(user)){
                throw  new Exception("User is not a participant");
            }
            User newAdmin=user;
            listUsers.remove(approver);
            listUsers.remove(user);
            listUsers.add(0,newAdmin);
            listUsers.add(approver);
        }
        return "SUCCESS";
    }

    public int removeUser(User user) throws Exception {
        int flag=0;
        for (Group group:groupUserMap.keySet()){
            List<User> userList= groupUserMap.get(group);
            if(user.equals(userList.get(0))){
                throw new Exception("Cannot remove admin");
            }
            for(int i=1;i<userList.size();i++){
                if(user.equals(userList.get(i))){
                    flag=1;
                    groupUserMap.get(group).remove(user);
                    break;
                }
            }
        }
        if(flag==0) throw new Exception("User not found");
        return messageId+customGroupCount;

    }

    public String findMessage(Date start, Date end, int k) {
        return "SUCCESS";
    }
}
