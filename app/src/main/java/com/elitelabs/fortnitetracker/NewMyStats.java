package com.elitelabs.fortnitetracker;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewMyStats extends Fragment {

    private TextView soloT, duoT, squadT, platformT, seasonT;
    private gamemode solos, duos, squads;
    private data solo, duo, squad;
    private SharedPreferences mPrefs;
    private String accountName = "", platform = "", season= "";
    private Boolean search = false;
    private EditText nameView;
    private Resources.Theme themes;
    TypedValue storedValueInTheme;

    private int tries = 0;


    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.show_stats_new, container, false);

        System.out.println("NEW LAYOUT");

        themes = getActivity().getTheme(); //needed for theme colors
        storedValueInTheme = new TypedValue(); //^

        soloT = view.findViewById(R.id.soloMode);
        duoT = view.findViewById(R.id.duoMode);
        squadT = view.findViewById(R.id.squadMode);
        nameView = view.findViewById(R.id.acctName);

        seasonT = view.findViewById(R.id.season);
        platformT = view.findViewById(R.id.platform);

        mPrefs = this.getActivity().getSharedPreferences("label", 0); //Restored Values (act name)

        ///////////////////
      /*  if(!mPrefs.getBoolean("firstTimeDes", false))
        {
            AlertDialog DesDialog = new AlertDialog.Builder(this.getContext())
                    .setMessage("The layout and design has changed. \nChange the color in the settings.\nYou can also change between the new and old design. Click on the \"LEGACY\" button to go back to the old layout")
                    .setTitle("New Design")
                    .setNegativeButton("Got it, thanks!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mPrefs.edit().putBoolean("firstTimeDes", true).commit();
                        }
                        // }).setPositiveButton("Got it!", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { }
                    }).show();
        }
*/
        //Create fragments and the corresponding data classes
        solos = new gamemode();
        solo = new data("Solo");
        duos = new gamemode();
        duo = new data("Duo");
        squads = new gamemode();
        squad = new data("Squad");
        squads.nTop2 = "Top 3"; squads.nTop3 = "Top 6";
        duos.nTop2 = "Top 5"; duos.nTop3 = "Top 12";
        solos.nTop2 = "Top 10"; solos.nTop3 = "Top 25";



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

        getStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName);
        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, solos).commit();
        nameView.setText(accountName);
        seasonT.setText(season);
        platformT.setText(platform.toUpperCase());


        //Listeners for changing gamemode
        soloT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duoT.setTextColor(getResources().getColor(R.color.grey2));
                duoT.setTextSize(24);
                squadT.setTextColor(getResources().getColor(R.color.grey2));
                squadT.setTextSize(24);
                soloT.setTextSize(32);
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    soloT.setTextColor(storedValueInTheme.data);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, solos).commit();
                //getStats("https://api.fortnitetracker.com/v1/profile/" + "psn" + "/" + "dragx-gaming");
            }
        });

        duoT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soloT.setTextColor(getResources().getColor(R.color.grey2));
                soloT.setTextSize(24);
                squadT.setTextColor(getResources().getColor(R.color.grey2));
                squadT.setTextSize(24);
                duoT.setTextSize(32);
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    duoT.setTextColor(storedValueInTheme.data);
                //  getStats("https://api.fortnitetracker.com/v1/profile/" + "psn" + "/" + "dragx-gaming");
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, duos).commit();
            }
        });

        squadT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duoT.setTextColor(getResources().getColor(R.color.grey2));
                duoT.setTextSize(24);
                soloT.setTextColor(getResources().getColor(R.color.grey2));
                soloT.setTextSize(24);
                //squadT.setTextColor(getResources().getColor(R.color.colorAccent));
                squadT.setTextSize(32);
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    squadT.setTextColor(storedValueInTheme.data);


                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, squads).commit();
                // getStats("https://api.fortnitetracker.com/v1/profile/" + "psn" + "/" + "dragx-gaming");
            }
        });



        seasonT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(seasonT.getText().equals("Lifetime"))
                {
                    season = "seasonal";
                    seasonT.setText("Season 9");
                }
                else
                {
                    season = "Lifetime";
                    seasonT.setText("Lifetime");
                }
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName);
            }
        });


        platformT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(platformT.getText().toString()){
                    case "PS4": case "PSN":
                        platformT.setText("XBL");
                        platform = "xbl";
                        break;
                    case "XBL":
                        platformT.setText("PC");
                        platform = "pc";
                        break;
                    case "PC":
                        platformT.setText("PS4");
                        platform = "psn";
                        break;
                }
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
                System.out.println(platform + "PLATFORM");
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
                    solos.setInfo(solo);
                    duos.setInfo(duo);
                    squads.setInfo(squad);
                    //Update the view of the fragment
                    try {
                        solos.update();
                        duos.update();
                        squads.update();
                    }catch(Exception xx){
                        System.out.println(xx.getMessage());
                    }

                } catch  (JSONException e)  {
                    System.out.println("ERROR:  " + e.getMessage());
                    if(e.getMessage().contains("<!DOCTYPE"))
                        try{new Toast(NewMyStats.this.getActivity().getApplicationContext()).makeText(NewMyStats.this.getActivity().getApplicationContext(), "E: Servers Overloaded. Check back later", Toast.LENGTH_LONG).show();}catch(Exception ex){}
                    else if (e.getMessage().contains("No value")){
                        try{new Toast(NewMyStats.this.getActivity().getApplicationContext()).makeText(NewMyStats.this.getActivity().getApplicationContext(), "User not found. Check spelling and platform", Toast.LENGTH_LONG).show();}catch(Exception ex){}
                    }
                    else
                        try{new Toast(NewMyStats.this.getActivity().getApplicationContext()).makeText(NewMyStats.this.getActivity().getApplicationContext(), "E: " + e.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception ex){}
                    //new Toast(MyStats.this.getActivity().getApplicationContext()).makeText(MyStats.this.getActivity().getApplicationContext(), "Make sure name and platform are inputted correctly. Consoles must be linked to and Epic account", Toast.LENGTH_LONG).show();
                }

                tries = 0;

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("FAIL: " + error.getMessage()); //show error message on console if it fails
                /*if(tries <= 1){
                    tries++;
                    System.out.println("AGAIN");
                    SystemClock.sleep(2000);
                    System.out.println("AFTER AGAIN");
                    //getStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName);
                }*/
                try{new Toast(NewMyStats.this.getActivity().getApplicationContext()).makeText(NewMyStats.this.getActivity().getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception x){}
                System.out.println("NULL");
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

}
