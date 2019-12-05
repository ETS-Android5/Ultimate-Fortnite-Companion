package com.elitelabs.fortnitetracker;


import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class MoreData extends Fragment {

    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private String access_token, expire, DATA = "https://fortnite-public-service-prod11.ol.epicgames.com/fortnite/api/game/v2/profile/", DATA2 = "/client/QueryProfile?profileId=athena&rvn=-1", LOOKUP = "https://persona-public-service-prod06.ol.epicgames.com/persona/api/public/account/lookup?q=", OAUTH_TOKEN = "https://account-public-service-prod03.ol.epicgames.com/account/api/oauth/token";
    private String username = "dukeraymon@live.com", password="Dukeray12", accountID, epicName="DragXGaming";
    private AlertDialog.Builder Dialog;
    private TextView text;

    private boolean feat1 = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.item_shop, container, false);

        mPrefs = this.getActivity().getSharedPreferences("label", 0); //Restored Values (act name)
        mEditor = mPrefs.edit();

        username = "exequielgiorlan@gmail.com";
        password = "Sexybeast1";

        text = view.findViewById(R.id.time);


        //Get saved access token and expire time
        access_token = mPrefs.getString("accessToken", "");
        accountID = mPrefs.getString("accountID", "");
        System.out.println(access_token);
        //getToken();
        getData();


        return view;
    }



    public void getToken(){

        String url = "https://account-public-service-prod03.ol.epicgames.com/account/api/oauth/token";

        StringRequest req = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        try{
                            access_token = new JSONObject(response).getString("access_token");
                            System.out.println("NEW CODE: " + access_token);
                            accountID = new JSONObject(response).getString("account_id"); //save the new variables for next time

                            mEditor.putString("accountID", accountID);
                            mEditor.putString("accessToken", access_token).commit();//save token

                            //getID();
                            getData();

                        } catch (Exception e) { System.out.println("FAIL: " + e.getMessage() ); }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("VOLLEY FAIL 1: " + error.getMessage());
                        error.printStackTrace();
                    }
                }) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "basic ZWM2ODRiOGM2ODdmNDc5ZmFkZWEzY2IyYWQ4M2Y1YzY6ZTFmMzFjMjExZjI4NDEzMTg2MjYyZDM3YTEzZmM4NGQ=");
                return headers;
            }

            @Override
            public String getBodyContentType() { return "application/x-www-form-urlencoded"; }

            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<String, String>();
                params.put("grant_type", "password");
                params.put("username",username);
                params.put("password",password);

                return params;
            }
        };

        RequestQueue q = Volley.newRequestQueue(getActivity().getApplicationContext());
        q.add(req);

    }


    public void getData(){

        String url = DATA + accountID + DATA2;

        StringRequest req = new StringRequest
                (Request.Method.POST, url, new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try{
                            System.out.println("WORKED" + response);
                            JSONObject resp = new JSONObject(response);
                            JSONObject items = resp.getJSONArray("profileChanges").getJSONObject(0).getJSONObject("profile").getJSONObject("items");


                            Iterator x = items.keys();
                            JSONArray arr = new JSONArray();

                            while(x.hasNext())
                            {
                                String key = (String) x.next();
                                JSONObject object = ((JSONObject) items.get(key)).put("id", key);
                                arr.put(object);
                            }

                            ArrayList<String> chars = new ArrayList<String>();
                            ArrayList<String> charIDs = new ArrayList<String>();
                            ArrayList<String> challenges = new ArrayList<String>();
                            ArrayList<String> challengeCompletion = new ArrayList<String>();

                            for(int i = 0; i < arr.length(); i++){
                                String temp = arr.getJSONObject(i).getString("templateId");
                                if(arr.getJSONObject(i).getString("templateId").contains("AthenaCharacter")){
                                    chars.add(temp);
                                    charIDs.add(arr.getJSONObject(i).getString("id"));
                                }
                                else if(temp.contains("Quest")){
                                    challenges.add(temp);
                                    challengeCompletion.add(arr.getJSONObject(i).getJSONObject("attributes").getString("quest_state"));
                                }
                            }

                            for(int i = 0; i < challenges.size(); i++)
                                System.out.println(challenges.get(i) + " - " + challengeCompletion.get(i));
                            for(int i = 0; i < chars.size(); i++)
                                System.out.println(chars.get(i) + " - " + charIDs.get(i));

                            //JSONObject stats = resp.getJSONArray("profileChanges").getJSONObject(0).getJSONObject("profile").getJSONObject("stats");

                        } catch (Exception e) { e.printStackTrace(); System.out.println("FAIL: " + e.getMessage() ); }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) { System.out.println("VOLLEY FAIL 3: " + error.getMessage()); getToken();}
                }) {

            @Override
            public byte[] getBody() {
                String str = "{\n" +
                        "\t\"X-EpicGames-ProfileRevisions\": [{\"profileId\":\"common_public\",\"clientCommandRevision\":0},{\"profileId\":\"common_core\",\"clientCommandRevision\":16},{\"profileId\":\"athena\",\"clientCommandRevision\":-1}]\n" +
                        "}";
                return str.getBytes();
            };

            @Override
            public String getBodyContentType()
            { return "application/json; charset=utf-8";}


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authorization", "bearer " + access_token);
                return headers;
            }
        };

        RequestQueue q = Volley.newRequestQueue(getActivity().getApplicationContext());
        q.add(req);

    }


    public void getID(){

        String url = LOOKUP + epicName;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try{
                            accountID = response.getString("id");
                            getData();

                        } catch (Exception e) {
                            System.out.println("FAIL: " + e.getMessage());
                        }

                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        System.out.println("VOLLEY FAIL 2: " + error.getMessage());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("Authentication", "bearer " + access_token);
                return headers;
            }
        };

        RequestQueue q = Volley.newRequestQueue(getActivity().getApplicationContext());
        q.add(jsonObjectRequest);
    }


}
