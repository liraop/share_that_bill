package com.mobapp.almaslira.sharethatbill;

import java.util.Date;

/**
 * Created by liraop on 12/7/14.
 */
public class Notification {

    String owner, description;
    int type;
    Date date;

    public Notification(String userName, int notificationType, String description, Date date){
        this.description = description;
        this.owner = userName;
        this.type = notificationType;
        this.date = date;
    }



}
