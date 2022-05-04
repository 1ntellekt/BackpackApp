package com.example.backpackapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.backpackapp.R;
import com.example.backpackapp.enteties.Author;
import com.example.backpackapp.enteties.Subject;

import java.util.List;

public class AuthorsArrayAdapter extends ArrayAdapter<Author> {


    public AuthorsArrayAdapter(@NonNull Context context, @NonNull List<Author> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position, View convertView, ViewGroup parent){
        if (convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_array_adapter_layout,parent,false);
        }
        TextView tvName = convertView.findViewById(R.id.tvName);
        Author author = getItem(position);
        if (author!=null){
            tvName.setText(author.getFull_name());
        }
        return convertView;
    }


}
