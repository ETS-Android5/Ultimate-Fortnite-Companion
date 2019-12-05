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

public class NewSearchStats extends Fragment implements View.OnClickListener{

    private String accountName = "", platform = "";
    private TextView getStats, xbox, psn, pc, lifetime, seasonal;
    private EditText accountNameInput;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private boolean shadow = false;
    private Resources.Theme themes;
    TypedValue storedValueInTheme;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.search_stats_new, container, false);

        themes = getActivity().getTheme(); //needed for theme colors
        storedValueInTheme = new TypedValue(); //^


        //Create View Items
        xbox =  view.findViewById(R.id.xbox);
        psn = view.findViewById(R.id.ps4);
        pc = view.findViewById(R.id.pc);
        seasonal = view.findViewById(R.id.seasonal);
        lifetime = view.findViewById(R.id.lifetime);
        getStats = view.findViewById(R.id.getStats);
        accountNameInput = view.findViewById(R.id.searchName);

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


        getStats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(view);
                accountName = accountNameInput.getText().toString().trim();
                if(platform.isEmpty() || accountName.isEmpty()){
                    new Toast(NewSearchStats.this.getActivity().getApplicationContext()).makeText(NewSearchStats.this.getActivity().getApplicationContext(), "Please enter an Epic name and select a platform", Toast.LENGTH_LONG).show();
                }
                else {
                    mEditor.putString("searchPlatform", platform).commit();
                    mEditor.putString("searchName", accountName).commit();
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewMyStats()).commit();
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
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    seasonal.setTextColor(storedValueInTheme.data);
                seasonal.setTextSize(34);
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
                lifetime.setTextSize(34);
                seasonal.setTextColor(getResources().getColor(R.color.grey2));
                seasonal.setTextSize(28);
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
                xbox.setTextSize(34);
                platform = "xbl";
                break;
            case R.id.ps4:
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    psn.setTextColor(storedValueInTheme.data);
                psn.setTextSize(34);
                platform ="psn";
                break;
            case R.id.pc:
                if (themes.resolveAttribute(R.attr.colorAccent, storedValueInTheme, true))
                    pc.setTextColor(storedValueInTheme.data);
                pc.setTextSize(34);
                platform = "pc";
                break;
        }
    }

}
