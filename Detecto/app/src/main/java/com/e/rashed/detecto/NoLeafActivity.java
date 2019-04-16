package com.e.rashed.detecto;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class NoLeafActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_leaf);

        ImageView cambMneu = (ImageView) findViewById(R.id.camBtnRes);

        cambMneu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(NoLeafActivity.this,MainActivity.class);
                startActivity(i);
            }
        });



    }
}
