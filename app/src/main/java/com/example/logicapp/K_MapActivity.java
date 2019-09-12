package com.example.logicapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.ColorUtils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Pair;
import android.view.Gravity;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

public class K_MapActivity extends AppCompatActivity {

    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private ArrayList<Integer> values;
    private TableLayout k_mapTL;
    private int[][] map2dArray;
    private int varsNum;
    private int rows;
    private int cols;
    private ArrayList<ArrayList<Pair<Integer, Integer>>> groups;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_k_map);
        mPreferences = getSharedPreferences("LocalDB", Context.MODE_PRIVATE);
        mEditor = mPreferences.edit();
        groups = new ArrayList<>();
        k_mapTL = findViewById(R.id.k_mapTL);
        values = (ArrayList<Integer>) getIntent().getSerializableExtra("rec_values");
        String unsimplified = getIntent().getStringExtra("unsimplified");
        int key = getIntent().getIntExtra("key", -1);
        varsNum = (int) Math.floor(Math.log(values.size())/Math.log(2));
        create2dArray();
        createFirstLine();
        fillKMapTable();
        findGroups();
        set_groups_colors();
        String simplified_res;
        if(check_if_alwaysTrue())
            simplified_res = "1";
        else
            simplified_res = generate_simplified_string();
        TextView text = findViewById(R.id.simplified_TV);
        text.setText(simplified_res);
        if(key != -1)
        {
            mEditor.putString("simplified" + key, simplified_res);
            mEditor.commit();
        }
        TextView unsimplified_text = findViewById(R.id.unSimplified_TV);
        unsimplified_text.setText(unsimplified);

    }

    private boolean check_if_alwaysTrue()
    {
        if(groups.size() == 1 && groups.get(0).size() == values.size())
            return true;
        return false;
    }

    private void set_groups_colors()
    {
        int random_color, curr_color, final_color;
        for(ArrayList<Pair<Integer, Integer>> group : groups)
        {
            random_color = randomColor(0.5f);
            for(Pair<Integer, Integer> pair : group)
            {
                TableRow row = (TableRow) k_mapTL.getChildAt(pair.first+1);
                Drawable background = row.getChildAt(pair.second+1).getBackground();
                if (background instanceof ColorDrawable)
                {
                    curr_color = ((ColorDrawable) background).getColor();
                    final_color = ColorUtils.blendARGB(curr_color, random_color, 0.5f);
                }
                else
                    final_color = random_color;
                row.getChildAt(pair.second+1).setBackgroundColor(final_color);
            }
        }
    }

    @SuppressLint("NewApi")
    public int randomColor(float alpha) {

        int r = (int) (0xff * Math.random());
        int g = (int) (0xff * Math.random());
        int b = (int) (0xff * Math.random());

        return Color.argb(alpha, r, g, b);
    }

    class SortBySize implements Comparator<ArrayList>
    {
        @Override
        public int compare(ArrayList arrayList, ArrayList t1) {
            return arrayList.size() - t1.size();
        }
    }

    private void sort_groups()
    {
        Collections.sort(groups, new SortBySize());
    }

    private void findGroups()
    {
        for(int r=0; r<rows; r++)
        {
            for(int c=0; c<cols; c++)
            {
                ArrayList<Pair<Integer, Integer>> group_1 = search_groups(r, c,1,1);
                groups.add(group_1);
                ArrayList<Pair<Integer, Integer>> group_2_v = search_groups(r, c,2,1);
                groups.add(group_2_v);
                ArrayList<Pair<Integer, Integer>> group_2_h = search_groups(r, c,1,2);
                groups.add(group_2_h);
                ArrayList<Pair<Integer, Integer>> group_4 = search_groups(r, c,2,2);
                groups.add(group_4);
                if(varsNum >= 3)
                {
                    ArrayList<Pair<Integer, Integer>> group_4_h = search_groups(r, c, 1, 4);
                    groups.add(group_4_h);
                    ArrayList<Pair<Integer, Integer>> group_8_h = search_groups(r, c, 2, 4);
                    groups.add(group_8_h);
                }
                if(varsNum >= 4)
                {
                    ArrayList<Pair<Integer, Integer>> group_4_v = search_groups(r, c, 4, 1);
                    groups.add(group_4_v);
                    ArrayList<Pair<Integer, Integer>> group_8_v = search_groups(r, c, 4, 2);
                    groups.add(group_8_v);
                    ArrayList<Pair<Integer, Integer>> group_16 = search_groups(r, c, 4, 4);
                    groups.add(group_16);
                }
            }
        }
        sort_groups();
        groups_in_new_look();
    }

    private String generate_simplified_string()
    {
        String res = "";
        String temp_res;
        for(ArrayList<Pair<Integer, Integer>> group: groups)
        {
            temp_res = "";
            HashSet<String> set = new HashSet<>();
            for(Pair<Integer, Integer> pair: group) {
                TableRow row = (TableRow) k_mapTL.getChildAt(pair.first + 1);
                TableRow first_line = (TableRow) k_mapTL.getChildAt(0);
                String left_text = ((TextView) row.getChildAt(0)).getText().toString();
                String top_text = ((TextView) first_line.getChildAt(pair.second + 1)).getText().toString();
                if (group.size() == 1) {
                    temp_res = left_text + top_text;
                    continue;
                }
                for (int i = 0; i < left_text.length(); i++) {
                    String ch = left_text.charAt(i) + "";
                    if (i + 1 < left_text.length() && left_text.charAt(i + 1) == '\'') {
                        ch += "'";
                        i++;
                    }
                    if(set.add(ch))
                        temp_res += ch;
                }
                for (int i = 0; i < top_text.length(); i++) {
                    String ch = top_text.charAt(i) + "";
                    if (i + 1 < top_text.length() && top_text.charAt(i + 1) == '\'') {
                        ch += "'";
                        i++;
                    }
                    if(set.add(ch))
                        temp_res += ch;
                }
            }
            if(set.isEmpty())
            {
                res += temp_res + " + ";
                continue;
            }
            int count;
            String final_res = "";
            List<String> array_of_set = new ArrayList<String>(set);
            Collections.sort(array_of_set);
            for(String s : array_of_set)
            {
                count = temp_res.length() - temp_res.replace(s.charAt(0)+"", "").length();
                if(count == 1)
                    final_res += s;
            }
            res += final_res + " + ";
        }
        return res != "" ? res.substring(0, res.length() - 3) : "0";
    }

    private void groups_in_new_look()
    {
        int i, counter;
        for(i=0; i<groups.size(); i++)
        {
            ArrayList<Pair<Integer, Integer>> child = groups.get(i);
            counter = 0;
            for(Pair<Integer, Integer> pair : child)
            {
                for(ArrayList<Pair<Integer, Integer>> g_child : groups)
                {
                    if(child != g_child)
                        if(g_child.contains(pair))
                        {
                            counter++;
                            break;
                        }
                }
            }
            if(counter == child.size())
            {
                i--;
                groups.remove(child);
            }
        }
    }

    private ArrayList<Pair<Integer, Integer>> search_groups(int r, int c, int v, int h)
    {
        HashSet<Pair<Integer, Integer>> group = new HashSet<>();
        for(int i=0; i<v; i++)
        {
            int row = r;
            if (row + i >= rows)
                row = 0-i;
            row += i;
            for(int j=0; j<h; j++)
            {
                int col = c;
                if (col + j >= cols)
                    col = 0-j;
                col += j;
                if (map2dArray[row][col] != 0)
                    group.add(new Pair<Integer, Integer>(row, col));
            }
        }
        if(group.size() < v*h)
            group.clear();
        ArrayList<Pair<Integer, Integer>> result = new ArrayList<>(group);
        return result;
    }

    private void fillKMapTable()
    {
        for(int r=0; r<rows; r++)
        {
            TableRow new_row = new TableRow(this);
            TextView text = new TextView(this);
            text.setTextColor(this.getResources().getColor(R.color.white));
            text.setGravity(Gravity.CENTER);
            text.setTypeface(Typeface.DEFAULT_BOLD);
            if(varsNum == 2 || varsNum == 3)
            {
                if(r==0)
                    text.setText("A'");
                else
                    text.setText("A");
            }
            else
            {
                if(r==0)
                    text.setText("A'B'");
                else if(r==1)
                    text.setText("A'B");
                else if(r==2)
                    text.setText("AB");
                else
                    text.setText("AB'");
            }
            new_row.addView(text);
            for(int c=0; c<cols; c++)
            {
                TextView new_text = new TextView(this);
                new_text.setTextColor(this.getResources().getColor(R.color.white));
                new_text.setGravity(Gravity.CENTER);
                new_text.setText(map2dArray[r][c]+"");
                new_row.addView(new_text);
            }
            k_mapTL.addView(new_row);
        }
    }

    private void createFirstLine()
    {
        TableRow new_row = new TableRow(this);
        TextView text = new TextView(this);
        new_row.addView(text);
        for(int c=0; c<cols; c++)
        {
            TextView new_text = new TextView(this);
            new_text.setTextColor(this.getResources().getColor(R.color.white));
            new_text.setGravity(Gravity.CENTER);
            new_text.setTypeface(Typeface.DEFAULT_BOLD);
            switch (varsNum)
            {
                case 2:
                    if(c == 0)
                        new_text.setText("B'");
                    else
                        new_text.setText("B");
                    break;
                case 3:
                    if(c == 0)
                        new_text.setText("B'C'");
                    else if(c == 1)
                        new_text.setText("B'C");
                    else if(c == 2)
                        new_text.setText("BC");
                    else
                        new_text.setText("BC'");
                    break;
                case 4:
                    if(c == 0)
                        new_text.setText("C'D'");
                    else if(c == 1)
                        new_text.setText("C'D");
                    else if(c == 2)
                        new_text.setText("CD");
                    else
                        new_text.setText("CD'");
                    break;
            }
            new_row.addView(new_text);
        }
        k_mapTL.addView(new_row);
    }

    private void create2dArray()
    {
        switch (varsNum)
        {
            case 2:
                map2dArray = new int[2][2];
                rows = 2;
                cols = 2;
                break;
            case 3:
                map2dArray = new int[2][4];
                rows = 2;
                cols = 4;
                break;
            case 4:
                map2dArray = new int[4][4];
                rows = 4;
                cols = 4;
                break;
        }
        fill2dArray();
    }

    private void fill2dArray()
    {
        for(int r=0; r< rows; r++)
        {
            for(int c=0; c<cols; c++)
            {
                int row = r;
                int col = c;
                if(col == 2)
                    col=3;
                else if(col == 3)
                    col = 2;
                if(row == 2)
                    row=3;
                else if(row == 3)
                    row = 2;
                map2dArray[r][c] = values.get(row*cols+col);
            }
        }
    }
}
