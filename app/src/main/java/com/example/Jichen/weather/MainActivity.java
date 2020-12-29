package com.example.Jichen.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    EditText city;
    Button button;
    TextView textView;


    class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            StringBuilder jsonString = new StringBuilder();

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in= connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();
                while(data != -1) {
                    char current = (char)data;
                    jsonString.append(current);
                    data = reader.read();
                }

                JSONObject jsonPart = new JSONObject(jsonString.toString()).getJSONArray("weather").getJSONObject(0);
                String main = jsonPart.getString("main");
                String description = jsonPart.getString("description");

                String result = main + ": " + description;

                return result;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public void getWeather(View view) {
        String typeIn = city.getText().toString();

        if(typeIn.length() == 0) {
            Toast.makeText(getApplicationContext(), "Please enter a city.", Toast.LENGTH_SHORT);
            return;
        }

        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            String encodedCityName = URLEncoder.encode(typeIn, "UTF-8");
            result = task.execute("http://api.openweathermap.org/data/2.5/weather?q=" + encodedCityName + "&appid=1c9bb18793842804ece94158cc370135").get();
            if(result == null || result.equals(""))
                Toast.makeText(getApplicationContext(), "result is empty", Toast.LENGTH_SHORT);
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Could not find weather :(", Toast.LENGTH_SHORT);
            e.printStackTrace();
        }

        textView.setText(result);

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(city.getWindowToken(), 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        city = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
    }
}