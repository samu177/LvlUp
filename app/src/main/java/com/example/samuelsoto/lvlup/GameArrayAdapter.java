package com.example.samuelsoto.lvlup;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.samuelsoto.lvlup.Classes.Game;

import java.util.ArrayList;
import java.util.List;

class GameArrayAdapter extends ArrayAdapter<Game> {

    private Context context;
    private List<Game> GameProperties;

    public GameArrayAdapter(Context context, int resource, ArrayList<Game> objects) {
        super(context, resource, objects);

        this.context = context;
        this.GameProperties = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Game platform = GameProperties.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.game_list, null);

        TextView id = (TextView) view.findViewById(R.id.idGame);
        TextView name = (TextView) view.findViewById(R.id.Itemname);

        name.setText(String.valueOf(platform.getName()));
        id.setText(String.valueOf(platform.getId()));

        return view;
    }
}