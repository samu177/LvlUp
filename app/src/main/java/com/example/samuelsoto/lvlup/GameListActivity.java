package com.example.samuelsoto.lvlup;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.samuelsoto.lvlup.Classes.Game;

import com.android.volley.VolleyError;
import com.igdb.api_android_java.callback.OnSuccessCallback;
import com.igdb.api_android_java.wrapper.IGDBWrapper;
import com.igdb.api_android_java.wrapper.Parameters;
import com.igdb.api_android_java.wrapper.Version;
import com.igdb.api_android_java.wrapper.Endpoint;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class GameListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private IGDBWrapper  wrapper;
    private int count = 0;
    private ArrayList<Game> games = new ArrayList<>();
    private ArrayAdapter<Game> adapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        apiSetup();
        getGames();

        list.setClickable(true);
        list.setOnItemClickListener(    new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                Log.i("Click", "click en el elemento " + position);

                Intent intent = new Intent(view.getContext(),GameDetail.class);
                intent.putExtra("ID", games.get(position).getId());
                startActivity(intent);

            }
        });

        Button buscar = (Button) findViewById(R.id.searchButtonGame);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView busquedaView = (TextView) findViewById(R.id.searchGame);
                final String busqueda = busquedaView.getText().toString();
                Log.d("Busqueda:",busqueda);
                buscar(busqueda);
            }
        });



    }

    public void buscar(String busqueda){
        Log.d(GameListActivity.class.getSimpleName(),busqueda);
        games.clear();
        Parameters params = new Parameters()
                .addSearch(busqueda)
                .addFields("id,name")
                .addOrder("name");

        wrapper.search(Endpoint.GAMES, params, new OnSuccessCallback(){
            @Override
            public void onSuccess(JSONArray result) {
                for (int i = 0; i < result.length(); i++) {
                    JSONObject json_data = null;
                    try {
                        json_data = result.getJSONObject(i);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        Game p = new Game(String.valueOf(json_data.getInt("id")), String.valueOf(json_data.getString("name")));
                        games.add(p);
                        count++;
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                adapter = new GameArrayAdapter(getApplicationContext(),0,games);
                list = (ListView) findViewById(R.id.gameList);
                list.setAdapter(adapter);
                Log.d(GameListActivity.class.getSimpleName(), String.valueOf(count));

            }

            @Override
            public void onError(VolleyError error) {
                Log.e("Volly Error", error.toString());
            }
        });
    }

    public void apiSetup() {
        context = getApplicationContext();
        wrapper = new IGDBWrapper(context, "4661abeeaff372aa70b98588332b3b99", Version.STANDARD, false);
    }

    public void getGames() {
        int offset=50;
        for(int i=0; i<90; i++) {
            Parameters params = null;
            if (i == 0) {
                params = new Parameters()
                        .addFilter("[release_dates.platform][any]=49,48,130")
                        .addFields("id,name")
                        .addLimit("50")
                        .addOrder("name");
            }else{
                params = new Parameters()
                        .addFilter("[release_dates.platform][any]=49,48,130")
                        .addFields("id,name")
                        .addLimit("50")
                        .addOffset(String.valueOf(offset))
                        .addOrder("name");
            }
            offset=offset+50;


            wrapper.games(params, new OnSuccessCallback() {
                @Override
                public void onSuccess(JSONArray result) {

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject json_data = null;
                        try {
                            json_data = result.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            Game p = new Game(String.valueOf(json_data.getInt("id")), String.valueOf(json_data.getString("name")));
                            games.add(p);
                            count++;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onError(VolleyError error) {
                    Log.e("Volly Error", error.toString());
                }
            });
        }
        adapter = new GameArrayAdapter(getApplicationContext(),0,games);
        list = (ListView) findViewById(R.id.gameList);
        list.setAdapter(adapter);
        Log.d(GameListActivity.class.getSimpleName(), String.valueOf(count));
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        } else if (id == R.id.nav_games) {
            Intent intent = new Intent(this, GameListActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_platforms) {
            Intent intent = new Intent(this, PlatformListActivity.class);
            this.startActivity(intent);
        }  else if (id == R.id.nav_update) {

        } else if (id == R.id.nav_options) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
