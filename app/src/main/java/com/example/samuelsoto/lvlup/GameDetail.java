package com.example.samuelsoto.lvlup;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.igdb.api_android_java.callback.OnSuccessCallback;
import com.igdb.api_android_java.wrapper.IGDBWrapper;
import com.igdb.api_android_java.wrapper.Parameters;
import com.igdb.api_android_java.wrapper.Version;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class GameDetail extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SQLiteDatabase gamesDB;
    private String id;
    private TextView name;
    private TextView summary;
    private TextView platforms;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);
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

        Log.d("ID:","La id es:" + id);

        IGDBWrapper wrapper = new IGDBWrapper(this, "11bb45eea6115987851e66f26472a6f7", Version.STANDARD, false);

        Parameters params = new Parameters()
                .addIds(id)
                .addFields("name,summary,platforms");

        wrapper.games(params, new OnSuccessCallback(){
            @Override
            public void onSuccess(JSONArray result) {

                JSONObject json_data = null;
                try {
                    json_data = result.getJSONObject(0);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                name = (TextView) findViewById(R.id.gameName);
                summary = (TextView) findViewById(R.id.gameSummary);
                platforms = (TextView) findViewById(R.id.gamePlatforms);

                try {

                    name.setText(String.valueOf(json_data.getString("name")));
                    summary.setText(String.valueOf(json_data.getString("summary")));

                    JSONArray jArray = json_data.getJSONArray("platforms");

                    if(jArray.length() != 0) {
                        StringBuilder platform = new StringBuilder();
                        for (int i = 0; i < jArray.length(); i++) {
                            if (jArray.getString(i).equals("48")) {
                                platform.append("PS4 ");
                            }
                            if (jArray.getString(i).equals("49")) {
                                platform.append("Xbox One ");
                            }

                            if (jArray.getString(i).equals("130")) {
                                platform.append("Nintendo Switch ");
                            }
                            Log.i("log_tag", jArray.getString(i));
                        }
                        platforms.setText(platform.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(VolleyError error) {
                // Do something on error
            }
        });

        Button addGame = (Button) findViewById(R.id.addGame);
        addGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gamesDB  = openOrCreateDatabase("games.db", MODE_PRIVATE, null);

                EditText cost = (EditText) findViewById(R.id.editMoneySpent);
                EditText score = (EditText) findViewById(R.id.editScore);
                EditText comment = (EditText) findViewById(R.id.editComment);


                gamesDB.execSQL("INSERT OR IGNORE INTO user_games( id, name, summary, platforms, cost, score, comment) VALUES(?,?,?,?,?,?,?)", new String[] {id,String.valueOf(name.getText()),
                        String.valueOf(summary.getText()),String.valueOf(platforms.getText()),cost.getText().toString(),score.getText().toString(),comment.getText().toString()});
                Log.i("cost", cost.getText().toString());
                Log.i("log_tag", "juego añadido");
                finish();
            }
        });
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
        } else if (id == R.id.nav_user_games) {
            Intent intent = new Intent(this, UserGameListActivity.class);
            this.startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
