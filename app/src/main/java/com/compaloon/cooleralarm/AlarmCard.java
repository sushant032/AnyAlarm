package com.compaloon.cooleralarm;

import android.app.PendingIntent;
import android.os.Parcel;

import java.io.Serializable;

public class AlarmCard implements Serializable {
    String title;
    int id;
    int hours;
    int mins;
    int secs;
    Parcel pendingIntent;

    public AlarmCard(int id, String title, int hours, int mins, int secs){
        this.id = id;
        this.title = title.isEmpty() ? "AnyAlarm" : title;
        this.hours = hours;
        this.mins = mins;
        this.secs = secs;
    }

    public void setPendingIntent(PendingIntent pendingIntent){
        Parcel parcel = Parcel.obtain();
        pendingIntent.writeToParcel(parcel,0);
        this.pendingIntent = parcel;
    }

    public PendingIntent getPendingIntent(){
        return PendingIntent.readPendingIntentOrNullFromParcel(pendingIntent);
    }

    public void recycleParcel(){
        pendingIntent.recycle();
        pendingIntent = null;
    }
}
