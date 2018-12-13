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

class UserGameArrayAdapter extends ArrayAdapter<Game> {

    private Context context;
    private List<Game> userGameProperties;

    public UserGameArrayAdapter(Context context, int resource, ArrayList<Game> objects) {
        super(context, resource, objects);

        this.context = context;
        this.userGameProperties = objects;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        Game userGame = userGameProperties.get(position);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.user_game_list, null);

        TextView name = (TextView) view.findViewById(R.id.Itemname);
        TextView id = (TextView) view.findViewById(R.id.idGame);

        name.setText(String.valueOf(userGame.getName()));
        id.setText(String.valueOf(userGame.getId()));

        return view;
    }
}