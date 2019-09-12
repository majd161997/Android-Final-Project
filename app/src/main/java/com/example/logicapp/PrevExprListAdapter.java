package com.example.logicapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class PrevExprListAdapter extends ArrayAdapter
{

    private Context mContext;
    private int mResource;
    private ArrayList<String> unsimplifiedList;
    private ArrayList<String> simplifiedList;

    public PrevExprListAdapter(@NonNull Context context, int resource, ArrayList<String> unsimplified, ArrayList<String> simplified)
    {
        super(context, resource, unsimplified);
        mContext = context;
        mResource = resource;
        unsimplifiedList = unsimplified;
        simplifiedList = simplified;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        TextView keyTV = convertView.findViewById(R.id.keyIDTV);
        TextView unsimplifiedTV = convertView.findViewById(R.id.unSimplifiedTV);
        TextView simplifiedTV = convertView.findViewById(R.id.simplifiedTV);
        keyTV.setText((position+1)+"");
        unsimplifiedTV.setText(unsimplifiedList.get(position));
        simplifiedTV.setText(simplifiedList.get(position));

        return convertView;
    }
}
