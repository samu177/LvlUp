package com.example.samuelsoto.lvlup;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatDelegate;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class UserGameDetail extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SQLiteDatabase gamesDB;
    private String id;
    private String name;
    private String summary;
    private String platforms;
    private Float cost;
    private Short score;
    private String comment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        changeTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_game_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent mIntent = getIntent();
        id = mIntent.getStringExtra("ID");

        gamesDB = openOrCreateDatabase("games.db", MODE_PRIVATE, null);

        Cursor cursorGames =
                gamesDB.rawQuery("SELECT * FROM user_games WHERE id = ?", new String[] {id});

        while(cursorGames.moveToNext()) {
            name = cursorGames.getString(1);
            summary = cursorGames.getString(2);
            platforms = cursorGames.getString(3);
            cost = cursorGames.getFloat(4);
            score = cursorGames.getShort(5);
            comment = cursorGames.getString(6);
        }
        TextView id_tv = (TextView) findViewById(R.id.idGame);
        TextView name_tv = (TextView) findViewById(R.id.gameName);
        TextView summary_tv = (TextView) findViewById(R.id.gameSummary);
        TextView platforms_tv = (TextView) findViewById(R.id.gamePlatforms);
        EditText cost_et = (EditText) findViewById(R.id.editMoneySpent);
        EditText score_et = (EditText) findViewById(R.id.editScore);
        EditText comment_et = (EditText) findViewById(R.id.editComment);

        name_tv.setText(name);
        summary_tv.setText(summary);
        platforms_tv.setText(platforms);
        cost_et.setText(String.valueOf(cost));
        score_et.setText(String.valueOf(score));
        comment_et.setText(comment);


        cursorGames.close();


        Button modGame = (Button) findViewById(R.id.modGame);
        modGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //update the game from the favourites list
                gamesDB  = openOrCreateDatabase("games.db", MODE_PRIVATE, null);

                EditText cost = (EditText) findViewById(R.id.editMoneySpent);
                EditText score = (EditText) findViewById(R.id.editScore);
                EditText comment = (EditText) findViewById(R.id.editComment);


                gamesDB.execSQL("UPDATE user_games SET cost = ?, score = ?, comment = ? WHERE id = ?", new String[] {cost.getText().toString(),score.getText().toString(),comment.getText().toString(),id});
                finish();
            }
        });

        Button deleteGame = (Button) findViewById(R.id.deleteGame);
        deleteGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //delete the game from the favourites list
                gamesDB  = openOrCreateDatabase("games.db", MODE_PRIVATE, null);

                gamesDB.execSQL("DELETE FROM user_games WHERE id = ?", new String[] {id});
                finish();
            }
        });
    }

    private void changeTheme(){
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.darktheme);
        }else{
            setTheme(R.style.AppTheme);
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
        // Handle navigation view item
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
