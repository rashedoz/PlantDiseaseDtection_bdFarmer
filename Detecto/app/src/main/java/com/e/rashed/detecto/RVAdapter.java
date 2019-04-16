package com.e.rashed.detecto;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.PersonViewHolder>{

    ArrayList<Person> data;


    CustomItemClickListener listener;




    public static class PersonViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView personName;
        TextView personAge;
        ImageView personPhoto;


        PersonViewHolder(View itemView) {
            super(itemView);
            cv = (CardView)itemView.findViewById(R.id.cv);
            personName = (TextView)itemView.findViewById(R.id.person_name);
            personAge = (TextView)itemView.findViewById(R.id.person_age);
            personPhoto = (ImageView)itemView.findViewById(R.id.person_photo);
        }
    }

    List<Person> persons;
    Context mContext;

    RVAdapter(List<Person> persons,Context mContext){
        this.persons = persons;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return persons.size();
    }

    @Override
    public RVAdapter.PersonViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cards_layout, viewGroup, false);
        PersonViewHolder pvh = new PersonViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PersonViewHolder personViewHolder, final int i) {
        personViewHolder.personName.setText(persons.get(i).name);
        personViewHolder.personAge.setText(persons.get(i).age);
        //textview formatting
        personViewHolder.personAge.setMaxLines(4);
        personViewHolder.personAge.setEllipsize(TextUtils.TruncateAt.END);

//        personViewHolder.personPhoto.setImageResource(persons.get(i).photoId);
        Picasso.get().load(persons.get(i).photo_url).into(personViewHolder.personPhoto);

        //OnclickListener
        personViewHolder.personPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intnt = new Intent(mContext,DiseaseDetails.class);
                String name_disease = persons.get(i).name;
                String remedy = persons.get(i).age;
                String pic_url = persons.get(i).photo_url;
                String remedy_url = persons.get(i).remedy_url;

                Log.e("Onclick","clicked :"+name_disease);
                Bundle extras = new Bundle();
                extras.putString("name_disease",name_disease);
                extras.putString("remedy",remedy);
                extras.putString("pic_url",pic_url);
                extras.putString("remedy_url",remedy_url);
                intnt.putExtras(extras);
                mContext.startActivity(intnt);
            }
        });

         }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }




}
