/**
 *
 * ShareThatBill
 *
 * CSE444 - Mobile Application Development
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
 * This class is used to pack together information about a Bill.
 */
public class Bill {
    String billName;
    String groupName;
    Float billValue;
    Calendar billDate;
    Float billLocationLatitute = 0.0f;
    Float billLocationLongitude = 0.0f;
    boolean locationIsSet = false;
    String billPicturePath;

}
