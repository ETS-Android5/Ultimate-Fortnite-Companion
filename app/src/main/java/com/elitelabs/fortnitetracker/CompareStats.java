package com.elitelabs.fortnitetracker;


import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 */
public class CompareStats extends android.support.v4.app.Fragment implements View.OnClickListener{

    private String accountName1 = "", platform1 = "-";
    private String accountName2 = "", platform2 = "-";
    private CompareGamemode Solos, Duos, Squads;
    private data solo1, duo1, squad1;
    private data solo2, duo2, squad2;
    private Button xbox1, psn1, pc1, xbox2, psn2, pc2;
    private ViewPager viewPager;
    private EditText name1, name2;
    private SharedPreferences mPrefs;

    private boolean shadow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.compare_stats, container, false);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            shadow = true;
        } else{ }

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

        //Create View Items
        xbox1 = (Button) view.findViewById(R.id.xbox);
        psn1 = (Button) view.findViewById(R.id.ps4);
        pc1 = (Button) view.findViewById(R.id.pc);
        name1 = (EditText) view.findViewById(R.id.p1Name);
        xbox2 = (Button) view.findViewById(R.id.xboxp2);
        psn2 = (Button) view.findViewById(R.id.ps4p2);
        pc2 = (Button) view.findViewById(R.id.pcp2);
        name2 = (EditText) view.findViewById(R.id.p2Name);

        name1.setMovementMethod(null);
        name2.setMovementMethod(null);

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
                    name1.setText(accountName1);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(name1.getWindowToken(), 0);
                    name1.setCursorVisible(false);
                    if(!platform1.equals("-"))
                        getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1);
                }
                return false;
            }
        });

        name2.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == KeyEvent.KEYCODE_ENTER){
                    accountName2 = name2.getText().toString().trim();
                    name2.setText(accountName2);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(name2.getWindowToken(), 0);
                    name2.setCursorVisible(false);
                    if(!platform2.equals("-"))
                        getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2);
                }
                return false;
            }
        });

        //BUTTON LISTENERS
        //Change platforms - xbox
        xbox1.setOnClickListener(this);
        //Change platforms - playstation
        psn1.setOnClickListener(this);
        //Change platforms - pc
        pc1.setOnClickListener(this);
        //Change platforms - xbox
        xbox2.setOnClickListener(this);
        //Change platforms - playstation
        psn2.setOnClickListener(this);
        //Change platforms - pc
        pc2.setOnClickListener(this);

        /*Button compare = (Button) view.findViewById(R.id.compare);
        compare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountName1 = name1.getText().toString();
                accountName2 = name2.getText().toString();
                name2.clearFocus();
                name1.setSelection(0);
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1);
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2);
            }
        });  */


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

                } catch  (JSONException e)  {
                    System.out.println("ERROR:  " + e.getMessage());
                    if(e.getMessage().contains("<!DOCTYPE"))
                        try{new Toast(CompareStats.this.getActivity().getApplicationContext()).makeText(CompareStats.this.getActivity().getApplicationContext(), "E: Servers Overloaded. Check back later", Toast.LENGTH_LONG).show();}catch(Exception ex){}
                    else
                        try{new Toast(CompareStats.this.getActivity().getApplicationContext()).makeText(CompareStats.this.getActivity().getApplicationContext(), "E: " + e.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception ex){}
                    //new Toast(MyStats.this.getActivity().getApplicationContext()).makeText(MyStats.this.getActivity().getApplicationContext(), "Make sure name and platform are inputted correctly. Consoles must be linked to and Epic account", Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("FAIL: " + error.getMessage()); //show error message on console if it fails
                try{new Toast(CompareStats.this.getActivity().getApplicationContext()).makeText(CompareStats.this.getActivity().getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception x){}
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


    @Override
    public void onClick(View view) {
        name1.setSelection(0);
        name2.setSelection(0);
        //Only color the correct button
        switch(view.getId()){
            case R.id.xbox:
                one();
                if(shadow) xbox1.setShadowLayer(50, 0, 0, getResources().getColor(R.color.white)); else xbox1.setTextColor(getResources().getColor(R.color.white));
                platform1 = "xbl";
                accountName1 = name1.getText().toString();
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1); //new platform stats
                break;
            case R.id.ps4:
                one();
                if(shadow) psn1.setShadowLayer(50, 0, 0, getResources().getColor(R.color.white)); else psn1.setTextColor(getResources().getColor(R.color.white));
                platform1 = "psn";
                accountName1 = name1.getText().toString();
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1); //new platform stats
                break;
            case R.id.pc:
                one();
                if(shadow) pc1.setShadowLayer(50, 0, 0, getResources().getColor(R.color.white)); else pc1.setTextColor(getResources().getColor(R.color.white));
                platform1 = "pc";
                accountName1 = name1.getText().toString();
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform1 + "/" + accountName1, 1); //new platform stats
                break;
            case R.id.xboxp2:
                two();
                if(shadow) xbox2.setShadowLayer(50, 0, 0, getResources().getColor(R.color.white)); else xbox2.setTextColor(getResources().getColor(R.color.white));
                accountName2 = name2.getText().toString();
                platform2 = "xbl";
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2); //new platform stats
                break;
            case R.id.ps4p2:
                two();
                if(shadow) psn2.setShadowLayer(50, 0, 0, getResources().getColor(R.color.white)); else psn2.setTextColor(getResources().getColor(R.color.white));
                platform2 = "psn";
                accountName2 = name2.getText().toString();
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2); //new platform stats
                break;
            case R.id.pcp2:
                two();
                if(shadow) pc2.setShadowLayer(50, 0, 0, getResources().getColor(R.color.white)); else pc2.setTextColor(getResources().getColor(R.color.white));
                accountName2 = name2.getText().toString();
                platform2 = "pc";
                getStats("https://api.fortnitetracker.com/v1/profile/" + platform2 + "/" + accountName2, 2); //new platform stats
                break;
        }
    }

    public void one(){
        if(shadow) {
            xbox1.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            psn1.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            pc1.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
        }else{
            xbox1.setTextColor(getResources().getColor(R.color.grey));
            psn1.setTextColor(getResources().getColor(R.color.grey));
            pc1.setTextColor(getResources().getColor(R.color.grey));
        }

        accountName1 = name1.getText().toString().trim();
    }
    public void two(){
        if(shadow) {
            xbox2.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            psn2.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            pc2.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
        }else{
            xbox2.setTextColor(getResources().getColor(R.color.grey));
            psn2.setTextColor(getResources().getColor(R.color.grey));
            pc2.setTextColor(getResources().getColor(R.color.grey));
        }
        accountName2 = name2.getText().toString().trim();
    }
}
