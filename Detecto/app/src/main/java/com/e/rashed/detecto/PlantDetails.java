package com.e.rashed.detecto;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlantDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public List<Person> persons;

    public RVAdapter adapter;
    public RecyclerView rv;

    public String plantName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);


        //Get Extras

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                plantName= null;
            } else {
                plantName= extras.getString("plantName");
            }
        } else {
            plantName= (String) savedInstanceState.getSerializable("plantName");
        }

        initializeData(plantName);
        Log.e("Pd","Data initialized");

        rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        Log.e("Pd","rv init");

        Context mContext = this;
        adapter = new RVAdapter(persons,mContext);
        if (adapter.getItemCount()!=0) {
            Log.e("Pd", "adapter initialized" + persons.get(0).name);
            rv.setAdapter(adapter);
            Log.e("Pd", "adapter set" + persons.get(0).name);
        }
        else {
            Log.e("Pd", "adapter empty");
        }




    }



    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
//    private void initializeData(){
//        persons = new ArrayList<>();
//        persons.add(new Person("Emma Wilson", "23 years old", R.drawable.apple, "as"));
//        persons.add(new Person("Lavery Maiss", "25 years old", R.drawable.apple,"asd"));
//        persons.add(new Person("Lillie Watts", "35 years old", R.drawable.apple, "asd"));
//    }

    private void initializeData(String plantName){
        persons = new ArrayList<>();

        DatabaseReference plant;
        DatabaseReference diseases;


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        plant = database.getReference("AndroidApp");

        String make_path = "AndroidApp/" + plantName + "/Diseases";
        diseases = database.getReference(make_path);

        diseases.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                ArrayList<String> disease_list = new ArrayList<String>();

                for(DataSnapshot dsp: dataSnapshot.getChildren()){
                    Log.e("Resultactivity","dsp="+dsp.getValue());
                    String image_url = (String) dsp.child("image_url").getValue();
                    String disese_name = (String) dsp.child("Name").getValue();
                    String remedy_url = (String) dsp.child("Remedy").getValue();
                    String remedy_short = (String) dsp.child("RemedyWord").getValue();

                    persons.add(new Person(disese_name, remedy_short, remedy_url, image_url));
                    Log.e("Resultactivity","n="+disese_name+image_url);


                }

                rv.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }



}
