package com.compaloon.cooleralarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Set;


public class CoolerBroadcastReceiver extends BroadcastReceiver {
    Set<AlarmCard> alarmCards;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent("com.compaloon.cooleralarm.StopAlarm");
        getAlarmCards(context);
        AlarmCard card = null;
        for(AlarmCard alarmCard: alarmCards)
            if(alarmCard.id == intent.getIntExtra("cardId",0)){
                card = alarmCard;
                break;
            }
        if(card!=null)
            card.recycleParcel();
        store_cards(context); // Store cards after making pending intent null
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        Toast.makeText(context, "Alarm Ringing", Toast.LENGTH_LONG).show();
    }


    private void getAlarmCards(Context context){
        Type listOfAlarmCards = new TypeToken<Set<AlarmCard>>() {}.getType();
        alarmCards = new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(context).getString("CARD_LIST", ""), listOfAlarmCards);
    }

    private void store_cards(Context context){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Type listOfAlarmCards = new TypeToken<Set<AlarmCard>>() {}.getType();
        String strCards = new Gson().toJson(alarmCards, listOfAlarmCards);
        preferences.edit().putString("CARD_LIST", strCards).apply();
    }
}
