package  com.challenge.swaqny.challenge.ui.gameplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.challenge.swaqny.challenge.R;
import com.challenge.swaqny.challenge.adapters.WordSearchPagerAdapter;
import com.challenge.swaqny.challenge.framework.WordSearchManager;
import com.challenge.swaqny.challenge.models.GameState;
import com.challenge.swaqny.challenge.ui.ResultsActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;

import java.util.ArrayList;

public class WordSearchActivity extends Activity implements WordSearchGridView.WordFoundListener, PauseDialogFragment.PauseDialogListener, View.OnClickListener,RewardedVideoAdListener {

    private final static boolean ON_SKIP_HIGHLIGHT_WORD = true;
    private final static long ON_SKIP_HIGHLIGHT_WORD_DELAY_IN_MS = 500;

    private final static int TIMER_GRANULARITY_IN_MS = 50;

    /**
     * Current number of grid views that have been instantiated
     * Actual fragment position is currentItem - 2
     */
    public static int currentItem;
    private final PauseDialogFragment mPauseDialogFragment = new PauseDialogFragment();
    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    private TextView mTimerTextView;
    private TextView mScoreTextView;
    private CountDownTimer mCountDownTimer;
    WordSearchFragment wordSearchFragment ;
   // number of leveles


 ArrayList score ;
     // updat time  for evey level

    int timMin  ;
    TextView numberOfquetion ,countSum ,countLevel;

    private String mGameState;
    private long mTimeRemaining;
    private long mRoundTime,timeAfter;
    private int mScore;
    private int mSkipped;
    private WordSearchPagerAdapter mWordSearchPagerAdapter;
    private  int numberQution ,levelInputQution  ,NUMERoflevel;
    Bundle bundle ;
    String level;
    int sum;
    private AdView mAdView;
     private RewardedVideoAd mAd;

    SharedPreferences sharedPreferences ;
     public  SharedPreferences.Editor editor ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bundle =getIntent().getExtras();
        numberQution=1;
        NUMERoflevel=bundle.getInt("numerOflevels");
        level= bundle.getString("level");
         sharedPreferences =  getSharedPreferences(level, Context.MODE_PRIVATE);



        editor= sharedPreferences.edit();
         int timeNumber =5;
        score =new ArrayList();
//        categoryId = R.string.ga_gameplay_screen;
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.wordsearch_activity);
        mGameState = GameState.START;
        findViewById(R.id.bSkip).setOnClickListener(this);
        findViewById(R.id.bPause).setOnClickListener(this);
        numberOfquetion =(TextView)findViewById(R.id.numberOfquetion);
        countSum=(TextView)findViewById(R.id.countSum);
        countLevel=(TextView)findViewById(R.id.countLevel);
        countLevel.setText(Integer.toString(NUMERoflevel)+"");
        mTimerTextView = (TextView) findViewById(R.id.tvTimer);
        mScoreTextView = (TextView) findViewById(R.id.tvScore);
        mScoreTextView.setText("0");
        currentItem = 0;
        mScore = 0;
        mSkipped = 0;




        mWordSearchPagerAdapter = new WordSearchPagerAdapter(getFragmentManager(), getApplicationContext());


        // Set up the ViewPager with the sections adapter.
        mViewPager = (WordSearchViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mWordSearchPagerAdapter);

        mRoundTime = WordSearchManager.getInstance().getGameMode().getTime();


          int qutionmuberLevel = bundle.getInt("qutionNumper");
        if (qutionmuberLevel!=0){
            levelInputQution= qutionmuberLevel;
            numberOfquetion.setText(Integer.toString(levelInputQution));

        }else {
            numberOfquetion.setText("test");

        }

        int numerOfLevel = bundle.getInt("numerOflevels");
        if(numerOfLevel!=0){
             NUMERoflevel=numerOfLevel ;
            countLevel.setText(Integer.toString(NUMERoflevel)+" ");

         }
         Long timeAfterup =bundle.getLong("timeAfter");
        if(timeAfterup!=0){
            mTimeRemaining=timeAfterup;
            setupCountDownTimer(mTimeRemaining);
             startCountDownTimer();

        }else {
            mTimeRemaining = mRoundTime;
            setupCountDownTimer(mTimeRemaining);
             startCountDownTimer();

        }

        // Use an activity context to get the rewarded video instance.

//     if(NUMERoflevel>5&&NUMERoflevel<10){


//     }
//     if (NUMERoflevel%5==0){
//         loadRewardedVideoAd();
//     }


   //*********************************** AdMode*****************************************************


       // MobileAds.initialize(this, "YOUR_ADMOB_APP_ID");
//        mAdView = (AdView) findViewById(R.id.adView);
//        mAdView.setAdUnitId("ca-app-pub-1858974607441283/4821516577");
//        AdRequest adRequest = new AdRequest.Builder().build();
//        mAdView.loadAd(adRequest);

        // Load an ad into the AdMob banner view.
        AdView adView = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .setRequestAgent("android_studio:ad_template").build();
                AdRequest adRequest = new AdRequest.Builder().build();

        adView.loadAd(adRequest);
 //



    }






    private void pauseGameplay() {
        if (mGameState.equals(GameState.PAUSE))
            return;
        mGameState = GameState.PAUSE;
        stopCountDownTimer();
        mPauseDialogFragment.show(getFragmentManager(), "dialog");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bSkip:
//                analyticsTrackEvent(R.string.ga_click_skip);
                if (ON_SKIP_HIGHLIGHT_WORD) {
                    ((WordSearchFragment) mWordSearchPagerAdapter.getFragmentFromCurrentItem(currentItem)).highlightWord();
                    (new CountDownTimer(ON_SKIP_HIGHLIGHT_WORD_DELAY_IN_MS, TIMER_GRANULARITY_IN_MS) {

                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            mViewPager.setCurrentItem(currentItem);
                            mSkipped++;
                        }
                    }).start();
                } else {
                    mViewPager.setCurrentItem(currentItem);
                    mSkipped++;
                }


                break;
            case R.id.bPause:
//                analyticsTrackEvent(R.string.ga_click_pause);
                pauseGameplay();
                break;
        }
    }

    @Override
    public void notifyWordFound() {
        mViewPager.setCurrentItem(currentItem);
        mScoreTextView.setText(Integer.toString(++mScore));
          sum =numberQution++;
        countSum.setText(Integer.toString(sum));


        if (levelInputQution <numberQution){
            mCountDownTimer.cancel();
            stopCountDownTimer();

             AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
            // ...Irrelevant code for customizing the buttons and title
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alert_gialoge, null);
             dialogBuilder.setView(dialogView);
             final TextView score =(TextView)dialogView.findViewById(R.id.score);
            score.setText(mScoreTextView.getText());
            final String scoreView = mScoreTextView.getText().toString();
             Button nextlevel = ( Button) dialogView.findViewById(R.id.nextlevel);
            final TextView show =(TextView)dialogView.findViewById(R.id.show);
            show.setText(Integer.toString(NUMERoflevel));
            final String countSumM= countSum.getText().toString();

            nextlevel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {






                    Intent i = new Intent(WordSearchActivity.this,  Suplevel.class);
                    editor.putString(Integer.toString(NUMERoflevel), mScoreTextView.getText().toString()  );
                    editor.commit();
                    i.putExtra("numerOflevels", NUMERoflevel++ );
                    i.putExtra("scoreView",scoreView  );
                    i.putExtra("level",level  );
                   if (scoreView.equals(countSumM)) {

                       i.putExtra("checkPss",true  );
                    }else {
                       i.putExtra("checkPss", false  );
                   }

//
//
                    startActivity(i);
             finish();







                }
            });

             AlertDialog alertDialog = dialogBuilder.create();
            alertDialog.show();
        }

    }

    @Override
    public void onDialogQuit() {
        finish();
    }

    @Override
    public void onDialogResume() {
        mGameState = GameState.PLAY;
        setupCountDownTimer(mTimeRemaining);
        startCountDownTimer();
//        setFullscreen();
    }

    @Override
    public void onDialogRestart() {
        mGameState = GameState.PLAY;
        restart();
    }

    private void restart() {
        mScore = 0;
        mSkipped = 0;
        mTimeRemaining = mRoundTime;
        setupCountDownTimer(mTimeRemaining);
        startCountDownTimer();
//        setFullscreen();
        mScoreTextView.setText("0");
        mViewPager.setCurrentItem(currentItem);
    }



    @Override
    protected void onResume() {
        super.onResume();
//        analyticsTrackScreen(getString(categoryId));
        if (mGameState.equals(GameState.START) || mGameState.equals(GameState.FINISHED))
            mGameState = GameState.PLAY;
        else
            pauseGameplay();

        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopCountDownTimer();
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        pauseGameplay();
    }

    private void setupCountDownTimer(final long timeinMS) {
        mCountDownTimer = new CountDownTimer(timeinMS, TIMER_GRANULARITY_IN_MS) {


            public void onTick(long millisUntilFinished) {
                mTimeRemaining = millisUntilFinished;
                long secondsRemaining = mTimeRemaining / 1000 + 1;
                mTimerTextView.setText(Long.toString(secondsRemaining));
                if (secondsRemaining <= 10) {
                    mTimerTextView.setTextColor(getResources().getColor(R.color.red));
                } else {
                    mTimerTextView.setTextColor(Color.BLACK);

                }
            }

            public void onFinish() {
                mGameState = GameState.FINISHED;
                mTimerTextView.setText("0");


                ((WordSearchFragment) mWordSearchPagerAdapter.getFragmentFromCurrentItem(currentItem)).highlightWord();
                (new CountDownTimer(ON_SKIP_HIGHLIGHT_WORD_DELAY_IN_MS, TIMER_GRANULARITY_IN_MS) {

                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {

                        mAd = MobileAds.getRewardedVideoAdInstance( getApplicationContext());
                        mAd.setRewardedVideoAdListener(WordSearchActivity.this);
                        if (mAd.isLoaded()) {
                            mAd.show();
                        }
                        mAd.loadAd("ca-app-pub-1858974607441283/7740513326", new AdRequest.Builder().build());


                        Intent i = new Intent(getApplicationContext(), ResultsActivity.class);
                        i.putExtra("score", mScore);
                        i.putExtra("skipped", mSkipped);
//                        try {
//                            i.wait(3000);
//                        } catch (InterruptedException e) {
//                            e.printStackTrace();
//                        }

                            startActivity(i);
                            finish();


                    }
                }).start();
//


            }
        };
    }

    private void startCountDownTimer() {
        if (mCountDownTimer != null)
            mCountDownTimer.start();
    }

    private void stopCountDownTimer() {
        if (mCountDownTimer != null)
            mCountDownTimer.cancel();
    }

    public long getTimeRemaining() {
        return mTimeRemaining;
    }

    public int getScore() {
        return mScore;
    }

    @Override
    public void onDestroy() {
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onRewardedVideoAdLoaded() {
        if (mAd.isLoaded())
        {
            mAd.show();
        }

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }
}
