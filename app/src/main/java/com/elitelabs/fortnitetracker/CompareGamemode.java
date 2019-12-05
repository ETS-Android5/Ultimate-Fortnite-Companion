package com.elitelabs.fortnitetracker;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.elitelabs.fortnitetracker.R;

/**
 * Created by DragXGaming on 3/28/2018.
 */

public class CompareGamemode extends Fragment{

    private data StatsP1 = new data(), StatsP2 = new data();
    private TextView wins, top2, top3, kills, type, kd, winP, matches, KPM, score, nameTop2P1, nameTop3P1;
    private TextView winsP2, top2P2, top3P2, killsP2, kdP2, winPP2, matchesP2, KPMP2, scoreP2, nameTop2P2, nameTop3P2;
     String NTop2, NTop3;
    private boolean shadow = false, NEW;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        NEW = getActivity().getSharedPreferences("label", 0).getBoolean("NewLayout", false);


        View view;

        if(NEW)
        {
            view = inflater.inflate(R.layout.compare_gamemode_new, container, false);
            System.out.println("NEW");
        } else {
            view = inflater.inflate(R.layout.compare_gamemode2, container, false);
            type = (TextView) view.findViewById(R.id.Type);
        }

        if (android.os.Build.VERSION.SDK_INT > Build.VERSION_CODES.M){
            shadow = true;
        }

        wins = (TextView) view.findViewById(R.id.Wins);
        top2 = (TextView) view.findViewById(R.id.Top2);
        top3 = (TextView) view.findViewById(R.id.Top3);
        kills = (TextView) view.findViewById(R.id.Kills);
        type = (TextView) view.findViewById(R.id.Type);
        kd = (TextView) view.findViewById(R.id.Kd);
        winP = (TextView) view.findViewById(R.id.WinP);
        matches = (TextView) view.findViewById(R.id.Matches);
        KPM = (TextView) view.findViewById(R.id.KPM);
        score = (TextView) view.findViewById(R.id.TotalScore);

        winsP2 = (TextView) view.findViewById(R.id.Winsp2);
        top2P2 = (TextView) view.findViewById(R.id.Top2p2);
        top3P2 = (TextView) view.findViewById(R.id.Top3p2);
        killsP2 = (TextView) view.findViewById(R.id.Killsp2);
        kdP2 = (TextView) view.findViewById(R.id.Kdp2);
        winPP2 = (TextView) view.findViewById(R.id.WinPp2);
        matchesP2 = (TextView) view.findViewById(R.id.Matchesp2);
        KPMP2 = (TextView) view.findViewById(R.id.KPMp2);
        scoreP2 = (TextView) view.findViewById(R.id.TotalScorep2);

        nameTop2P1 = (TextView) view.findViewById(R.id.top2);
        nameTop3P1 = (TextView) view.findViewById(R.id.top3);
        nameTop2P2 = (TextView) view.findViewById(R.id.top2p2);
        nameTop3P2 = (TextView) view.findViewById(R.id.top3p2);

        if(shadow && !NEW){
            wins.setShadowLayer(55,0,0,getResources().getColor(R.color.shadowColor));
            top2.setShadowLayer(55,0,0,getResources().getColor(R.color.shadowColor));
            top3.setShadowLayer(55,0,0,getResources().getColor(R.color.shadowColor));
            winsP2.setShadowLayer(55,0,0,getResources().getColor(R.color.shadowColor));
            top2P2.setShadowLayer(55,0,0,getResources().getColor(R.color.shadowColor));
            top3P2.setShadowLayer(55,0,0,getResources().getColor(R.color.shadowColor));
        }

        if(!StatsP1.getMatches().equals("") && !StatsP2.getMatches().equals("") && NEW)
            update();

        // Inflate the layout for this fragment
        return view;
    }

    public void setP1(data stats){
        this.StatsP1 = stats;
    }
    public void setP2(data stats){
        this.StatsP2 = stats;
    }

    public void update(){
        wins.setText("" + StatsP1.getWins());
        top2.setText("" + StatsP1.getSeconds());
        top3.setText("" + StatsP1.getThirds());
        kills.setText("" + StatsP1.getKills());
        if(!NEW) type.setText("" + StatsP1.getGameType());
        kd.setText("" + StatsP1.getKd());
        winP.setText("" + StatsP1.getWinP());
        if(!NEW) matches.setText("" + StatsP1.getMatches() + " matches");
        score.setText("" + StatsP1.getScore());
        KPM.setText("" + StatsP1.getKPM());

        winsP2.setText("" + StatsP2.getWins());
        top2P2.setText("" + StatsP2.getSeconds());
        top3P2.setText("" + StatsP2.getThirds());
        killsP2.setText("" + StatsP2.getKills());
        if(!NEW) type.setText("" + StatsP2.getGameType());
        kdP2.setText("" + StatsP2.getKd());
        winPP2.setText("" + StatsP2.getWinP());
        if(!NEW) matchesP2.setText("" + StatsP2.getMatches() + " matches");
        scoreP2.setText("" + StatsP2.getScore());
        KPMP2.setText("" + StatsP2.getKPM());

        nameTop2P1.setText(NTop2);
        nameTop2P2.setText(NTop2);

        nameTop3P2.setText(NTop3);
        nameTop3P1.setText(NTop3);

    }

    public void updateP1(String Ntop2, String Ntop3){
        wins.setText("" + StatsP1.getWins());
        top2.setText("" + StatsP1.getSeconds());
        top3.setText("" + StatsP1.getThirds());
        kills.setText("" + StatsP1.getKills());
        if(!NEW) type.setText("" + StatsP1.getGameType());
        kd.setText("" + StatsP1.getKd());
        winP.setText("" + StatsP1.getWinP());
        if(!NEW) matches.setText("" + StatsP1.getMatches() + " matches");
        score.setText("" + StatsP1.getScore());
        KPM.setText("" + StatsP1.getKPM());

        nameTop2P1.setText(Ntop2);
        nameTop3P1.setText(Ntop3);

    }

    public void updateP2(String Ntop2, String Ntop3){
        winsP2.setText("" + StatsP2.getWins());
        top2P2.setText("" + StatsP2.getSeconds());
        top3P2.setText("" + StatsP2.getThirds());
        killsP2.setText("" + StatsP2.getKills());
        if(!NEW) type.setText("" + StatsP2.getGameType());
        kdP2.setText("" + StatsP2.getKd());
        winPP2.setText("" + StatsP2.getWinP());
        if(!NEW) matchesP2.setText("" + StatsP2.getMatches() + " matches");
        scoreP2.setText("" + StatsP2.getScore());
        KPMP2.setText("" + StatsP2.getKPM());

        nameTop2P2.setText(Ntop2);
        nameTop3P2.setText(Ntop3);

    }


}
