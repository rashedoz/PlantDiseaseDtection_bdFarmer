package com.e.rashed.detecto;

import android.app.Activity;
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



        /* Icon Plant Details activity*/
        ImageView appleIcn =(ImageView) findViewById(R.id.appleicon);
        ImageView grapeIcn =(ImageView) findViewById(R.id.grapeicon);
        ImageView cornIcn =(ImageView) findViewById(R.id.cornIcon);
        ImageView potatoIcn =(ImageView) findViewById(R.id.potatoIcon);
        ImageView tomatoIcn =(ImageView) findViewById(R.id.tomatoIcon);
        ImageView tBtn =(ImageView) findViewById(R.id.tBtn);

        appleIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PlantDetails.class);
                i.putExtra("plantName","Apple");
                startActivity(i);

            }
        });
        grapeIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PlantDetails.class);
                i.putExtra("plantName","Grape");
                startActivity(i);

            }
        });
        cornIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PlantDetails.class);
                i.putExtra("plantName","Corn");
                startActivity(i);

            }
        });
        potatoIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PlantDetails.class);
                i.putExtra("plantName","Potato");
                startActivity(i);

            }
        });
        tomatoIcn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),PlantDetails.class);
                i.putExtra("plantName","Tomato");
                startActivity(i);

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

        tBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),TutorialActivity.class);
                startActivity(i);
            }
        });

        final Context m = this;

//        Toast.makeText(HomeActivity.this,
//                "Clicked", Toast.LENGTH_LONG).show();
        final MediaPlayer mediaPlayer = MediaPlayer.create(context, R.raw.awattadhin_rogshomuho );
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.reset();
                mp.release();
                mp=null;
                final MediaPlayer mp2;
                mp2 = MediaPlayer.create(context, R.raw.chobir_menu_te_jabe);
                mp2.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        // TODO Auto-generated method stub
                        mp.reset();
                        mp.release();
                        mp=null;
                    }
                });
                mp2.start();
                targetView(m);
            }
        });





    }

    public void targetView(Context mContext){

        TapTargetView.showFor((Activity) mContext,                 // `this` is an Activity
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

                    }
                });
    }
}
