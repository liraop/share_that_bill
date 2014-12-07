package com.mobapp.almaslira.sharethatbill;

/**
 * Created by liraop on 12/7/14.
 */
public class Notification {

    String owner, description;
    int type;

    public Notification(String userName, int notificationType, String description){
        this.description = description;
        this.owner = userName;
        this.type = notificationType;
    }



}
