package com.elitelabs.fortnitetracker;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
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
import com.google.android.gms.ads.internal.overlay.zzo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewCompareStats extends android.support.v4.app.Fragment {

    private String accountName1 = "", platform1 = "pc";
    private String accountName2 = "", platform2 = "pc";
    private CompareGamemode Solos, Duos, Squads;
    private data solo1, duo1, squad1;
    private data solo2, duo2, squad2;
   // private Button xbox1, psn1, pc1, xbox2, psn2, pc2;
    private TextView platform1T, platform2T, soloT, duoT, squadT;
    private ViewPager viewPager;
    private EditText name1, name2;
    private SharedPreferences mPrefs;
    private Resources.Theme themes;
    TypedValue storedValueInTheme;
    private TextView getStats;
    private boolean shadow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.compare_stats_new, container, false);

        themes = getActivity().getTheme(); //needed for theme colors
        storedValueInTheme = new TypedValue(); //^


        //Create fragments and the corresponding data classes
        Solos = new CompareGamemode();
        solo1 = new data("Solo");
        Duos =  new CompareGamemode();
        duo1 = new data("Duo");
        Squads = new CompareGamemode();
        squad1 = new data("Squad");
        solo2 = new data("Solo");
        duo2 = new data("Duo");
        squad2 = new data("Squad");

        Squads.NTop2 = "Top 3"; Squads.NTop3 = "Top 6";
        Duos.NTop2 = "Top 5"; Duos.NTop3 = "Top 12";
        Solos.NTop2 = "Top 10"; Solos.NTop3 = "Top 25";

        //Create View Items
        soloT = view.findViewById(R.id.soloMode);
        duoT = view.findViewById(R.id.duoMode);
        squadT = view.findViewById(R.id.squadMode);
        name1 = (EditText) view.findViewById(R.id.p1Name);
        name2 = (EditText) view.findViewById(R.id.p2Name);
        platform1T = view.findViewById(R.id.platform1);
        platform2T = view.findViewById(R.id.platform2);


        name1.setMovementMethod(null);
        name2.setMovementMethod(null);


        getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, Solos).commit();

        getStats = view.findViewById(R.id.getStats);
        getStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name1.getText().toString().equals("") || name2.getText().toString().equals("")){
                    try{new Toast(NewCompareStats.this.getActivity().getApplicationContext()).makeText(NewCompareStats.this.getActivity().getApplicationContext(), "Please enter names for both players", Toast.LENGTH_LONG).show();}catch(Exception x){}
                }
                else{
                    getStats.setText("Update Stats");
                    accountName1 = name1.getText().toString();
                    accountName2 = name2.getText().toString();
                    getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1);
                    getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2);
                }
            }
        });


        name1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name1.setCursorVisible(true);
            }
        });
        name2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name2.setCursorVisible(true);
            }
        });

        name1.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER){
                    accountName1 = name1.getText().toString().trim();
                    name1.setText(accountName1.trim());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(name1.getWindowToken(), 0);
                    name1.setCursorVisible(false);
                    //if(!platform1.equals("-"))
                    //    getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1);
                }
                return false;
            }
        });

        name2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER){
                    accountName2 = name2.getText().toString().trim();
                    name2.setText(accountName2.trim());
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(name2.getWindowToken(), 0);
                    name2.setCursorVisible(false);
                    //if(!platform2.equals("-"))
                    //    getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2);
                }
                return false;
            }
        });


        platform1T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(platform1T.getText().toString()){
                    case "PS4": case "PSN":
                        platform1T.setText("XBL");
                        platform1 = "xbl";
                        System.out.println("TEST" + platform1 + accountName1);
                        break;
                    case "XBL":
                        platform1T.setText("PC");
                        platform1 = "pc";
                        break;
                    case "PC":
                        platform1T.setText("PS4");
                        platform1 = "psn";
                        break;
                }
                //getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1);
            }
        });

        platform2T.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch(platform2T.getText().toString()){
                    case "PS4": case "PSN":
                        platform2T.setText("XBL");
                        platform2 = "xbl";
                        break;
                    case "XBL":
                        platform2T.setText("PC");
                        platform2 = "pc";
                        break;
                    case "PC":
                        platform2T.setText("PS4");
                        platform2 = "psn";
                        break;
                }
                //getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2);
            }
        });


        //Listeners for changing gamemode
        soloT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duoT.setTextColor(getResources().getColor(R.color.grey2));
                duoT.setTextSize(22);
                squadT.setTextColor(getResources().getColor(R.color.grey2));
                squadT.setTextSize(22);
                soloT.setTextSize(30);
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    soloT.setTextColor(storedValueInTheme.data);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, Solos).commit();
                //getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1);
                //getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2);

            }
        });

        duoT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soloT.setTextColor(getResources().getColor(R.color.grey2));
                soloT.setTextSize(22);
                squadT.setTextColor(getResources().getColor(R.color.grey2));
                squadT.setTextSize(22);
                duoT.setTextSize(30);
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    duoT.setTextColor(storedValueInTheme.data);
                //getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1);
                //getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, Duos).commit();

            }
        });

        squadT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                duoT.setTextColor(getResources().getColor(R.color.grey2));
                duoT.setTextSize(22);
                soloT.setTextColor(getResources().getColor(R.color.grey2));
                soloT.setTextSize(22);
                //squadT.setTextColor(getResources().getColor(R.color.colorAccent));
                squadT.setTextSize(30);
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    squadT.setTextColor(storedValueInTheme.data);
                getChildFragmentManager().beginTransaction().replace(R.id.fragment_container, Squads).commit();
                //getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1);
                //getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2);
            }
        });

        return view;
    }


    //Get data from API
    public void getStats(String url, int Player){
        final int player = Player;

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String prefix = "";
                try {
                    JSONObject json = response; //The entire http response, contains all data for the player

                    //SET ALL DATA
                    switch (player)
                    {
                        case 1:
                            setGamemodeData(solo1, "p2", "top10", "top25", json);
                            setGamemodeData(duo1, "p10", "top5", "top12", json);
                            setGamemodeData(squad1, "p9", "top3", "top6", json);
                            //Set the info in the fragment
                            Solos.setP1(solo1);
                            Duos.setP1(duo1);
                            Squads.setP1(squad1);
                            //Update the view of the fragment
                            try{ Solos.updateP1("Top 10", "Top 25"); } catch (Exception E1){}
                            try { Duos.updateP1("Top 5", "Top 12"); } catch (Exception E1){}
                            try { Squads.updateP1("Top 3", "Top 6"); } catch (Exception E1){}
                            break;

                        case 2:
                            setGamemodeData(solo2, "p2", "top10", "top25", json);
                            setGamemodeData(duo2, "p10", "top5", "top12", json);
                            setGamemodeData(squad2, "p9", "top3", "top6", json);
                            //Set the info in the fragment
                            Solos.setP2(solo2);
                            Duos.setP2(duo2);
                            Squads.setP2(squad2);
                            //Update the view of the fragment
                            try{ Solos.updateP2("Top 10", "Top 25"); } catch (Exception E2){}
                            try { Duos.updateP2("Top 5", "Top 12"); } catch (Exception E2){}
                            try { Squads.updateP2("Top 3", "Top 6"); } catch (Exception E2){}
                            break;
                    }

                } catch  (JSONException e)  {
                    System.out.println("ERROR:  " + e.getMessage());
                    if(e.getMessage().contains("<!DOCTYPE"))
                        try{new Toast(NewCompareStats.this.getActivity().getApplicationContext()).makeText(NewCompareStats.this.getActivity().getApplicationContext(), "E: Servers Overloaded. Check back later", Toast.LENGTH_LONG).show();}catch(Exception ex){}
                    else
                        try{new Toast(NewCompareStats.this.getActivity().getApplicationContext()).makeText(NewCompareStats.this.getActivity().getApplicationContext(), "E: " + e.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception ex){}
                    //new Toast(MyStats.this.getActivity().getApplicationContext()).makeText(MyStats.this.getActivity().getApplicationContext(), "Make sure name and platform are inputted correctly. Consoles must be linked to and Epic account", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("FAIL: " + error.getMessage()); //show error message on console if it fails
                try{new Toast(NewCompareStats.this.getActivity().getApplicationContext()).makeText(NewCompareStats.this.getActivity().getApplicationContext(), "Please try again later: " + error.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception x){}
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

        /*AsyncHttpClient client = new AsyncHttpClient();
        client.addHeader("TRN-Api-Key", "d5b68a2b-840f-4f2f-8862-7f32f67c3a08"); //API Key
        //Get Response
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody)); //The entire http response, contains all data for the player

                    //SET ALL DATA
                    switch (player)
                    {
                        case 1:
                            setGamemodeData(solo1, "p2", "top10", "top25", json);
                            setGamemodeData(duo1, "p10", "top5", "top12", json);
                            setGamemodeData(squad1, "p9", "top3", "top6", json);
                            //Set the info in the fragment
                            Solos.setP1(solo1);
                            Duos.setP1(duo1);
                            Squads.setP1(squad1);
                            //Update the view of the fragment
                            Solos.updateP1("Top 10", "Top 25");
                            Duos.updateP1("Top 5", "Top 12");
                            Squads.updateP1("Top 3", "Top 6");
                            break;

                        case 2:
                            setGamemodeData(solo2, "p2", "top10", "top25", json);
                            setGamemodeData(duo2, "p10", "top5", "top12", json);
                            setGamemodeData(squad2, "p9", "top3", "top6", json);
                            //Set the info in the fragment
                            Solos.setP2(solo2);
                            Duos.setP2(duo2);
                            Squads.setP2(squad2);
                            //Update the view of the fragment
                            Solos.updateP2("Top 10", "Top 25");
                            Duos.updateP2("Top 5", "Top 12");
                            Squads.updateP2("Top 3", "Top 6");
                            break;
                    }

                } catch (JSONException e) {
                    System.out.println("ERROR:  " + e.getMessage());
                    if(e.getMessage().contains("<!DOCTYPE"))
                        new Toast(CompareStats.this.getActivity().getApplicationContext()).makeText(CompareStats.this.getActivity().getApplicationContext(), "E: Servers Overloaded. Check back later", Toast.LENGTH_LONG).show();
                    else
                        new Toast(CompareStats.this.getActivity().getApplicationContext()).makeText(CompareStats.this.getActivity().getApplicationContext(), "E: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    } //if something goes wrong output the error message
            }
            @Override public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                System.out.println("FAIL: " + error.getMessage()); //show error message on console if it fails
                new Toast(CompareStats.this.getActivity().getApplicationContext()).makeText(CompareStats.this.getActivity().getApplicationContext(), "F:" + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });*/
    }


    //Get each stat from the json list for the sent in gamemode
    public void setGamemodeData(data gameType, String gameVal, String top2, String top3, JSONObject json) throws JSONException {
        gameType.setWins(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("top1").getString("displayValue"));
        gameType.setSeconds(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject(top2).getString("displayValue"));
        gameType.setThirds(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject(top3).getString("displayValue"));
        gameType.setKills(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("kills").getString("displayValue"));
        gameType.setMatches(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("matches").getString("displayValue"));
        gameType.setKd(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("kd").getString("displayValue"));
        gameType.setScore(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("score").getString("value"));
        gameType.setKPM(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("kpg").getString("displayValue"));
        try{gameType.setWinP(json.getJSONObject("stats").getJSONObject(gameVal).getJSONObject("winRatio").getString("displayValue"));} catch(JSONException e){}
        //If a person has no wins, the win ratio stat doesnt exist, which causes an error. to fix this, surround winRatio with try catch (only need to do this for winratio)
    }


}
