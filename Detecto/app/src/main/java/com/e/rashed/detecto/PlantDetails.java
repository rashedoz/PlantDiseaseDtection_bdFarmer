package com.e.rashed.detecto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class PlantDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public List<Person> persons;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_details);

        initializeData();
        Log.e("Pd","Data initialized");

        RecyclerView rv = (RecyclerView)findViewById(R.id.rv);
        rv.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        rv.setLayoutManager(llm);
        Log.e("Pd","rv init");

        RVAdapter adapter = new RVAdapter(persons);
        Log.e("Pd","adapter initialized"+persons.get(0).name);
        rv.setAdapter(adapter);
        Log.e("Pd","adapter set"+persons.get(0).name);


    }



    // This method creates an ArrayList that has three Person objects
// Checkout the project associated with this tutorial on Github if
// you want to use the same images.
    private void initializeData(){
        persons = new ArrayList<>();
        persons.add(new Person("Emma Wilson", "23 years old", R.drawable.apple));
        persons.add(new Person("Lavery Maiss", "25 years old", R.drawable.apple));
        persons.add(new Person("Lillie Watts", "35 years old", R.drawable.apple));
    }

}
