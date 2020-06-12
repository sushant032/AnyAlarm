package com.compaloon.cooleralarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MaterialDesignHomePage extends AppCompatActivity {
    Context context;
    LinearLayout scrollView;
    MaterialAlertDialogBuilder builder;
    AlertDialog dialog;
    public static  HashSet<AlarmCard> alarmCards = new HashSet<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        setContentView(R.layout.activity_material_design_home_page);

        checkPermissions();

        scrollView = findViewById(R.id.allcards);

        // Init previous cards
        initPreviousCards();

        FloatingActionButton fab = findViewById(R.id.addCard);
        final View form = getLayoutInflater().inflate(R.layout.time_input_form,null);
        builder = new MaterialAlertDialogBuilder(this);
        builder.setView(form)
                .setPositiveButton("CREATE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                try {
                    String title = ((EditText) form.findViewById(R.id.cardTitle)).getText().toString();
                    int hours = Integer.parseInt(checkEmptyString(((EditText) form.findViewById(R.id.hours)).getText().toString()));
                    int mins = Integer.parseInt(checkEmptyString(((EditText) form.findViewById(R.id.mins)).getText().toString()));
                    int seconds = Integer.parseInt(checkEmptyString(((EditText) form.findViewById(R.id.secs)).getText().toString()));
                    AlarmCard card = new AlarmCard(new Random().nextInt(999_999_999),title, hours, mins, seconds);
                    alarmCards.add(card);
                    store_cards(); // Store all the cards in shared preference
                    scrollView.addView(newCard(card));
                    Toast.makeText(getApplicationContext(), "New alarm created", Toast.LENGTH_LONG).show();
                }catch (Exception e){
                    Toast.makeText(getApplicationContext(), "Incorrect values", Toast.LENGTH_LONG).show();
                }
            }
        })
        .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.show();
            }
        });
    }
    private String checkEmptyString(String string){
        if(string.isEmpty())
            return "0";
        return string;
    }
    private void checkPermissions(){
        if(!Settings.canDrawOverlays(this)){
            MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
            builder.setTitle("Permission Required")
                    .setMessage("To wake up the app from background we require this permission. On yes you will be taken to settings where you can grant us this permission")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            startActivity(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Toast.makeText(getApplicationContext(), "This permission is required", Toast.LENGTH_LONG).show();
                        }
                    });
            builder.create().show();
        }
    }

    private void initPreviousCards(){
        scrollView.removeAllViews();
        Type listOfAlarmCards = new TypeToken<Set<AlarmCard>>() {}.getType();
        alarmCards = new Gson().fromJson(PreferenceManager.getDefaultSharedPreferences(context).getString("CARD_LIST", ""), listOfAlarmCards);
        if(alarmCards!=null){
            for(AlarmCard card: alarmCards)
                scrollView.addView(newCard(card));
        }else{
            alarmCards = new HashSet<>();
        }
    }

    private void store_cards(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Type listOfAlarmCards = new TypeToken<Set<AlarmCard>>() {}.getType();
        String strCards = new Gson().toJson(alarmCards, listOfAlarmCards);
        preferences.edit().putString("CARD_LIST", strCards).apply();
    }

    private MaterialCardView newCard(final AlarmCard card){

        MaterialCardView cardView = new MaterialCardView(context);
        MaterialCardView.LayoutParams params = new MaterialCardView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(48,32,48,0);
        cardView.setLayoutParams(params);
        cardView.setCardBackgroundColor(Color.parseColor("#ffffff"));
        cardView.setElevation(6);
        cardView.setMinimumHeight(300);

        RelativeLayout relativeLayout = new RelativeLayout(context);
        RelativeLayout.LayoutParams relativeLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        relativeLayout.setLayoutParams(relativeLayoutParams);
        relativeLayout.setPadding(32,16,16,16);
        relativeLayout.setMinimumHeight(300);
        cardView.addView(relativeLayout);

        RelativeLayout.LayoutParams auxRelParams;

        // Card Title Text View
        MaterialTextView cardTitle = new MaterialTextView(context);
        cardTitle.setId(View.generateViewId());
        cardTitle.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        cardTitle.setText(card.title);
        cardTitle.setTextSize(24);
        auxRelParams = (RelativeLayout.LayoutParams) cardTitle.getLayoutParams();
        auxRelParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        auxRelParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relativeLayout.addView(cardTitle);

        // Card Text
        MaterialTextView cardText = new MaterialTextView(context);
        cardText.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        cardText.setText("Ring alarm in \n"+card.hours+" H "+card.mins+" M "+card.secs+" S");
        cardText.setMaxWidth(600);
        auxRelParams = (RelativeLayout.LayoutParams) cardText.getLayoutParams();
        auxRelParams.addRule(RelativeLayout.BELOW, cardTitle.getId());
        auxRelParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeLayout.addView(cardText);

        // Card Text
        MaterialTextView cross = new MaterialTextView(context);
        cross.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        cross.setBackground(getDrawable(R.drawable.cross));
        cross.setTextColor(Color.parseColor("#A32727"));
        auxRelParams = (RelativeLayout.LayoutParams) cross.getLayoutParams();
        auxRelParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alarmCards.remove(card);
                scrollView.removeAllViews();
                store_cards();
                initPreviousCards();
            }
        });
        relativeLayout.addView(cross);


        final MaterialButton btn = new MaterialButton(context);
        final MaterialButton stopBtn = new MaterialButton(context);

        // Start Button
        btn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        btn.setText("START");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startAlert(card);
            }
        });
        auxRelParams = (RelativeLayout.LayoutParams) btn.getLayoutParams();
        auxRelParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        auxRelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addView(btn);

        // Stop Button
        stopBtn.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));
        stopBtn.setText("STOP");
        stopBtn.setBackgroundColor(Color.parseColor("#ff0000"));
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopAlert(card);
            }
        });
        auxRelParams = (RelativeLayout.LayoutParams) stopBtn.getLayoutParams();
        auxRelParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        auxRelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeLayout.addView(stopBtn);
        if(card.pendingIntent==null){
            stopBtn.setVisibility(View.INVISIBLE);
            btn.setVisibility(View.VISIBLE);
        }
        else{
            btn.setVisibility(View.INVISIBLE);
            stopBtn.setVisibility(View.VISIBLE);
        }
        return  cardView;
    }

    public void stopAlert(AlarmCard card){
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = card.getPendingIntent();
        if(pendingIntent!=null){
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }
        card.recycleParcel();
        store_cards();
        initPreviousCards();
    }

    public void startAlert(AlarmCard card){
        int i = card.hours*60*60+60*card.mins+card.secs;
        int req = new Random().nextInt(999_999_999); // 234324243
        Intent intent = new Intent(getApplicationContext(), CoolerBroadcastReceiver.class);
        intent.putExtra("cardId",card.id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this.getApplicationContext(), req, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                + (i * 1000), pendingIntent);
        card.setPendingIntent(pendingIntent);
        store_cards();
        initPreviousCards();
        Toast.makeText(this, "Alarm Set",Toast.LENGTH_LONG).show();
    }
}