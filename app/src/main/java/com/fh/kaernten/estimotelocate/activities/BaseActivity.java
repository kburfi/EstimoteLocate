package com.fh.kaernten.estimotelocate.activities;

import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.fh.kaernten.estimotelocate.EstimoteApplication;


public abstract class BaseActivity extends AppCompatActivity {

    private boolean mInstanceStateSaved;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstanceStateSaved = false;
        ActionBar a = getSupportActionBar();
        if (a != null) {
            int title = defineTitle();
            if (title != -1) a.setTitle(title);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getEstimoteApplication().setCurrentActivity(this);
    }

    @Override
    protected void onPause() {
        clearReferences();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        clearReferences();
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        mInstanceStateSaved = true;
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onResumeFragments() {
        mInstanceStateSaved = false;
        super.onResumeFragments();
    }

    /**
     * @return true if activity did not call OnSaveInstanceState yet, was not destroyed and call is from UI thread
     */
    public boolean activityIsAlive() {
        if (!BaseActivity.this.isFinishing() && Looper.myLooper() == Looper.getMainLooper() && !mInstanceStateSaved) {
            FragmentManager manager = getSupportFragmentManager();
            return manager != null && !manager.isDestroyed();
        }
        return false;
    }

    public void showTextInToast(int textId) {
        if (activityIsAlive()) {
            Toast.makeText(this, textId, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * called when activity may update its view
     */
    public void updateCallback() {

    }

    private void clearReferences() {
        BaseActivity currActivity = getEstimoteApplication().getCurrentActivity();
        if (this.equals(currActivity)) {
            getEstimoteApplication().setCurrentActivity(null);
        }
    }

    protected boolean statusBarColorIsChangeable() {
        return android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public void setStatusBarColor(int color) {
        if (statusBarColorIsChangeable()) {
            getWindow().setStatusBarColor(
                    ContextCompat.getColor(BaseActivity.this, color));
        }
    }

    public abstract int defineTitle();

    public EstimoteApplication getEstimoteApplication() {
        return (EstimoteApplication) getApplication();
    }

    protected boolean shouldAskPermission() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);
    }
}
