package com.alphaford.pimapplication;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreenActivity extends AppCompatActivity {
    private static final int SPLASH_DELAY = 3000;

    private final Handler mHandler   = new Handler();
    private final Launcher mLauncher = new Launcher();
    @Override
    protected void onStart() {
        super.onStart();

        mHandler.postDelayed(mLauncher, SPLASH_DELAY);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        startActivity(new Intent(this, LoginActivity.class));
        finish();

    }
    @Override
    protected void onStop() {
        mHandler.removeCallbacks(mLauncher);
        super.onStop();
    }
    private void launch() {
        if (!isFinishing()) {
            startActivity(new Intent(this, LoginActivity.class));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }
    private class Launcher implements Runnable {
        @Override
        public void run() {
            launch();
        }
    }
}
