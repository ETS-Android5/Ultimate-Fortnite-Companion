package com.elitelabs.fortnitetracker;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;


public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawer;
    private NewMyStats newMyStats;
    private NewSearchStats newSearchStats;
    private NewCompareStats newCompareStats;

    private MyStats myStats;
    private CompareStats compareStats;
    private SearchStats searchStats;
    private Settings settings;
    private ItemShop itemshop;
    private AlertDialog.Builder HelpDialog, Dialog;
    private ViewPager viewPager;
    private AdView mAdView;
    private boolean NewLayout = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        NewLayout = getSharedPreferences("label", 0).getBoolean("NewLayout", false);
        if(NewLayout){
            switch(getSharedPreferences("label", 0).getString("themeColor", "")){
                case "blue":
                    setTheme(R.style.AppThemeNewBlue);
                    break;
                case "red":
                    setTheme(R.style.AppThemeNewRed);
                    break;
                case "purple":
                    setTheme(R.style.AppThemeNewPurple);
                    break;
                default:
                    setTheme(R.style.AppThemeNewPurple);
                    break;
            }
        }
        else
            setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


       // System.out.println(        " TEST 123" + getSharedPreferences("label", 0).getBoolean("TEST", true));

        NewLayout = getSharedPreferences("label", 0).getBoolean("NewLayout", false);
        if(NewLayout){
            updateNewLayout();
        }


        //Initialize ads
        MobileAds.initialize(this, "ca-app-pub-7215660139227978~9728959987");

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("457C10F5B87B7594C213A91C614F7201").build();
        mAdView.loadAd(adRequest);


        HelpDialog = new AlertDialog.Builder(this)
                .setTitle("Help")
                .setMessage("Can't find your stats?\n\nFor consoles (PS4, Xbox) you need to connect your console account to an Epic Games account. \n\n To create a linked account:\n" +
                                "- Go to www.epicgames.com/fortnite and click the login icon \n - Instead of clicking on EPIC GAMES login you must click on either PLAYSTATION or XBOX\n" +
                                "- Follow the steps to create an account\n" +
                                "- You now have an epic games account linked to your console!\n\n" + "If you need any more help, tap the Feedback tab and send us an email"  )
                .setNegativeButton("Leave", new DialogInterface.OnClickListener() { @Override public void onClick(DialogInterface dialog, int which) { }
                });


        //Start app on the MyStats page
        if(NewLayout)
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new NewFindMyStats()).commit();
        else
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new findMyStats()).commit();


        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        NavigationView navView = findViewById(R.id.nav_view);

        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                NewLayout = getSharedPreferences("label", 0).getBoolean("NewLayout", false);

               AdRequest adRequest = new AdRequest.Builder().addTestDevice("457C10F5B87B7594C213A91C614F7201").build();

                switch (item.getItemId()){
                    case R.id.MyStats:
                        mAdView.loadAd(adRequest);
                        if(!NewLayout){
                            myStats = new MyStats();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, myStats).commit();
                        } else {
                            newMyStats = new NewMyStats();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newMyStats).commit();
                        }
                        break;
                    case R.id.SearchStats:
                        if(!NewLayout) {
                            searchStats = new SearchStats();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, searchStats).commit();
                        }else{
                            newSearchStats = new NewSearchStats();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newSearchStats).commit();
                        }
                        break;
                    case R.id.Compare:
                        if(!NewLayout) {
                            compareStats = new CompareStats();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, compareStats).commit();
                        }else{
                            newCompareStats= new NewCompareStats();
                            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, newCompareStats).commit();
                        }
                        break;
                    case R.id.Settings:
                        settings = new Settings();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, settings).commit();
                        break;
                    case R.id.ItemShop:
                        mAdView.loadAd(adRequest);

                        itemshop = new ItemShop();
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, itemshop).commit();
                        break;
                    case R.id.Help:
                        HelpDialog.show();
                        break;
                    case R.id.Feedback:
                        Intent intent = new Intent(Intent.ACTION_SENDTO);intent.setData(Uri.parse("mailto:"));
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "EliteLabsDev@yahoo.com" });
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Fortnite Feedback");
                        intent.putExtra(Intent.EXTRA_TEXT, "Please type your feedback below. We will reply withing 24 hours! To leave a review, visit the google play store (" + BuildConfig.VERSION_NAME + ")");
                        startActivity(Intent.createChooser(intent, "Send email..."));
                        break;
                    case R.id.Share:
                        Intent intent1 = new Intent(Intent.ACTION_SEND);
                        intent1.setType("*/*");
                        //intent1.putExtra(Intent.EXTRA_SUBJECT, "Check out this Fortnite Companion App!");
                        intent1.putExtra(Intent.EXTRA_TEXT, "Check out this Fortnite Companion App!\n https://play.google.com/store/apps/details?id=com.elitelabs.fortnitetracker");
                        startActivity(Intent.createChooser(intent1, "Share..."));
                        break;
                    case R.id.Rate:
                        Intent intent2 = new Intent(Intent.ACTION_VIEW);
                        intent2.setData(Uri.parse("market://details?id=com.elitelabs.fortnitetracker"));
                        try { startActivity(intent2); } catch (ActivityNotFoundException e)
                        {
                            intent2.setData(Uri.parse("https://play.google.com/store/apps/details?id=com.elitelabs.fortnitetracker"));
                        }
                        break;
                }
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        if(drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }


    //Make changes to to layout - make it the new layout
    public void updateNewLayout(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        //Change toolbar to theme color
        Resources.Theme themes = getTheme();
        TypedValue storedValueInTheme = new TypedValue();
        if (themes.resolveAttribute(R.attr.colorPrimary, storedValueInTheme, true)) {
            toolbar.setBackgroundColor(storedValueInTheme.data);
        }

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        findViewById(R.id.adView).setBackgroundColor(getResources().getColor(R.color.offwhite));

        NavigationView nav = findViewById(R.id.nav_view);
        nav.getMenu().getItem(0).setIcon(R.drawable.my_stats_icon);
        nav.getMenu().getItem(0).setChecked(true); //make MyStats checked by default on startup
        nav.getMenu().getItem(1).setIcon(R.drawable.search_icon);
        nav.getMenu().getItem(2).setIcon(R.drawable.compare_icon);
        nav.getMenu().getItem(3).setIcon(R.drawable.item_shop_icon);
        nav.getMenu().getItem(4).setIcon(R.drawable.settings_icon);

        nav.setBackgroundColor(getResources().getColor(R.color.darkGrey));
        nav.setItemBackground(getDrawable(R.drawable.menustyle_new));
    }



}
