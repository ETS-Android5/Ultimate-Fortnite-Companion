package com.elitelabs.fortnitetracker;


import android.os.Build;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elitelabs.fortnitetracker.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class gamemode extends android.support.v4.app.Fragment {


    private data myStats = new data();
    private TextView wins, top2, top3, kills, type, kd, winP, matches, KPM, score, avgScore, nameTop2, nameTop3;
    private boolean shadow =false;
    String nTop2, nTop3;
    private boolean NEW = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        NEW = getActivity().getSharedPreferences("label", 0).getBoolean("NewLayout", false);

        View view;

        if(NEW)
        {
            view = inflater.inflate(R.layout.gamemode_new, container, false);
            System.out.println("NEW");
        } else {
            view = inflater.inflate(R.layout.gamemode, container, false);
            type = (TextView) view.findViewById(R.id.Type);
        }

        if (android.os.Build.VERSION.SDK_INT > 23){
            shadow = true;
        }

        wins = (TextView) view.findViewById(R.id.Wins);
        top2 = (TextView) view.findViewById(R.id.Top2);
        top3 = (TextView) view.findViewById(R.id.Top3);
        kills = (TextView) view.findViewById(R.id.Kills);
        kd = (TextView) view.findViewById(R.id.Kd);
        winP = (TextView) view.findViewById(R.id.WinP);
        matches = (TextView) view.findViewById(R.id.Matches);
        KPM = (TextView) view.findViewById(R.id.KPM);
        score = (TextView) view.findViewById(R.id.TotalScore);
        avgScore = (TextView) view.findViewById(R.id.AvgScore);

        nameTop2 = (TextView) view.findViewById(R.id.top2);
        nameTop3 = (TextView) view.findViewById(R.id.top3);

        if(shadow && !NEW)
        {
            wins.setShadowLayer(90,0,0,getResources().getColor(R.color.shadowColor));
            top2.setShadowLayer(90,0,0,getResources().getColor(R.color.shadowColor));
            top3.setShadowLayer(90,0,0,getResources().getColor(R.color.shadowColor));
        }

        if(!myStats.getMatches().equals("") && NEW)
            update();

        // Inflate the layout for this fragment
        return view;
    }

    public void setInfo(data myStats){
        this.myStats = myStats;
    }

    public void update(String Ntop2, String Ntop3){
        //Change text size if number is too big. NEED TO CHECK IF IT WORKS DID NOT CHECK IT YET
        if(NEW && myStats.getWins().length() > 2)
            wins.setTextSize(30);
        else if (NEW)
            wins.setTextSize(36);

        wins.setText("" + myStats.getWins());
        top2.setText("" + myStats.getSeconds());
        top3.setText("" + myStats.getThirds());
        kills.setText("" + myStats.getKills());
        type.setText("" + myStats.getGameType());
        kd.setText("" + myStats.getKd());
        winP.setText("" + myStats.getWinP());
        matches.setText("" + myStats.getMatches() + " matches");
        score.setText("" + myStats.getScore());
        avgScore.setText("" + myStats.getScorePerMatch());
        KPM.setText("" + myStats.getKPM());

        nameTop2.setText(Ntop2);
        nameTop3.setText(Ntop3);

    }

    public void update(){
        wins.setText("" + myStats.getWins());
        top2.setText("" + myStats.getSeconds());
        top3.setText("" + myStats.getThirds());
        kills.setText("" + myStats.getKills());
        kd.setText("" + myStats.getKd());
        winP.setText("" + myStats.getWinP());
        matches.setText("" + myStats.getMatches() + " matches");
        score.setText("" + myStats.getScore());
        avgScore.setText("" + myStats.getScorePerMatch());
        KPM.setText("" + myStats.getKPM());

    }

    public void setTops(String top2, String top3){
        nameTop2.setText(top2);
        nameTop3.setText(top3);
    }



}
