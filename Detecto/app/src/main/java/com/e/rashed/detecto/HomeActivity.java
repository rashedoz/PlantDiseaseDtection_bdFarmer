package com.e.rashed.detecto;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Context context = this;

        final MediaPlayer mp;
        mp = MediaPlayer.create(context, R.raw.beep);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                // TODO Auto-generated method stub
                mp.reset();
                mp.release();
                mp=null;
            }
        });
        mp.start();


        ImageView camBtn = (ImageView) findViewById(R.id.camBtn);
        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent i = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(i);

                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.beep);
                mediaPlayer.start(); // no need to call prepare(); create() does that for you


            }
        });
    }
}
