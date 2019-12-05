package com.elitelabs.fortnitetracker;

import android.content.Context;
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

public class SearchStats extends Fragment implements View.OnClickListener{

    private String accountName = "", platform = "";
    private Button getStats, xbox, psn, pc, lifetime, seasonal;
    private EditText accountNameInput;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private boolean shadow = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_stats, container, false);

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            shadow = true;
        } else{ }

        //Create View Items
        xbox = (Button) view.findViewById(R.id.xbox);
        psn = (Button) view.findViewById(R.id.ps4);
        pc = (Button) view.findViewById(R.id.pc);
        seasonal = view.findViewById(R.id.seasonal);
        lifetime = view.findViewById(R.id.lifetime);
        getStats = (Button) view.findViewById(R.id.getStats);
        accountNameInput = ((EditText)view.findViewById(R.id.searchName));

        accountNameInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                accountNameInput.setCursorVisible(true);
            }
        });

        accountNameInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(i == keyEvent.KEYCODE_ENTER)
                {
                    //Get rid of enter after enter key is pressed (trim)
                    String temp = accountNameInput.getText().toString().trim();
                    accountNameInput.setText(temp);
                    hideKeyboard(view);
                }
                return false;
            }
        });

        mPrefs = this.getActivity().getSharedPreferences("label", 0); //Restored Values (act name)
        mEditor = mPrefs.edit();
        mEditor.putString("searchSeason", "Lifetime").commit();

        if(shadow) {
            xbox.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            psn.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
            pc.setShadowLayer(0, 0, 0, getResources().getColor(R.color.white));
        }else{
            xbox.setTextColor(getResources().getColor(R.color.grey));
            psn.setTextColor(getResources().getColor(R.color.grey));
            pc.setTextColor(getResources().getColor(R.color.grey));
        }

        getStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                accountName = accountNameInput.getText().toString().trim();
                if(platform.isEmpty() || accountName.isEmpty()){
                    new Toast(SearchStats.this.getActivity().getApplicationContext()).makeText(SearchStats.this.getActivity().getApplicationContext(), "Please enter an Epic name and select a platform", Toast.LENGTH_LONG).show();
                }
                else {
                    mEditor.putString("searchPlatform", platform).commit();
                    mEditor.putString("searchName", accountName).commit();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new MyStats()).commit();
                }
            }
        });

        //Change platforms - xbox
        xbox.setOnClickListener(this);
        //Change platforms - playstation
        psn.setOnClickListener(this);
        //Change platforms - pc
        pc.setOnClickListener(this);

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
                mEditor.putString("searchSeason", "Season 9").commit();
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
                mEditor.putString("searchSeason", "Lifetime").commit();
            }
        });


        return view;

    }

    public void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        accountNameInput.setCursorVisible(false);
    }


    @Override
    public void onClick(View view) {
        hideKeyboard(view);
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

}
