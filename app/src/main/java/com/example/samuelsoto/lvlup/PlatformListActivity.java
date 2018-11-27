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
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.samuelsoto.lvlup.Classes.Platform;

import com.android.volley.VolleyError;
import com.igdb.api_android_java.callback.OnSuccessCallback;
import com.igdb.api_android_java.wrapper.IGDBWrapper;
import com.igdb.api_android_java.wrapper.Parameters;
import com.igdb.api_android_java.wrapper.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PlatformListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private IGDBWrapper  wrapper;
    private int count = 0;
    private ArrayList<Platform> platforms = new ArrayList<>();
    private ArrayAdapter<Platform> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_platform_list);
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


    }

    public void apiSetup() {
        context = getApplicationContext();
        wrapper = new IGDBWrapper(context, "4661abeeaff372aa70b98588332b3b99", Version.STANDARD, false);
    }

    public void getGames() {
        for(int i=0; i<3; i++) {
            Parameters params = null;
            if (i == 0) {
                params = new Parameters()
                        .addFields("id,name")
                        .addLimit("50")
                        .addOrder("name");
            } else if (i == 1) {
                params = new Parameters()
                        .addFields("id,name")
                        .addLimit("50")
                        .addOffset("51")
                        .addOrder("name");
            } else if (i == 2) {
                params = new Parameters()
                        .addFields("id,name")
                        .addLimit("50")
                        .addOffset("104")
                        .addOrder("name");
            }


            wrapper.platforms(params, new OnSuccessCallback() {
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
                            Platform p = new Platform(String.valueOf(json_data.getInt("id")), String.valueOf(json_data.getString("name")));
                            platforms.add(p);
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
        adapter = new platformArrayAdapter(getApplicationContext(),0,platforms);
        ListView list = (ListView) findViewById(R.id.platformList);
        list.setAdapter(adapter);
        Log.d(PlatformListActivity.class.getSimpleName(), String.valueOf(count));
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
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(this, UserActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_update) {

        } else if (id == R.id.nav_options) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}