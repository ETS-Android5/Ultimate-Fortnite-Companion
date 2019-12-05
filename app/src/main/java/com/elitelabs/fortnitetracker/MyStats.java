package com.elitelabs.fortnitetracker;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class MyStats extends Fragment implements View.OnClickListener {

    private String accountName = "", platform = "", season= "";
    private gamemode Solos, Duos, Squads;
    private data solo, duo, squad;
    private Button xbox, psn, pc;
    private ViewPager viewPager;
    private EditText nameView;
    private TextView seasonView;
    private Boolean search = false, shadow = false;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;

    private int tries = 0;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)  {

        View view = inflater.inflate(R.layout.show_stats, container, false);

        mPrefs = this.getActivity().getSharedPreferences("label", 0); //Restored Values (act name)
        mEditor = mPrefs.edit();


        if(!mPrefs.getBoolean("firstTime", false)){
            AlertDialog HelpDialog = new AlertDialog.Builder(this.getContext())
                    .setTitle("How-To")
                    .setMessage("Swipe right and left to switch between viewing solo, duo and squad stats!")
                    .setNegativeButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mEditor.putBoolean("firstTime", true).commit();
                        }
                        //}).setPositiveButton("Got it!", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { }
                    }).show();
        }

        if(!mPrefs.getBoolean("firstTimeDes", false)) {
            android.app.AlertDialog DesDialog = new android.app.AlertDialog.Builder(this.getContext())
                    .setMessage("The layout and design has changed. \nTo apply new design go to SETTINGS and click on the color option of your choice under the \"NEW LAYOUT\" title")
                    .setTitle("New Design")
                    .setNegativeButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mPrefs.edit().putBoolean("firstTimeDes", true).commit();
                       /*     mPrefs.edit().putBoolean("NewLayout", true).commit();
                            mEditor.putString("themeColor", "blue").commit();
                            //Restart app to update layout
                            Intent intent = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);*/
                        }
                       /*   }).setPositiveButton("Keep this design", new DialogInterface.OnClickListener() {
                             @Override public void onClick(DialogInterface dialog, int which) {
                               mPrefs.edit().putBoolean("firstTimeDes", true).commit();
                         }*/
                    }).show();
        }


        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            shadow = true;
        } else{ }

        //Create fragments and the corresponding data classes
        Solos = new gamemode();
        solo = new data("Solo");
        Duos = new gamemode();
        duo = new data("Duo");
        Squads = new gamemode();
        squad = new data("Squad");

        //Create View Items
        xbox =  view.findViewById(R.id.xbox);
        psn =  view.findViewById(R.id.ps4);
        pc = view.findViewById(R.id.pc);
        nameView = view.findViewById(R.id.acctName);
        seasonView = view.findViewById(R.id.season);

        mPrefs = this.getActivity().getSharedPreferences("label", 0); //Restored Values (act name)
        accountName = mPrefs.getString("searchName", "");
        platform = mPrefs.getString("searchPlatform", "");
        season = mPrefs.getString("searchSeason", "");
        if(accountName.isEmpty()){
            accountName = mPrefs.getString("accountName", "");
            platform = mPrefs.getString("platform", "");
            season = mPrefs.getString("season", "");
        }
        else {
            search = true;
            nameView.setEnabled(true);
        }

        mPrefs.edit().remove("searchName").commit();
        mPrefs.edit().remove("searchPlatform").commit();
        mPrefs.edit().remove("searchSeason").commit();


        //Create Fragment View with tabs
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);  //The view pager is where the fragments are shown
        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        //Add fragments
        FragmentsAdapter adapter = new FragmentsAdapter(this.getActivity().getSupportFragmentManager());
        adapter.addFragment(Solos, "Solo");
        adapter.addFragment(Duos, "Duo");
        adapter.addFragment(Squads, "Squad");
        viewPager.setAdapter(adapter); //set the view to show fragments

        if(shadow) {
            xbox.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            psn.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            pc.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
        }else{
            xbox.setTextColor(getResources().getColor(R.color.grey));
            psn.setTextColor(getResources().getColor(R.color.grey));
            pc.setTextColor(getResources().getColor(R.color.grey));
        }

        //Set the correct platform button to be highlighted
        switch (platform)
        {
            case "psn":
                if(shadow) {psn.setShadowLayer(90, 0, 0, getResources().getColor(R.color.white));} else{ psn.setTextColor(getResources().getColor(R.color.white));}
                break;
            case "xb1":
                if(shadow) { xbox.setShadowLayer(90, 0, 0, getResources().getColor(R.color.white));} else {xbox.setTextColor(getResources().getColor(R.color.white));}
                break;
            case "pc":
               if(shadow) { pc.setShadowLayer(90, 0, 0, getResources().getColor(R.color.white));} else {pc.setTextColor(getResources().getColor(R.color.white));}
                break;
        }

        nameView.setText(accountName);
        seasonView.setText(season);

        getStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName);

        //
        seasonView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(season.equals("Lifetime"))
                    season = "Season 9";
                else
                    season = "Lifetime";

                mPrefs.edit().putString("season", season).commit();
                seasonView.setText(season);
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName);
            }
        });


        if(search)
        {
            nameView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nameView.setCursorVisible(true);
                }
            });
        }

        nameView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER){
                    accountName = nameView.getText().toString().trim();
                    nameView.setText(accountName);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(nameView.getWindowToken(), 0);
                    nameView.setCursorVisible(false);
                    getStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName);
                }
                return false;
            }
        });

        //BUTTON LISTENERS
        //Change platforms - xbox
        xbox.setOnClickListener(this);
        //Change platforms - playstation
        psn.setOnClickListener(this);
        //Change platforms - pc
        pc.setOnClickListener(this);

        return view;

    }


    //Get data from API
    public void getStats(String url){

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String prefix = "";
                try {
                    JSONObject json = response; //The entire http response, contains all data for the player

                    if(!season.equals("Lifetime"))
                        prefix = "curr_";

                    //SET ALL DATA
                    setGamemodeData(solo, prefix + "p2", "top10", "top25", json);
                    setGamemodeData(duo, prefix + "p10", "top5", "top12", json);
                    setGamemodeData(squad, prefix + "p9", "top3", "top6", json);

                    //Set the info in the fragment
                    Solos.setInfo(solo);
                    Duos.setInfo(duo);
                    Squads.setInfo(squad);
                    //Update the view of the fragment
                    try {
                        Solos.update("Top 10", "Top 25");
                        Duos.update("Top 5", "Top 12");
                        Squads.update("Top 3", "Top 6");
                    }catch(Exception xx){
                        System.out.println("PROBLEM " + xx.getLocalizedMessage());
                    }

                } catch  (JSONException e)  {
                    System.out.println("ERROR:  " + e.getMessage());
                    if(e.getMessage().contains("<!DOCTYPE"))
                        try{new Toast(MyStats.this.getActivity().getApplicationContext()).makeText(MyStats.this.getActivity().getApplicationContext(), "E: Servers Overloaded. Check back later", Toast.LENGTH_LONG).show();}catch(Exception ex){}
                    else if(e.getMessage().contains("no value")){
                        try{new Toast(MyStats.this.getActivity().getApplicationContext()).makeText(MyStats.this.getActivity().getApplicationContext(), "User not found. Please check spelling and platform", Toast.LENGTH_LONG).show();}catch(Exception ex){}
                    }else
                        try{new Toast(MyStats.this.getActivity().getApplicationContext()).makeText(MyStats.this.getActivity().getApplicationContext(), "E: " + e.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception ex){}
                    //new Toast(MyStats.this.getActivity().getApplicationContext()).makeText(MyStats.this.getActivity().getApplicationContext(), "Make sure name and platform are inputted correctly. Consoles must be linked to and Epic account", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("FAIL: " + error.getMessage()); //show error message on console if it fails
               /* if(tries <= 1){
                    tries++;
                    getStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName);
                } */
                try{new Toast(MyStats.this.getActivity().getApplicationContext()).makeText(MyStats.this.getActivity().getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception x){}
            }

        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                //headers.put("Content-Type", "application/json");
                headers.put("TRN-Api-Key", "d5b68a2b-840f-4f2f-8862-7f32f67c3a08");
                return headers;
            }
        };

        RequestQueue q = Volley.newRequestQueue(getActivity().getApplicationContext());
        q.add(req);
    }


    //Get each stat from the json list for the sent in gamemode
    public void setGamemodeData(data gameType, String gameVal, String top2, String top3, JSONObject json) throws JSONException{
        gameType.setWins(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("top1").getString("displayValue"));
        gameType.setSeconds(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject(top2).getString("displayValue"));
        gameType.setThirds(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject(top3).getString("displayValue"));
        gameType.setKills(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("kills").getString("displayValue"));
        gameType.setMatches(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("matches").getString("displayValue"));
        gameType.setKd(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("kd").getString("displayValue"));
        gameType.setScore(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("score").getString("value"));
        gameType.setScorePerMatch(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("scorePerMatch").getInt("value"));
        gameType.setKPM(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("kpg").getString("displayValue"));
       try{gameType.setWinP(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("winRatio").getString("displayValue"));} catch(JSONException e){gameType.setWinP("N/A");}
       //If a person has no wins, the win ratio stat doesnt exist, which causes an error. to fix this, surround winRatio with try catch (only need to do this for winratio)
    }


    @Override
    public void onClick(View view) {
        //Reset all buttons
        if(shadow) {
            xbox.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            psn.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            pc.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
        }else{
            xbox.setTextColor(getResources().getColor(R.color.grey));
            psn.setTextColor(getResources().getColor(R.color.grey));
            pc.setTextColor(getResources().getColor(R.color.grey));
        }
        //Only color the correct button
        switch(view.getId()){
            case R.id.xbox:
                if(shadow) xbox.setShadowLayer(90, 0, 0, getResources().getColor(R.color.white)); else xbox.setTextColor(getResources().getColor(R.color.white));
                platform = "xbl";
                break;
            case R.id.ps4:
                if(shadow) psn.setShadowLayer(90, 0, 0, getResources().getColor(R.color.white)); else psn.setTextColor(getResources().getColor(R.color.white));
                platform ="psn";
                break;
            case R.id.pc:
                if(shadow) pc.setShadowLayer(90, 0, 0, getResources().getColor(R.color.white)); else pc.setTextColor(getResources().getColor(R.color.white));
                platform = "pc";
                break;
        }
        getStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName); //new platform stats
    }
}
