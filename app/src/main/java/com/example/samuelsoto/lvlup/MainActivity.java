package com.example.samuelsoto.lvlup;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.ListView;

import com.android.volley.VolleyError;
import com.example.samuelsoto.lvlup.Classes.Game;
import com.igdb.api_android_java.callback.OnSuccessCallback;
import com.igdb.api_android_java.wrapper.IGDBWrapper;
import com.igdb.api_android_java.wrapper.Parameters;
import com.igdb.api_android_java.wrapper.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SQLiteDatabase gamesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gamesDB  = openOrCreateDatabase("games.db", MODE_PRIVATE, null);
        gamesDB.execSQL("CREATE TABLE IF NOT EXISTS games (id VARCHAR(50), name VARCHAR(50))");




        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton btnUsr = (ImageButton) findViewById(R.id.buttonUser);
        btnUsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getGames();

            }
        });

        ImageButton btnPlat = (ImageButton) findViewById(R.id.buttonPlatform);
        btnPlat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), PlatformListActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        ImageButton btnGame = (ImageButton) findViewById(R.id.buttonGame);
        btnGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), GameListActivity.class);
                startActivityForResult(intent, 0);
            }
        });
    }

    public void getGames(){


        IGDBWrapper wrapper = new IGDBWrapper(this, "64092fec918a9c7ba3ef3482988430d8", Version.STANDARD, false);

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

    Log.d("Log","Ha llegado al wrapper");
            wrapper.games(params, new OnSuccessCallback() {
                @Override
                public void onSuccess(JSONArray result) {
                    Log.d("Log","Ha pasado del wrapper");

                    for (int i = 0; i < result.length(); i++) {
                        JSONObject json_data = null;
                        try {
                            json_data = result.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        try {
                            Log.d("Log","Ha llegado al try");
                                ContentValues row = new ContentValues();
                                row.put("id", String.valueOf(json_data.getInt("id")));
                                row.put("name", String.valueOf(json_data.getString("name")));
                                gamesDB.insert("games",null,row);



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


    @SuppressWarnings("StatementWithEmptyBody")
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        gamesDB.close();
    }

}
