package kiwigroup.yodelego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import kiwigroup.yodelego.model.User;

public class SplashActivity extends BaseLoginActivity {

    private static final long SPLASH_SCREEN_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);

        SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
        if (sharedPref.contains("username") && sharedPref.contains("password")){
            String username = sharedPref.getString("username", "");
            String password = sharedPref.getString("password", "");
            startLoginProcess(username, password);
        } else {
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    goToWelcome();
                }
            }, SPLASH_SCREEN_DELAY);
        }
    }

    @Override
    protected void onLoginError(String errorMessage) {
        goToWelcome();
    }

    @Override
    protected void onLoginFieldError(String tag, String errorMessage) {
        goToWelcome();
    }

    @Override
    protected void onUserInfoError(String errorMessage) {
        goToWelcome();
    }

    @Override
    protected void onLoginSuccess(User user) {
        Intent mainIntent = new Intent().setClass(SplashActivity.this, MainActivity.class);
        mainIntent.putExtra("user", user);
        startActivity(mainIntent);
        finish();
    }

    private void goToWelcome(){
        Intent mainIntent = new Intent().setClass(SplashActivity.this, WelcomeActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
