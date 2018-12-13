package com.example.samuelsoto.lvlup;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.samuelsoto.lvlup.Classes.Game;



import java.util.ArrayList;

public class UserGameListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Context context;
    private int count = 0;
    private ArrayList<Game> userGames = new ArrayList<>();
    private ArrayAdapter<Game> adapter;
    private ListView list;
    private SQLiteDatabase gamesDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_game_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        gamesDB = openOrCreateDatabase("games.db", MODE_PRIVATE, null);
        getGames();

        list.setClickable(true);
        list.setOnItemClickListener(    new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {

                Intent intent = new Intent(view.getContext(),UserGameDetail.class);
                intent.putExtra("ID", userGames.get(position).getId());
                startActivity(intent);

            }
        });

        Button buscar = (Button) findViewById(R.id.searchButtonUserGames);

        buscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TextView busquedaView = (TextView) findViewById(R.id.searchUserGame);
                final String busqueda = busquedaView.getText().toString();

                buscar(busqueda);
            }
        });
    }

    public void buscar(String busqueda){
        userGames.clear();
        count=0;


        Cursor cursorGames =
                gamesDB.rawQuery("SELECT id, name FROM user_games WHERE name LIKE ? ORDER BY name", new String[] {'%'+busqueda+'%'});


        while(cursorGames.moveToNext()) {
            String id = cursorGames.getString(0);
            String name = cursorGames.getString(1);

            Game p = new Game(id, name);
            userGames.add(p);
            count++;

        }

        adapter = new UserGameArrayAdapter(getApplicationContext(),0,userGames);
        list = (ListView) findViewById(R.id.userGameList);
        list.setAdapter(adapter);

        cursorGames.close();

    }

    public void getGames() {
        userGames.clear();

        Cursor cursorGames =
                gamesDB.rawQuery("select id, name from user_games ORDER BY name", null);

        while(cursorGames.moveToNext()) {
            String id = cursorGames.getString(0);
            String name = cursorGames.getString(1);

            Game p = new Game(id, name);
            userGames.add(p);
            count++;
        }

        adapter = new UserGameArrayAdapter(getApplicationContext(),0,userGames);
        list = (ListView) findViewById(R.id.userGameList);
        list.setAdapter(adapter);

        cursorGames.close();
    }

    private void changeTheme(){
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darktheme);
        }else{
            setTheme(R.style.AppTheme);
        }
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        getGames();
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
        // Handle navigation view item clicks
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            this.startActivity(intent);
        } else if (id == R.id.nav_games) {
            Intent intent = new Intent(this, GameListActivity.class);
            this.startActivity(intent);
        } else if (id == R.id.nav_user_games) {
            Intent intent = new Intent(this, UserGameListActivity.class);
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
