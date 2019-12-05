package com.elitelabs.fortnitetracker;

import android.os.Parcel;
import android.os.Parcelable;

import static java.lang.Integer.parseInt;

/**
 * Created by DragXGaming on 3/19/2018.
 */

public class data {

    String gameType, KPM, score;
    String kills, matches;
    String winP, kd, scorePerMatch;
    String wins, seconds, thirds;

    data(String gt, String wins, String seconds, String thirds, String at, String k, String m, String s, String w, String kd){
        gameType = gt;
        KPM = at;
        kills = k;
        matches = m;
        score = s;
        winP = w;
        this.kd = kd;

        this.wins = wins;
        this.seconds = seconds;
        this.thirds = thirds;
    }

    data(){
        gameType = "";
        KPM = "";
        kills = "";
        matches = "";
        score = "";
        winP = "";
        this.kd = "";

        this.wins = "";
        this.seconds = "";
        this.thirds = "";
    }

    data(String gt){
        gameType = gt;
    }


    public void setWins(String wins) {
        if(wins.isEmpty())
            this.wins = "N/A";
        else
            this.wins = wins;
    }
    public String getWins(){
        return wins;
    }

    public void setSeconds(String seconds) {
        this.seconds = seconds;
    }
    public String getSeconds(){
        return seconds;
    }

    public void setThirds(String thirds) {
        this.thirds = thirds;
    }
    public String getThirds(){
        return thirds;
    }

    public void setKills(String kills) {
        this.kills = kills;
    }
    public String getKills(){
        return kills;
    }

    public String getGameType() {
        return gameType;
    }

    public void setMatches(String matches) {
        this.matches = matches;
    }
    public String getMatches() {
        return matches;
    }

    public void setKd(String kd) {
        this.kd = kd;
    }
    public String getKd() {
        return kd;
    }

    public void setScore(String score) {
        this.score = score;
    }
    public String getScore() {
        return "" + parseInt(score)/1000 + "k";
    }

    public void setWinP(String winP) {
        this.winP = winP;
    }
    public String getWinP() {
        return winP;
    }

    public void setKPM(String KPM) {
        this.KPM = KPM;
    }
    public String getKPM() {return KPM;}

    public void setScorePerMatch(int scorePerMatch) {this.scorePerMatch = "" + scorePerMatch;}
    public String getScorePerMatch() {
        return scorePerMatch;
    }

}
