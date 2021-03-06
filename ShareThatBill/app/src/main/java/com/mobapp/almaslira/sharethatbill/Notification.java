/**
 *
 * ShareThatBill
 *
 * CSE444 - Mobile Application Programming
 * Prof. Robert J. Irwin
 *
 * Team:
 * Jose E. Almas de Jesus Junior - jeajjr@gmail.com
 * Pedro de Oliveira Lira - pedulira@gmail.com
 *
 */

package com.mobapp.almaslira.sharethatbill;

import java.util.Calendar;

/**
 * This class is used to pack information of a group notification. Notifications are created when:
 * - a bill is created
 * - a bill is edited
 * - a bill is deleted
 * - an user is added
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
        return owner.hashCode() * description.hashCode() * date.getTime().hashCode();
    }


}
