package com.example.keerthana.cricscoreslive;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipeContainer;
    RecyclerView recyclerView;
    LinearLayoutManager manager;
    private Handler mHandler;
    Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = (RecyclerView) findViewById(R.id.match_list);
        manager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        //FetchScoresTask task = new FetchScoresTask();
        //task.execute();

        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipe);

        // Setup refresh listener which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                update();
            }

        });

        mHandler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    update();
                } finally {
                    mHandler.postDelayed(this, 60000);//Refreshed every minute
                }
            }
        };

        Toast.makeText(this,"Data refreshed every minute\nor\nSwipe to Refresh", Toast.LENGTH_LONG).show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        runnable.run();
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public class FetchScoresTask extends AsyncTask<Void,Void,ArrayList<String>> {

        @Override
        protected ArrayList doInBackground(Void... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            String dataJson = null;
            try{
                String link = "http://cricapi.com/api/cricket";
                Log.i("CricketURL", link);
                URL url = new URL(link);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                StringBuffer buffer = new StringBuffer();

                reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                JSONObject json = new JSONObject(buffer.toString());
                JSONArray matches = json.getJSONArray("data");
                ArrayList<String> list = new ArrayList<>(matches.length());
                for (int i=0;i<matches.length(); i++) {
                    url = new URL(link + "Score?unique_id=" + matches.getJSONObject(i).getString("unique_id"));
                    connection = (HttpURLConnection) url.openConnection();
                    connection.connect();

                    inputStream = connection.getInputStream();
                    buffer = new StringBuffer();

                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    while ((line = reader.readLine()) != null) {
                        // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                        // But it does make debugging a *lot* easier if you print out the completed
                        // buffer for debugging.
                        buffer.append(line + "\n");
                    }
                    list.add(buffer.toString());
                }
                    return list;

            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList dataJson){
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.match_list);
            assert recyclerView != null;
            recyclerView.setAdapter(new RecyclerViewAdapter(dataJson));
            swipeContainer.setRefreshing(false);
        }
    }
    @Override
    protected void onStop(){
        super.onStop();
        mHandler.removeCallbacks(runnable);
    }

    protected void update(){
        if (isNetworkAvailable()) {
            FetchScoresTask task = new FetchScoresTask();
            task.execute();
        }
        else{
            Toast.makeText(getApplicationContext(),"Network Error",Toast.LENGTH_LONG);}
    }
}
