package com.elitelabs.fortnitetracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class NewFindMyStats extends Fragment implements View.OnClickListener {

    private String accountName = "", platform = "", season = "";
    private SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    private TextView getNewStats, xbox, psn, pc, lifetime, seasonal;
    private EditText noActName;
    private Boolean checked = false;
    private Resources.Theme themes;
    TypedValue storedValueInTheme;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.search_stats_new, container, false);

        themes = getActivity().getTheme(); //needed for theme colors
        storedValueInTheme = new TypedValue(); //^

        mPrefs = getActivity().getSharedPreferences("label", 0); //Restored Values (act name)
        mEditor = mPrefs.edit();
        //If statement for already there account ...
        accountName = mPrefs.getString("accountName", "");
        platform = mPrefs.getString("platform", "");
        //accountName = ""; platform = "";
        if(!accountName.isEmpty() && !platform.isEmpty())
        {
            //close this and open the other one#
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewMyStats()).commit();
        }
        
        
        //Create View Items
        xbox =  view.findViewById(R.id.xbox);
        psn =  view.findViewById(R.id.ps4);
        pc =  view.findViewById(R.id.pc);
        getNewStats =  view.findViewById(R.id.getStats);
        noActName = view.findViewById(R.id.searchName);

        seasonal = view.findViewById(R.id.seasonal);
        lifetime = view.findViewById(R.id.lifetime);

        view.findViewById(R.id.info).setVisibility(View.VISIBLE);




        //set season to lifetime as default
        mEditor.putString("season", "Lifetime").commit();



        seasonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    seasonal.setTextColor(storedValueInTheme.data);
                seasonal.setTextSize(36);
                lifetime.setTextColor(getResources().getColor(R.color.grey2));
                lifetime.setTextSize(28);
                mEditor.putString("searchSeason", "Season 9").commit();
            }
        });

        lifetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    lifetime.setTextColor(storedValueInTheme.data);
                lifetime.setTextSize(36);
                seasonal.setTextColor(getResources().getColor(R.color.grey2));
                seasonal.setTextSize(28);
                mEditor.putString("searchSeason", "Lifetime").commit();
            }
        });


        noActName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noActName.setCursorVisible(true);
            }
        });

        noActName.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == keyEvent.KEYCODE_ENTER)
                {
                    //get rid of the enter after the enter key is pressed
                    String temp = noActName.getText().toString().trim();
                    noActName.setText(temp);
                    hideKeyboard(view);
                }
                return false;
            }
        });


        getNewStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //No previous stats - save accountName and platform
                accountName = noActName.getText().toString().trim();
                getNewStats.setText("LOADING");
                checkStats("https://api.fortnitetracker.com/v1/profile/" + platform + "/" + accountName); //new platform stats
            }
        });


        //Change platforms - xbox
        xbox.setOnClickListener(this);
        //Change platforms - playstation
        psn.setOnClickListener(this);
        //Change platforms - pc
        pc.setOnClickListener(this);


        return view;

    }


    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        noActName.setCursorVisible(false);
    }


    @Override
    public void onClick(View view) {
        hideKeyboard(view);
        //Reset all texts
        psn.setTextColor(getResources().getColor(R.color.grey2));
        psn.setTextSize(28);
        pc.setTextColor(getResources().getColor(R.color.grey2));
        pc.setTextSize(28);
        xbox.setTextColor(getResources().getColor(R.color.grey2));
        xbox.setTextSize(28);
        //Only color the correct button
        switch(view.getId()){
            case R.id.xbox:
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    xbox.setTextColor(storedValueInTheme.data);
                xbox.setTextSize(36);
                platform = "xbl";
                break;
            case R.id.ps4:
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    psn.setTextColor(storedValueInTheme.data);
                psn.setTextSize(36);
                platform ="psn";
                break;
            case R.id.pc:
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    pc.setTextColor(storedValueInTheme.data);
                pc.setTextSize(36);
                platform = "pc";
                break;
        }
    }



    //Get data from API
    public void checkStats(String url){

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET,url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                String prefix = "";
                try {
                    JSONObject json = response; //The entire http response, contains all data for the player
                    json.getJSONObject("stats");
                    checked = true;

                    if(checked){
                        mEditor.putString("accountName", accountName);
                        mEditor.putString("platform", platform);
                        mEditor.commit();
                        myStats();
                    }

                } catch (JSONException e) {
                    System.out.println("ERROR" + e.getMessage());
                    try{new Toast(NewFindMyStats.this.getActivity()).makeText(NewFindMyStats.this.getActivity().getApplicationContext(), "Not Found, please check spelling and platform. For help visit Help tab", Toast.LENGTH_LONG).show();}catch(Exception ex){}
                    getNewStats.setText("GET STATS!");
                } //if something goes wrong output the error message
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("FAIL: " + error.getMessage()); //show error message on console if it fails
                try{new Toast(NewFindMyStats.this.getActivity().getApplicationContext()).makeText(NewFindMyStats.this.getActivity().getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception x){}
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



    public void myStats()
    {
        try{
            NewFindMyStats.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewMyStats()).commit();
        }catch(NullPointerException e){try{new Toast(NewFindMyStats.this.getActivity().getApplicationContext()).makeText(NewFindMyStats.this.getActivity().getApplicationContext(), "Please Try Again Later", Toast.LENGTH_LONG).show();}catch(Exception x){} }
    }

}
