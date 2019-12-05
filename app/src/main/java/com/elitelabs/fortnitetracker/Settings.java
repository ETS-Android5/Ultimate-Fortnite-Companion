package com.elitelabs.fortnitetracker;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.elitelabs.fortnitetracker.R;

public class Settings extends android.support.v4.app.Fragment implements View.OnClickListener{

    private EditText name;
    private Button xbox, psn, pc, lifetime, season, legacy;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private RadioGroup newLayoutGroup;
    private RadioButton blue,red,purple;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings, container, false);

        mPrefs = getActivity().getSharedPreferences("label", 0);
        mEditor = mPrefs.edit();

        xbox = (Button) view.findViewById(R.id.xbox);
        psn = (Button) view.findViewById(R.id.psn);
        pc = (Button) view.findViewById(R.id.pc);
        lifetime = view.findViewById(R.id.lifetime);
        season = view.findViewById(R.id.seasonal);
        legacy = view.findViewById(R.id.Legacy);
        newLayoutGroup = view.findViewById(R.id.radio);
        blue = view.findViewById(R.id.blue);
        red = view.findViewById(R.id.red);
        purple = view.findViewById(R.id.purple);


        red.setOnClickListener(this);
        blue.setOnClickListener(this);
        purple.setOnClickListener(this);


        String platform = mPrefs.getString("platform", "");
        switch (platform) {
            case "xbl":
                xbox.setTextColor(getResources().getColor(R.color.white));
                break;
            case "psn":
                psn.setTextColor(getResources().getColor(R.color.white));
                break;
            case "pc":
                pc.setTextColor(getResources().getColor(R.color.white));
                break;
            default:
                break;
        }

        String statSeason = mPrefs.getString("season", "");
        if(statSeason.equals("Lifetime"))
            lifetime.setTextColor(getResources().getColor(R.color.white));
        else
            season.setTextColor(getResources().getColor(R.color.white));

        Boolean newLayoutDesign = mPrefs.getBoolean("NewLayout", false);
        if(newLayoutDesign)
        {
            switch(mPrefs.getString("themeColor", "")){
                case "blue":
                    blue.setChecked(true);
                    break;
                case "red":
                    red.setChecked(true);
                    break;
                case "purple":
                    purple.setChecked(true);
                    break;
                default:
                    purple.setChecked(true);
                    break;
            }
        }
        else
            legacy.setTextColor(getResources().getColor(R.color.white));


        xbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                psn.setTextColor(getResources().getColor(R.color.darkblue));
                pc.setTextColor(getResources().getColor(R.color.darkblue));
                mEditor.putString("platform", "xbl").commit();
                xbox.setTextColor(getResources().getColor(R.color.white));
            }
        });
        psn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                xbox.setTextColor(getResources().getColor(R.color.darkblue));
                pc.setTextColor(getResources().getColor(R.color.darkblue));
                mEditor.putString("platform", "psn").commit();
                psn.setTextColor(getResources().getColor(R.color.white));
            }
        });
        pc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                psn.setTextColor(getResources().getColor(R.color.darkblue));
                xbox.setTextColor(getResources().getColor(R.color.darkblue));
                mEditor.putString("platform", "pc").commit();
                pc.setTextColor(getResources().getColor(R.color.white));
            }
        });

        lifetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                season.setTextColor(getResources().getColor(R.color.darkblue));
                lifetime.setTextColor(getResources().getColor(R.color.white));
                mEditor.putString("season", "Lifetime").commit();
            }
        });
        season.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lifetime.setTextColor(getResources().getColor(R.color.darkblue));
                season.setTextColor(getResources().getColor(R.color.white));
                mEditor.putString("season", "Season 9").commit();
            }
        });


        legacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //newLayout.setTextColor(getResources().getColor(R.color.darkblue));
                legacy.setTextColor(getResources().getColor(R.color.white));
                mEditor.putBoolean("NewLayout", false).commit();

                //Restart app to update layout
                Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
            }
        });


        name = (EditText) view.findViewById(R.id.epicname);
        name.setHint(mPrefs.getString("accountName", ""));

        name.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_ENTER) {
                    String temp = name.getText().toString().trim();
                    mEditor.putString("accountName", temp).commit();
                    name.setCursorVisible(false);
                    name.setSelection(0);
                    name.setText(temp.trim());
                    //Hide Keyboard after enter
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return false;
            }
        });

        view.findViewById(R.id.policy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://docs.google.com/document/u/3/d/e/2PACX-1vR0l8Dadrcmg2CiFtuu9o4I6xJ5YhxrbeKzdEHTG5bYyHk1lpoCY-zGXPHiAu32ATyZGirjXX50prcQ/pub")));
            }
        });

        return view;

    }

    @Override
    public void onClick(View view) {
        switch(newLayoutGroup.getCheckedRadioButtonId()){
            case R.id.blue:
                mEditor.putString("themeColor", "blue").commit();
                break;
            case R.id.red:
                mEditor.putString("themeColor", "red").commit();
                break;
            case R.id.purple:
                mEditor.putString("themeColor", "purple").commit();
                break;
            default:
                mEditor.putString("themeColor", "purple").commit();
                break;
        }
        legacy.setTextColor(getResources().getColor(R.color.darkblue));
        mEditor.putBoolean("NewLayout", true).commit();
        //Restart app to update layout
        Intent i = getActivity().getBaseContext().getPackageManager().getLaunchIntentForPackage( getActivity().getBaseContext().getPackageName() );
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }


}
