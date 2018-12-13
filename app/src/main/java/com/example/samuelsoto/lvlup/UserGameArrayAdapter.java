package com.example.samuelsoto.lvlup;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.samuelsoto.lvlup.Classes.UserGame;

import java.util.ArrayList;
import java.util.List;

class UserGameArrayAdapter extends ArrayAdapter<UserGame> {

    private Context context;
    private List<UserGame> userGameProperties;

    //constructor, call on creation
    public UserGameArrayAdapter(Context context, int resource, ArrayList<UserGame> objects) {
        super(context, resource, objects);

        this.context = context;
        this.userGameProperties = objects;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        UserGame userGame = userGameProperties.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.user_game_list, null);

        TextView name = (TextView) view.findViewById(R.id.Itemname);
        TextView id = (TextView) view.findViewById(R.id.idGame);

        name.setText(String.valueOf(userGame.getName()));
        id.setText(String.valueOf(userGame.getId()));


        return view;
    }
}