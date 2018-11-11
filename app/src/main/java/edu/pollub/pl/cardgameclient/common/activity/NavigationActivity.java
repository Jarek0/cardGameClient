package edu.pollub.pl.cardgameclient.common.activity;

import android.app.Activity;
import android.content.Intent;

import roboguice.activity.RoboActivity;

public abstract class NavigationActivity extends RoboActivity {

    public void goTo(Class<? extends Activity> activityToGo) {
        Intent intent = new Intent(this, activityToGo);
        startActivityForResult(intent, 0);
    }

    public void comeBack() {
        Intent intent = new Intent();
        setResult(0, intent);
        finish();
    }

}
