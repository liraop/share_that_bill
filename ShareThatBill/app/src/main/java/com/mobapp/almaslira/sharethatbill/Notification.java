package com.mobapp.almaslira.sharethatbill;

import java.util.Calendar;

/**
 * Created by liraop on 12/7/14.
 */
public class Notification {
    public static final int BILL_CREATED = 1;
    public static final int BILL_EDITED = 2;
    public static final int BILL_DELETED = 3;
    public static final int USER_ADDED = 4;

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
