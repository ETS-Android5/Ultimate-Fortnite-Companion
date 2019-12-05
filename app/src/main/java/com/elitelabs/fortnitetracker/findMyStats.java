package com.elitelabs.fortnitetracker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

public class findMyStats extends Fragment implements View.OnClickListener {

    private String accountName = "", platform = "", season = "";
    private SharedPreferences mPrefs;
    SharedPreferences.Editor mEditor;
    private Button getNewStats, xbox, psn, pc, lifetime, seasonal;
    private EditText noActName;
    private Boolean checked = false, shadow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.search_stats, container, false);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            shadow = true;
        } else{ }

        mPrefs = getActivity().getSharedPreferences("label", 0); //Restored Values (act name)
        mEditor = mPrefs.edit();

        if(!mPrefs.getBoolean("firstTimeDes", false)) {
            AlertDialog DesDialog = new AlertDialog.Builder(this.getContext())
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

        //If statement for already there account ...
        accountName = mPrefs.getString("accountName", "");
        platform = mPrefs.getString("platform", "");
        //accountName = ""; platform = "";
        if(!accountName.isEmpty() && !platform.isEmpty())
        {
            //close this and open the other one#
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyStats()).commit();
        }
        
        
        //Create View Items
        xbox = (Button) view.findViewById(R.id.xbox);
        psn = (Button) view.findViewById(R.id.ps4);
        pc = (Button) view.findViewById(R.id.pc);
        getNewStats = (Button) view.findViewById(R.id.getStats);
        noActName = ((EditText) view.findViewById(R.id.searchName));

        seasonal = view.findViewById(R.id.seasonal);
        lifetime = view.findViewById(R.id.lifetime);

        view.findViewById(R.id.info).setVisibility(View.VISIBLE);




        //set season to lifetime as default
        mEditor.putString("season", "Lifetime").commit();


        if(shadow) {
            xbox.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            psn.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            pc.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
        }else{
            xbox.setTextColor(getResources().getColor(R.color.grey));
            psn.setTextColor(getResources().getColor(R.color.grey));
            pc.setTextColor(getResources().getColor(R.color.grey));
        }


        seasonal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                if(shadow) {
                    lifetime.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
                    seasonal.setShadowLayer(85, 0, 0, getResources().getColor(R.color.white));
                }
                else
                {
                    lifetime.setTextColor(getResources().getColor(R.color.grey));
                    seasonal.setTextColor(getResources().getColor(R.color.white));
                }
                mEditor.putString("season", "Season 9").commit();
            }
        });

        lifetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                if(shadow) {
                    lifetime.setShadowLayer(85, 0, 0, getResources().getColor(R.color.white));
                    seasonal.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
                }
                else
                {
                    seasonal.setTextColor(getResources().getColor(R.color.grey));
                    lifetime.setTextColor(getResources().getColor(R.color.white));
                }
                mEditor.putString("season", "Lifetime").commit();
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
                    try{new Toast(findMyStats.this.getActivity()).makeText(findMyStats.this.getActivity().getApplicationContext(), "Not Found, please check spelling and platform. For help visit Help tab", Toast.LENGTH_LONG).show();}catch(Exception ex){}
                    getNewStats.setText("GET STATS!");
                } //if something goes wrong output the error message
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("FAIL: " + error.getMessage()); //show error message on console if it fails
                try{new Toast(findMyStats.this.getActivity().getApplicationContext()).makeText(findMyStats.this.getActivity().getApplicationContext(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();}catch(Exception x){}
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
            findMyStats.this.getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyStats()).commit();
        }catch(NullPointerException e){try{new Toast(findMyStats.this.getActivity().getApplicationContext()).makeText(findMyStats.this.getActivity().getApplicationContext(), "Please Try Again Later", Toast.LENGTH_LONG).show();}catch(Exception x){} }
    }

}
