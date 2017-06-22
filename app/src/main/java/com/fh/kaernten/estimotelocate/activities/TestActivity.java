package com.fh.kaernten.estimotelocate.activities;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.alarm.AlarmBroadcastReceiver;
import com.fh.kaernten.estimotelocate.helper.DateHelper;
import com.fh.kaernten.estimotelocate.helper.IntentHelper;
import com.fh.kaernten.estimotelocate.interfaces.MobileTest;
import com.fh.kaernten.estimotelocate.objects.TestResult;
import com.fh.kaernten.estimotelocate.objects.TestResultDao;
import com.fh.kaernten.estimotelocate.testcases.AccuracyTest2D;
import com.fh.kaernten.estimotelocate.testcases.AccuracyTest3D;
import com.fh.kaernten.estimotelocate.testcases.AlwaysRangingTest;
import com.fh.kaernten.estimotelocate.testcases.BatteryRuntimeTest;
import com.fh.kaernten.estimotelocate.testcases.MovementMonitoringTest;

/**
 * Testing is done as instrumentation tests. The device must be disconnected in order to log battery
 * level. Hence automated instrumented tests are not sufficient.
 */
public class TestActivity extends BaseActivity {

    private TestResultDao testResultDao;

    private PendingIntent alarmPendingIntent;
    private AlarmManager alarmManager;
    private AppCompatTextView outputTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        testResultDao = getEstimoteApplication().getDaoSession().getTestResultDao();
        initializeView();
    }

    @Override
    protected void onResume() {
        updateResultTextView();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        stopTest();
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.test_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.abortTest) {
            stopTest();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public int defineTitle() {
        return R.string.test_options;
    }

    @Override
    public void updateCallback() {
        if (activityIsAlive()) {
            updateResultTextView();
        }
    }

    private void initializeView() {
        setContentView(R.layout.test_activity);
        outputTextView = (AppCompatTextView) findViewById(R.id.testOutput);
        initializeTest(new BatteryRuntimeTest(), R.id.testBattery);
        initializeTest(new AccuracyTest2D(), R.id.testAccuracy2D);
        initializeTest(new AccuracyTest3D(), R.id.testAccuracy3D);
        initializeTest(new MovementMonitoringTest(), R.id.testMovementMonitoring);
        initializeTest(new AlwaysRangingTest(), R.id.testRanging);
    }

    private void updateResultTextView() {
        if (getEstimoteApplication().getRunningTest() != null) {
            TestResult last = getLastTestResult();
            if (last != null) {
                outputTextView.setText("Last result from " + DateHelper.format(this, last.getTime()) +
                        "\nBattery level was " + last.getBatteryLevel());
            } else {
                outputTextView.setText(R.string.test_running);
            }
        } else {
            outputTextView.setVisibility(View.INVISIBLE);
        }
    }

    private void initializeTest(MobileTest test, int twoButtonContainer) {
        LinearLayout layout = (LinearLayout) findViewById(twoButtonContainer);
        Button testButton = (AppCompatButton) layout.findViewById(R.id.left);
        testButton.setText(test.getType().getName(this));
        testButton.setOnClickListener(view -> createTest(test));
        Button resultButton = (AppCompatButton) layout.findViewById(R.id.right);
        resultButton.setOnClickListener(view -> showTestResult(test));
    }

    private void createTest(MobileTest test) {
        getEstimoteApplication().setRunningTest(test);
        Intent intent = IntentHelper.buildIntent(this, AlarmBroadcastReceiver.class);
        alarmPendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        getEstimoteApplication().getRunningTest()
                .startTest(this, alarmManager, alarmPendingIntent);
        outputTextView.setVisibility(View.VISIBLE);
    }

    private TestResult getLastTestResult() {
        return testResultDao.queryBuilder()
                .where(TestResultDao.Properties.TestType.eq(
                        getEstimoteApplication().getRunningTest().getType().getDbId()))
                .orderDesc(TestResultDao.Properties.Time).limit(1).build().unique();
    }

    private void showTestResult(MobileTest test) {
        getEstimoteApplication().setRunningTest(test);
        IntentHelper.startActivity(this, ListTestResultsActivity.class);
    }

    private void stopTest() {
        if (getEstimoteApplication().getRunningTest() != null) {
            getEstimoteApplication().getRunningTest().stopTest(this);
            getEstimoteApplication().setRunningTest(null);
        }
        if (alarmManager != null && alarmPendingIntent != null) {
            alarmManager.cancel(alarmPendingIntent);
        }
        outputTextView.setText(R.string.test_running);
        outputTextView.setVisibility(View.INVISIBLE);
    }
}