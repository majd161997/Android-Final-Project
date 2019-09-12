package com.example.logicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.InputFilter;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class TruthTableActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private int key = 0;
    private int varsNum;
    private TableLayout truthTableFLTL;
    private TableLayout truthTableTL;
    private ArrayList<Integer> values;
    private String unsimplified = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_truth_table);
        truthTableFLTL = findViewById(R.id.truthTableFLTL);
        truthTableTL = findViewById(R.id.truthTableTL);
        values = new ArrayList<>();
        varsNum = getIntent().getIntExtra("varsNumber", 2);
        createFirstLine();
        fillTruthTable();
    }

    private void fillTruthTable()
    {
        int rows = (int) Math.pow(2, varsNum);
        for(int r=0; r<rows; r++)
        {
            TableRow new_row = new TableRow(this);
            String boolNum = Integer.toBinaryString(r);
            while(boolNum.length() < varsNum)
                boolNum = "0" + boolNum;
            for(int c=0; c<boolNum.length(); c++)
            {
                TextView text = new TextView(this);
                text.setText(boolNum.charAt(c)+"");
                text.setTextColor(this.getResources().getColor(R.color.white));
                text.setGravity(Gravity.CENTER);
                new_row.addView(text);
            }
            EditText valueET = new EditText(this);
            valueET.setSelectAllOnFocus(true);
            valueET.setFilters(new InputFilter[] { new InputFilter.LengthFilter(1) });
            valueET.setInputType(InputType.TYPE_CLASS_NUMBER);
            valueET.setKeyListener(DigitsKeyListener.getInstance("01"));
            valueET.setGravity(Gravity.CENTER);
            valueET.setTextColor(this.getResources().getColor(R.color.colorAccent));
            DrawableCompat.setTint(valueET.getBackground(), ContextCompat.getColor(this, R.color.white));
            new_row.addView(valueET);
            truthTableTL.addView(new_row);
        }
    }

    private void createFirstLine()
    {
        TableRow new_row = new TableRow(this);
        for(int i=0; i<varsNum; i++)
        {
            TextView text = new TextView(this);
            char c = 'A';
            c += i;
            text.setText(c+"");
            text.setTypeface(Typeface.DEFAULT_BOLD);
            text.setTextColor(this.getResources().getColor(R.color.white));
            text.setGravity(Gravity.CENTER);
            new_row.addView(text);
        }
        TextView text = new TextView(this);
        text.setText("Value");
        text.setTypeface(Typeface.DEFAULT_BOLD);
        text.setTextColor(this.getResources().getColor(R.color.white));
        text.setGravity(Gravity.CENTER);
        new_row.addView(text);
        truthTableFLTL.addView(new_row);
    }

    public void simplifyBtn_handle(View view)
    {
        mPreferences = getSharedPreferences("LocalDB", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        //this.getSharedPreferences("LocalDB", 0).edit().clear().commit();
        values.clear();
        if(checkValues())
        {
            updateValues();
            generate_unsimplified();
            String valuesString = converteValuesToString();
            key = mPreferences.getInt("key", -1);
            key++;
            mEditor.putInt("key", key);
            mEditor.putString("unsimplified"+key, unsimplified);
            mEditor.commit();
            mEditor.putString("values"+key, valuesString);
            mEditor.commit();
            Intent intent = new Intent(this, K_MapActivity.class);
            intent.putExtra("rec_values", values);
            intent.putExtra("unsimplified", unsimplified);
            intent.putExtra("key", key);
            startActivity(intent);
        }
    }

    private String converteValuesToString()
    {
        String res = "";
        for(int i=0; i< values.size(); i++)
            res += values.get(i)+"";
        return res;
    }

    private void generate_unsimplified()
    {
        String res = "";
        for(int i=0; i<values.size(); i++)
        {
            if(values.get(i) != 0)
            {
                String boolNum = Integer.toBinaryString(i);
                while(boolNum.length() < varsNum)
                    boolNum = "0" + boolNum;
                for(int c=0; c<boolNum.length(); c++)
                {
                    char ch = (char) ('A'+c);
                    if(boolNum.charAt(c) == '0')
                        res += ch+"'";
                    else
                        res += ch+"";
                }
                res += " + ";
            }
        }
        unsimplified = (res != "" ? res.substring(0, res.length() - 3) : "0");
    }

    private void updateValues()
    {
        for(int i=0; i<truthTableTL.getChildCount(); i++)
        {
            TableRow row = (TableRow) truthTableTL.getChildAt(i);
            EditText value = (EditText) row.getChildAt(varsNum);
            values.add(Integer.valueOf(value.getText().toString()));
        }
    }

    private boolean checkValues()
    {
        boolean result = true;
        for(int i=0; i<truthTableTL.getChildCount(); i++)
        {
            TableRow row = (TableRow) truthTableTL.getChildAt(i);
            EditText value = (EditText) row.getChildAt(varsNum);
            DrawableCompat.setTint(value.getBackground(), ContextCompat.getColor(this, R.color.white));
        }
        for(int i=0; i<truthTableTL.getChildCount(); i++)
        {
            TableRow row = (TableRow) truthTableTL.getChildAt(i);
            EditText value = (EditText) row.getChildAt(varsNum);
            if(value.getText().toString().isEmpty())
            {
                DrawableCompat.setTint(value.getBackground(), ContextCompat.getColor(this, R.color.red));
                result = false;
            }
        }
        return result;
    }
}
