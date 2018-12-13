package com.example.samuelsoto.lvlup;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.CountDownTimer;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Crear base de datos

        gamesDB  = openOrCreateDatabase("games.db", MODE_PRIVATE, null);
        gamesDB.execSQL("CREATE TABLE IF NOT EXISTS games (id VARCHAR(50), name VARCHAR(50))");
        gamesDB.execSQL("CREATE TABLE IF NOT EXISTS user_games (id VARCHAR(50), name VARCHAR(50), summary TEXT, platforms VARCHAR(50), cost DECIMAL(5,2), comment TEXT, PRIMARY KEY (id))");

        updateMain();
        //deleteUserGames();

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
            gamesDB.execSQL("delete from games");
            Log.d("Log","Ha borrado la tabla");
            IGDBWrapper wrapper = new IGDBWrapper(this, "11bb45eea6115987851e66f26472a6f7", Version.STANDARD, false);

            int offset=50;
            for(int i=0; i<100; i++) {
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
            Log.d("Log","Transaccion finalizada");
            gamesDB.endTransaction();
        }
        Log.d("Log","FIN");
    }

    @Override
    public void onResume()
    {  // After a pause OR at startup
        super.onResume();
        updateMain();
    }

    private void deleteUserGames(){
        gamesDB.execSQL("delete from user_games");
    }

    private void updateMain(){
        //Recoger el número de juegos de la lista
        cursorGames  = gamesDB.rawQuery("SELECT count(*) FROM user_games", null);

        String countgames = "0";

        while(cursorGames.moveToNext()) {
            if(cursorGames.getString(0) != null){
                countgames = cursorGames.getString(0);
            }
            Log.d("number of games", countgames);
        }

        TextView ngames = (TextView) findViewById(R.id.txtNumGames);
        ngames.setText(countgames);
        cursorGames.close();

        //Recoger el dinero total gastado
        cursorGames  = gamesDB.rawQuery("SELECT cost FROM user_games", null);

        float totalCost=0;

        while(cursorGames.moveToNext()) {
            if(cursorGames.getFloat(0) != 0.0f){
                totalCost = cursorGames.getFloat(0);
            }
            Log.d("total cost", String.valueOf(totalCost));
        }

        TextView cost = (TextView) findViewById(R.id.txtMoneySpent);
        String display = String.valueOf(totalCost) + '€';
        cost.setText(display);
        cursorGames.close();
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
