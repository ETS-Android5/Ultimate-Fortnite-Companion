package com.elitelabs.fortnitetracker;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;

import static java.util.Date.parse;


/**
 * A simple {@link Fragment} subclass.
 */
public class ItemShop extends Fragment implements View.OnClickListener {

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private String access_token, expire, OAUTH_TOKEN = "https://account-public-service-prod03.ol.epicgames.com/account/api/oauth/token";
    private ArrayList<item> DailyItems = new ArrayList<item>();
    private ArrayList<item> WeeklyItems = new ArrayList<item>();
    private ArrayList<TextView> nameBoxDay = new ArrayList<TextView>();
    private ArrayList<TextView> nameBoxWeek = new ArrayList<TextView>();
    private ArrayList<TextView> priceBoxDay = new ArrayList<TextView>();
    private ArrayList<TextView> priceBoxWeek = new ArrayList<TextView>();
    private ArrayList<ImageView> imageBoxDay = new ArrayList<ImageView>();
    private ArrayList<ImageView> imageBoxWeek = new ArrayList<ImageView>();
    private ArrayList<RelativeLayout> boxWeek = new ArrayList<RelativeLayout>();
    private ArrayList<RelativeLayout> boxDay = new ArrayList<RelativeLayout>();
    private AlertDialog.Builder Dialog;
    private TextView text;

    private boolean feat1 = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;

        if(getActivity().getSharedPreferences("label", 0).getBoolean("NewLayout", false))
            view  = inflater.inflate(R.layout.item_shop_new, container, false);
        else
            view = inflater.inflate(R.layout.item_shop, container, false);

        mPrefs = this.getActivity().getSharedPreferences("label", 0); //Restored Values (act name)
        mEditor = mPrefs.edit();

        text = view.findViewById(R.id.time);

        Dialog = new AlertDialog.Builder(this.getContext())
                .setTitle("How-To")
                .setMessage("To cycle through all items in the item shop, tap on the \"tap me\" text on right ")
                .setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        mEditor.putBoolean("shopNotification", true).commit();
                    }
                });

        if(!mPrefs.getBoolean("shopNotification", false))
            Dialog.show();


        nameBoxWeek.add((TextView) view.findViewById(R.id.feat1name));
        nameBoxWeek.add((TextView) view.findViewById(R.id.feat2name));
        nameBoxDay.add((TextView) view.findViewById(R.id.day1name));
        nameBoxDay.add((TextView) view.findViewById(R.id.day2name));
        nameBoxDay.add((TextView) view.findViewById(R.id.day3name));
        nameBoxDay.add((TextView) view.findViewById(R.id.day4name));

        priceBoxWeek.add((TextView) view.findViewById(R.id.feat1price));
        priceBoxWeek.add((TextView) view.findViewById(R.id.feat2price));
        priceBoxDay.add((TextView) view.findViewById(R.id.day1price));
        priceBoxDay.add((TextView) view.findViewById(R.id.day2price));
        priceBoxDay.add((TextView) view.findViewById(R.id.day3price));
        priceBoxDay.add((TextView) view.findViewById(R.id.day4price));

        imageBoxWeek.add((ImageView) view.findViewById(R.id.feat1image));
        imageBoxWeek.add((ImageView) view.findViewById(R.id.feat2image));
        imageBoxDay.add((ImageView) view.findViewById(R.id.day1image));
        imageBoxDay.add((ImageView) view.findViewById(R.id.day2image));
        imageBoxDay.add((ImageView) view.findViewById(R.id.day3image));
        imageBoxDay.add((ImageView) view.findViewById(R.id.day4image));

        boxDay.add((RelativeLayout) view.findViewById(R.id.day1));
        boxDay.add((RelativeLayout) view.findViewById(R.id.day2));
        boxDay.add((RelativeLayout) view.findViewById(R.id.day3));
        boxDay.add((RelativeLayout) view.findViewById(R.id.day4));
        boxWeek.add((RelativeLayout) view.findViewById(R.id.feat1));
        boxWeek.add((RelativeLayout) view.findViewById(R.id.feat2));

        //Get saved access token and expire time
        access_token = mPrefs.getString("accessToken", "");

        try {
            getItemShop();
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("ERROR ITEMSOP " + e.getMessage());
        }

        //Timer countdown
        SimpleDateFormat date = new SimpleDateFormat("HH:mm:ss");
        date.setTimeZone(TimeZone.getTimeZone("UTC"));
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        String currentTime = date.format(cal.getTime());
        long Time = 0, Refresh, TimeLeft = 0;
        try { Time = date.parse(currentTime).getTime();
            Refresh = date.parse("23:59:59").getTime();
            TimeLeft = Refresh - Time; } catch (ParseException e) { }

            new CountDownTimer(TimeLeft, 1000){
                @Override
                public void onTick(long l) {
                    long millis = l;
                    String hms = (TimeUnit.MILLISECONDS.toHours(millis) - TimeUnit.DAYS.toHours(TimeUnit.MILLISECONDS.toDays(millis)) + ":")
                            + (TimeUnit.MILLISECONDS.toMinutes(millis) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)) + ":"
                            + (TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis))));
                    text.setText(/*context.getString(R.string.ends_in) + " " +*/ hms);
                }
                @Override public void onFinish() { }
            }.start();


        //Switch daily items on tap
        view.findViewById(R.id.changeDay).setOnClickListener(this);
        view.findViewById(R.id.changeFeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(WeeklyItems.size() > 2){
                WeeklyItems.add(0, WeeklyItems.get(WeeklyItems.size() - 1));
                WeeklyItems.remove(WeeklyItems.size() - 1);
                try {
                    updateView();
                } catch (Exception e) {
                    e.printStackTrace();
                }}
            }
        });

        //If there are alternates, tap to swap
        if(WeeklyItems.size() > 2){
            view.findViewById(R.id.changeFeat).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    WeeklyItems.add(WeeklyItems.get(0));
                    WeeklyItems.remove(0);
                    imageBoxWeek.add(imageBoxWeek.get(0));
                    nameBoxWeek.add(nameBoxWeek.get(0));
                    priceBoxWeek.add(priceBoxWeek.get(0));
                    priceBoxWeek.remove(0);
                    nameBoxWeek.remove(0);
                    imageBoxWeek.remove(0);

                    try {
                        updateView();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }


        return view;
    }


    public void getItemShop() throws JSONException {

        String url = "https://fnbr.co/api/shop";
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                @Override
                public void onResponse(JSONObject response) {

                    try{
                        JSONArray DailyResp, WeeklyResp;

                        DailyResp = response.getJSONObject("data").getJSONArray("daily");
                        WeeklyResp = response.getJSONObject("data").getJSONArray("featured");

                        //Daily Items
                        for(int x = 0; x < DailyResp.length(); x++){
                            DailyItems.add(x, new item());
                            DailyItems.get(x).setPrice(DailyResp.getJSONObject(x).getString("price"));
                            DailyItems.get(x).setId(DailyResp.getJSONObject(x).getString("id"));
                            DailyItems.get(x).setName(DailyResp.getJSONObject(x).getString("name"));
                            DailyItems.get(x).setRarity(DailyResp.getJSONObject(x).getString("rarity"));
                            DailyItems.get(x).setType(DailyResp.getJSONObject(x).getString("type"));
                            DailyItems.get(x).setImageLink(DailyResp.getJSONObject(x).getJSONObject("images").getString("icon"));
                        }

                        //Weekly Items
                        for(int x = 0; x < WeeklyResp.length(); x++){
                            WeeklyItems.add(x, new item());
                            WeeklyItems.get(x).setPrice(WeeklyResp.getJSONObject(x).getString("price"));
                            WeeklyItems.get(x).setId(WeeklyResp.getJSONObject(x).getString("id"));
                            WeeklyItems.get(x).setName(WeeklyResp.getJSONObject(x).getString("name"));
                            WeeklyItems.get(x).setRarity(WeeklyResp.getJSONObject(x).getString("rarity"));
                            WeeklyItems.get(x).setType(WeeklyResp.getJSONObject(x).getString("type"));
                            WeeklyItems.get(x).setImageLink(WeeklyResp.getJSONObject(x).getJSONObject("images").getString("featured"));
                            if(WeeklyItems.get(x).getImageLink().equals("false") || !WeeklyItems.get(x).getType().equals("outfit"))
                                WeeklyItems.get(x).setImageLink(WeeklyResp.getJSONObject(x).getJSONObject("images").getString("icon"));
                            else
                                WeeklyItems.get(x).setImageLink2(WeeklyResp.getJSONObject(x).getJSONObject("images").getString("icon"));
                        }

                        updateView();

                    } catch (Exception e) {
                        System.out.println("FAIL: " + e.getMessage() );
                    }

                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    // TODO: Handle error
                    System.out.println("VOLLEY FAIL: " + error.getMessage());
                }
            }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<String, String>();
                    //headers.put("Content-Type", "application/json");
                    headers.put("x-api-key", "3200738b-a856-4754-a088-0a392e813ba4");
                    return headers;
                }
        };

        RequestQueue q = Volley.newRequestQueue(getActivity().getApplicationContext());
        q.add(jsonObjectRequest);

    }

    public void updateView() throws Exception {

        for(int x = 0; x < 4; x++){
            nameBoxDay.get(x).setText(DailyItems.get(x).getName());
            priceBoxDay.get(x).setText(DailyItems.get(x).getPrice());

            Picasso.with(this.getContext()).load(DailyItems.get(x).getImageLink()).into(imageBoxDay.get(x));

            switch(DailyItems.get(x).getRarity()){
                case "uncommon":
                    boxDay.get(x).setBackground(getResources().getDrawable(R.drawable.uncommon_g));
                    break;
                case "rare":
                    boxDay.get(x).setBackground(getResources().getDrawable(R.drawable.rare_g));
                    break;
                case "epic":
                    boxDay.get(x).setBackground(getResources().getDrawable(R.drawable.epic_g));
                    break;
                case "legendary":
                    boxDay.get(x).setBackground(getResources().getDrawable(R.drawable.legendary_g));
                    break;
            }
        }

        for(int x = 0; x < 2; x++){
            nameBoxWeek.get(x).setText(WeeklyItems.get(x).getName());
            priceBoxWeek.get(x).setText(WeeklyItems.get(x).getPrice());

            Picasso.with(this.getContext()).load(WeeklyItems.get(x).getImageLink()).fit().into(imageBoxWeek.get(x));

            switch(WeeklyItems.get(x).getRarity()){
                case "uncommon":
                    boxWeek.get(x).setBackground(getResources().getDrawable(R.drawable.uncommon_g));
                    break;
                case "rare":
                    boxWeek.get(x).setBackground(getResources().getDrawable(R.drawable.rare_g));
                    break;
                case "epic":
                    boxWeek.get(x).setBackground(getResources().getDrawable(R.drawable.epic_g));
                    break;
                case "legendary":
                    boxWeek.get(x).setBackground(getResources().getDrawable(R.drawable.legendary_g));
                    break;
            }
        }

    }

    public void getToken(){

        AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("Authorization", "basic ZWM2ODRiOGM2ODdmNDc5ZmFkZWEzY2IyYWQ4M2Y1YzY6ZTFmMzFjMjExZjI4NDEzMTg2MjYyZDM3YTEzZmM4NGQ="); //Basic Token
        //Body - parameters
        RequestParams body = new RequestParams();
        body.add("grant_type", "password"); body.add("username", "dragxgaming@yahoo.com"); body.add("password", "Dukeray12"); body.add("includePerms", "true");
        //Get the access token and expire time
        client.post(OAUTH_TOKEN, body, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject response = new JSONObject(new String(responseBody));
                    //Get / set variables
                    access_token = response.getString("access_token");
                    System.out.println("NEW CODE: " + access_token);
                    //save the new variables for next time
                    mEditor.putString("accessToken", access_token);//save token
                    mEditor.apply();
                    //After we have the new token, get the item shop
                    getItemShop();
                } catch (JSONException e) { System.out.println("ERROR " + e.getMessage()); }
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) { System.out.println("FAIL 0: " + error.getMessage()); }
        });
    }


    @Override
    public void onClick(View view){

        try {

            DailyItems.add(0, DailyItems.get(DailyItems.size() - 1));
            DailyItems.remove(DailyItems.size() - 1);
            try {
                updateView();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception E){}

    }

}
