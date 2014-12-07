package com.mobapp.almaslira.sharethatbill;

import java.util.Calendar;

/**
 * Created by liraop on 12/7/14.
 */
public class Notification {

    String owner, description;
    int type;
    Calendar date;

    public Notification(String userName, int notificationType, String description){
        this.description = description;
        this.owner = userName;
        this.type = notificationType;
        this.date = Calendar.getInstance();
    }


    @Override
    public int hashCode(){
        return owner.hashCode() * description.hashCode() * date.hashCode();
    }


}
