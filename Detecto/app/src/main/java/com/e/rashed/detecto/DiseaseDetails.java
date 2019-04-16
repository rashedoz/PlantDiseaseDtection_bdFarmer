package com.e.rashed.detecto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class DiseaseDetails extends AppCompatActivity {

    String name_disease;
    String remedy ;
    String pic_url;
    String remedy_url;

    TextView dName;
    TextView remedy_word;
    ImageView pic;
    TextView remedy_url_txt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disease_details);

        //Get Extras
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                name_disease= null;
                remedy = null;
                pic_url = null;
                remedy_url = null;
            } else {
                name_disease= extras.getString("name_disease");
                remedy= extras.getString("remedy");
                pic_url= extras.getString("pic_url");
                remedy_url= extras.getString("remedy_url");
            }
        } else {
            name_disease= (String) savedInstanceState.getSerializable("name_disease");
            remedy= (String) savedInstanceState.getSerializable("remedy");
            pic_url= (String) savedInstanceState.getSerializable("pic_url");
            remedy_url= (String) savedInstanceState.getSerializable("remedy_url");
        }

        dName = (TextView) findViewById(R.id.disease_name);
        remedy_word = (TextView)findViewById(R.id.remedy);
        pic = (ImageView)findViewById(R.id.imview);
        remedy_url_txt = (TextView)findViewById(R.id.remedy_url);



        String linkedText = "<b>বিস্তারিত::</b>  " +
                String.format("<a href=\"%s\">এখানে ক্লিক করলে বিস্তারিত জানতে পারবেন </a> ", remedy_url);

        remedy_url_txt.setText(Html.fromHtml(linkedText));
        remedy_url_txt.setMovementMethod(LinkMovementMethod.getInstance());


        dName.setText(name_disease);
        remedy_word.setText(remedy);
        //remedy_url_txt.setText(remedy_url);

//        personViewHolder.personPhoto.setImageResource(persons.get(i).photoId);
        Picasso.get().load(pic_url).into(pic);

    }
}
