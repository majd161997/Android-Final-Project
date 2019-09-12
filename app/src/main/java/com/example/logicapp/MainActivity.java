package com.example.logicapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private ArrayList<String> varsOptions;
    private ArrayList<String> prevExpressions;
    private ArrayList<String> prevExpressionsSimplified;
    private ArrayList<String> prevExpressionsvals;
    private ListView varsLV;
    private ListView prevExprLV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        varsOptions = new ArrayList<>();
        prevExpressions = new ArrayList<>();
        prevExpressionsSimplified = new ArrayList<>();
        prevExpressionsvals = new ArrayList<>();
        varsLV = findViewById(R.id.varsOptLV);
        prevExprLV = findViewById(R.id.prevExprLV);
        fillOptions();
        fillPrevExpressions();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.vars_options_style, varsOptions);
        varsLV.setAdapter(arrayAdapter);
        varsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), TruthTableActivity.class);
                intent.putExtra("varsNumber", position+2);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void fillPrevExpressions()
    {
        prevExpressions.clear();
        prevExpressionsSimplified.clear();
        prevExpressionsvals.clear();
        mPreferences = getSharedPreferences("LocalDB", Context.MODE_PRIVATE);
        int key = mPreferences.getInt("key", -1);
        if(key == -1)
            return;
        for(int k=0; k<=key; k++)
        {
            prevExpressions.add(mPreferences.getString("unsimplified"+k, ""));
            prevExpressionsSimplified.add(mPreferences.getString("simplified"+k, ""));
            prevExpressionsvals.add(mPreferences.getString("values"+k, ""));
        }
        PrevExprListAdapter prevExprAdapter = new PrevExprListAdapter(this, R.layout.prev_adapter_view_layout, prevExpressions, prevExpressionsSimplified);
        prevExprLV.setAdapter(prevExprAdapter);
        prevExprLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), K_MapActivity.class);
                ArrayList<Integer> vals = reCalcValues(position);
                intent.putExtra("rec_values", vals);
                intent.putExtra("unsimplified", prevExpressions.get(position));
                startActivity(intent);
            }
        });
    }

    private ArrayList<Integer> reCalcValues(int pos)
    {
        ArrayList<Integer> vals = new ArrayList<>();
        String value = prevExpressionsvals.get(pos);
        for(int c=0; c<value.length(); c++)
            vals.add(Integer.valueOf(value.charAt(c)+""));
        return  vals;
    }

    private void fillOptions()
    {
        varsOptions.add("2 Variables");
        varsOptions.add("3 Variables");
        varsOptions.add("4 Variables");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        fillPrevExpressions();
    }
}
