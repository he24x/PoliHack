package com.example.commit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import Model.User;

public class MainMenu extends AppCompatActivity {

    TextView stats;
    CardView calendar1,whatsaapp;
    TextView user1, stranger;
    ProgressBar progressBar;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    int calendar=0;

    private static final long START_TIME_IN_MILLIS = 10000;
    public static final String SHARED_PREFS = "sharedPreferences";

    private TextView mTextViewCountDown;

    private CountDownTimer mCountDownTimer;
    public static final String Progress = "Progress";
    private boolean mTimerRunning;
    int progress=15;

    private long mTimeLeftInMillis;
    private long mEndTime;

    int progress_copy;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        stats = findViewById(R.id.text_for_user);
        progressBar = findViewById(R.id.progressBar4);
        calendar1 = findViewById(R.id.calendar);
        whatsaapp = findViewById(R.id.whatsapp);
        user1 = findViewById(R.id.user_name);
        mTextViewCountDown = findViewById(R.id.text_view_countdown);
        progressBar.setProgress(progress);
        stranger = findViewById(R.id.chatText);

        stranger.setText("Chat with a \n" +
                "stranger");

        stats.setText("You have " + progress + " days of quarantine left!");
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                user1.setText("Welcome back, " + user.getUsername());
                //if(user.getImageURL().equals("default"))
                //{
                //    pfp.setImageResource(getIntIds(images));
                //}
                //else
                //{
                //    Glide.with(MainMenu.this).load(user.getImageURL()).into(pfp);
                //}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        calendar1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity();
            }
        });

        whatsaapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openActivity2();
            }
        });

        SharedPreferences preferes = getSharedPreferences("preferes", MODE_PRIVATE);
        boolean firstStart = preferes.getBoolean("firstStart", true);

        if(firstStart){
            showStartDialog();
            if (mTimerRunning) {
                pauseTimer();
            } else {
                startTimer();
            }
        }
    }

    public void openActivity(){
        Intent intent = new Intent (MainMenu.this,Calendar.class);
        startActivity(intent);
    }

    public void openActivity2(){
        Intent intent = new Intent (MainMenu.this, ChWhatsapp.class);
        startActivity(intent);
    }

    private void startTimer(){
        mEndTime = System.currentTimeMillis() + mTimeLeftInMillis;

        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning=false;
                progress--;
                stats.setText("You have " + progress + " days of quarantine left!");
                progressBar.setProgress(progress);
                resetTimer();
                startTimer();
                if(progress==0){
                    pauseTimer();
                    stats.setText("You're quarantine ended!");
                }
            }
        }.start();

        mTimerRunning = true;
    }

    private void pauseTimer(){
        mCountDownTimer.cancel();
    }

    private void resetTimer(){
        mTimeLeftInMillis = START_TIME_IN_MILLIS;
        updateCountDownText();
    }

    private void updateCountDownText(){
        int minutes = (int) (mTimeLeftInMillis/1000)/60;
        int seconds = (int) (mTimeLeftInMillis/1000)%60;

        String timeLeftFormatted = String.format(Locale.getDefault(),"%02d:%02d", minutes, seconds);

        mTextViewCountDown.setText(timeLeftFormatted);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong("millisLeft", mTimeLeftInMillis);
        editor.putBoolean("timerRunning", mTimerRunning);
        editor.putLong("endTime", mEndTime);
        editor.apply();
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }
    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        mTimeLeftInMillis = prefs.getLong("millisLeft", START_TIME_IN_MILLIS);
        mTimerRunning = prefs.getBoolean("timerRunning", false);
        updateCountDownText();
        if (mTimerRunning) {
            mEndTime = prefs.getLong("endTime", 0);
            mTimeLeftInMillis = mEndTime - System.currentTimeMillis();
            startTimer();
        }
    }

    private void showStartDialog(){
        new AlertDialog.Builder(this)
                .setTitle("Welcome to commit!")
                .setMessage("Unfortunately you got infected! We hope that you are feeling well. If you feel lonely you can chat with random strangers by using our app.")
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int which) {
                        dialogInterface.dismiss();
                    }
                })
                .create().show();
        SharedPreferences preferes = getSharedPreferences("preferes", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferes.edit();
        editor.putBoolean("firstStart", false);
        editor.apply();
    }
    @Override
    public void finish() {
        super.finish();

    }

    public void saveData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor edit = sharedPreferences.edit();

        edit.putInt(Progress, progress);
        edit.apply();
    }

    public void loadData(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        progress_copy = sharedPreferences.getInt(Progress,0);
    }

    public void updateView(){
        progress=progress_copy;
    }

}