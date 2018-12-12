package com.example.samuelsoto.lvlup;

        import android.app.Activity;
        import android.content.Context;
        import android.graphics.Bitmap;
        import android.graphics.BitmapFactory;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.ArrayAdapter;
        import android.widget.ImageView;
        import android.widget.TextView;

        import com.example.samuelsoto.lvlup.Classes.Game;
        import com.example.samuelsoto.lvlup.R;

        import java.io.IOException;
        import java.net.MalformedURLException;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.List;

class GameArrayAdapter extends ArrayAdapter<Game> {

    private Context context;
    private List<Game> platformProperties;

    //constructor, call on creation
    public GameArrayAdapter(Context context, int resource, ArrayList<Game> objects) {
        super(context, resource, objects);

        this.context = context;
        this.platformProperties = objects;
    }

    //called when rendering the list
    public View getView(int position, View convertView, ViewGroup parent) {

        //get the property we are displaying
        Game platform = platformProperties.get(position);

        //get the inflater and inflate the XML layout for each item
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.game_list, null);

        TextView name = (TextView) view.findViewById(R.id.Itemname);
        TextView id = (TextView) view.findViewById(R.id.idGame);

        name.setText(String.valueOf(platform.getName()));
        id.setText(String.valueOf(platform.getId()));

        return view;
    }
}