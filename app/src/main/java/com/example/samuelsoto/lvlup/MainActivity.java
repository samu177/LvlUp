package com.example.samuelsoto.lvlup;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatDelegate;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
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
    private Toast toast;
    private Cursor cursorGames;
    public static final String theme = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lightordark();
        changeTheme();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Crear base de datos

        gamesDB  = openOrCreateDatabase("games.db", MODE_PRIVATE, null);
        gamesDB.execSQL("CREATE TABLE IF NOT EXISTS games (id VARCHAR(50), name VARCHAR(50))");
        gamesDB.execSQL("CREATE TABLE IF NOT EXISTS user_games (id VARCHAR(50), name VARCHAR(50), summary TEXT, platforms VARCHAR(50), cost DECIMAL(5,2), score SMALLINT, comment TEXT, PRIMARY KEY (id))");

        updateMain();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ImageButton buttonUpdate = (ImageButton) findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast(v);
                getGames();
            }
        });


        ImageButton btnUsr = (ImageButton) findViewById(R.id.buttonUsr);
        btnUsr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), UserGameListActivity.class);
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
        gamesDB.beginTransaction();
        try{
            //delete the table before insert
            gamesDB.execSQL("delete from games");
            IGDBWrapper wrapper = new IGDBWrapper(this, "11bb45eea6115987851e66f26472a6f7", Version.STANDARD, false);

            int offset=50;
            for(int i=0; i<100; i++) { //get the games from Xbox One, PS4 and Nintendo Switch
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
            gamesDB.setTransactionSuccessful();
        }finally{
            gamesDB.endTransaction();
        }
    }

    //restore the theme of the app
    private void lightordark(){
        SharedPreferences prefs = getSharedPreferences(theme, MODE_PRIVATE);
        int restoredInt = prefs.getInt("dark",0);
        if(restoredInt==1){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else{
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
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
        updateMain();
    }

    private void updateMain(){
        //collect the number of games
        cursorGames  = gamesDB.rawQuery("SELECT count(*) FROM user_games", null);

        String countgames = "0";

        while(cursorGames.moveToNext()) {
            if(cursorGames.getString(0) != null){
                countgames = cursorGames.getString(0);
            }
        }

        TextView ngames = (TextView) findViewById(R.id.txtNumGames);
        ngames.setText(countgames);
        cursorGames.close();

        //collect the money spent
        cursorGames  = gamesDB.rawQuery("SELECT SUM(cost) FROM user_games", null);

        float totalCost=0;

        while(cursorGames.moveToNext()) {
            if(cursorGames.getFloat(0) != 0.0f){
                totalCost = cursorGames.getFloat(0);
            }
        }

        TextView cost = (TextView) findViewById(R.id.txtMoneySpent);
        String display = String.valueOf(totalCost) + '€';
        cost.setText(display);
        cursorGames.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreferences.Editor editor = getSharedPreferences(theme, MODE_PRIVATE).edit();
            if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES){
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putInt("dark", 0);
                editor.apply();
            }else{
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putInt("dark", 1);
                editor.apply();
            }
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            finish();
        }

        return super.onOptionsItemSelected(item);
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
        // Handle navigation view item clicks
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //clear the rest of the activities
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

    public void showToast(View view) {
        toast = Toast.makeText(
                MainActivity.this,
                "Espere hasta que acabe de actualizar la base de datos",
                Toast.LENGTH_SHORT);
        toast.show();

        new CountDownTimer(10000, 1000)
        {
            public void onTick(long millisUntilFinished) {
                toast.show();
            }

            public void onFinish() {
                toast.cancel();

            }
        }.start();
    }


    @Override
    public void onDestroy(){
        super.onDestroy();
        gamesDB.close();
    }

}
