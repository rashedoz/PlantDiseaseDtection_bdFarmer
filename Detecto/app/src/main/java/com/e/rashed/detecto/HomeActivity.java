package com.e.rashed.detecto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        final Context context = this;

        final MediaPlayer mp;
        mp = MediaPlayer.create(context, R.raw.chobir_menu_te_jan);
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

        ImageView appleIcn =(ImageView) findViewById(R.id.appleicon);
        appleIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,PlantDetails.class));
            }
        });


        ImageView camBtn = (ImageView) findViewById(R.id.camBtn);
        camBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


//                MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.beep );
//                mediaPlayer.start(); // no need to call prepare(); create() does that for you

                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);


            }
        });

        TapTargetView.showFor(this,                 // `this` is an Activity
                TapTarget.forView(findViewById(R.id.camBtn), "ছবির মেনু তে যান ", "এখানে  ক্লিক করলে ছবির মেনু তে যাবে")
                        // All options below are optional
                        .outerCircleColor(R.color.colorAccent)      // Specify a color for the outer circle
                        .outerCircleAlpha(0.96f)            // Specify the alpha amount for the outer circle
                        .targetCircleColor(R.color.colorPrimary)   // Specify a color for the target circle
                        .titleTextSize(50)                  // Specify the size (in sp) of the title text
                        .titleTextColor(R.color.bg)      // Specify the color of the title text
                        .descriptionTextSize(30)            // Specify the size (in sp) of the description text
                        .descriptionTextColor(R.color.corngreen)  // Specify the color of the description text
                        .textColor(R.color.yellow)            // Specify a color for both the title and description text
                        .textTypeface(Typeface.SANS_SERIF)  // Specify a typeface for the text
                        .dimColor(R.color.bg)            // If set, will dim behind the view with 30% opacity of the given color
                        .drawShadow(true)                   // Whether to draw a drop shadow or not
                        .cancelable(false)                  // Whether tapping outside the outer circle dismisses the view
                        .tintTarget(true)                   // Whether to tint the target view's color
                        .transparentTarget(false)           // Specify whether the target is transparent (displays the content underneath)
                                                            // Specify a custom drawable to draw as the target
                        .targetRadius(60),                  // Specify the target radius (in dp)
                new TapTargetView.Listener() {          // The listener can listen for regular clicks, long clicks or cancels
                    @Override
                    public void onTargetClick(TapTargetView view) {
                        super.onTargetClick(view);      // This call is optional

                        Toast.makeText(HomeActivity.this,
                                "Clicked", Toast.LENGTH_LONG).show();
                        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.rog_shomuho );
                        mediaPlayer.start(); // no need to call prepare(); create() does that for you
                        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                mp.reset();
                                mp.release();
                                mp=null;
                            }
                        });
                    }
                });

        if(!mp.isPlaying())
            mp.release();

    }
}
