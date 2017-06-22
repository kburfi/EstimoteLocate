package com.fh.kaernten.estimotelocate.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.AppCompatTextView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.adapter.TestResultAdapter;
import com.fh.kaernten.estimotelocate.helper.NumberHelper;
import com.fh.kaernten.estimotelocate.interfaces.ProgressBarCallback;
import com.fh.kaernten.estimotelocate.helper.WriteToCsvTask;
import com.fh.kaernten.estimotelocate.interfaces.MobileTest;
import com.fh.kaernten.estimotelocate.objects.TestResult;
import com.fh.kaernten.estimotelocate.objects.TestResultDao;

import org.greenrobot.greendao.query.LazyList;

public class ListTestResultsActivity extends BaseActivity implements ProgressBarCallback {

    private static final int PERMISSION_WRITE_REQUEST_CODE = 200;

    private ListView list;
    private AppCompatTextView outputTV;
    private ProgressBar progressBar;

    private MobileTest test;
    private LazyList<TestResult> testResults;

    private TestResultDao testResultDao;
    private WriteToCsvTask csvAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        test = getEstimoteApplication().getRunningTest();
        if (test != null) {
            testResultDao = getEstimoteApplication().getDaoSession().getTestResultDao();
            testResults = testResultDao.queryBuilder()
                    .where(TestResultDao.Properties.TestType.eq(test.getType().getDbId()))
                    .orderAsc(TestResultDao.Properties.Time).build().listLazy();
//            TestResult last = getEstimoteApplication().getDaoSession().getTestResultDao().queryBuilder()
//                    .orderDesc(TestResultDao.Properties.Id).limit(1).unique();
        }
        initializeView();
    }

    @Override
    protected void onDestroy() {
        if (testResults != null) {
            testResults.close();
        }
        if (csvAsyncTask != null) {
            csvAsyncTask.cancel(true);
        }
        super.onDestroy();
    }

    @Override
    public int defineTitle() {
        return R.string.show_result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater i = getMenuInflater();
        i.inflate(R.menu.test_result_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean exportPossible = csvAsyncTask == null && testResults != null && !testResults.isEmpty();
        menu.findItem(R.id.export).setVisible(exportPossible);
        menu.findItem(R.id.abort).setVisible(csvAsyncTask != null);
        menu.findItem(R.id.delete).setVisible(csvAsyncTask == null && testResults!= null && !testResults.isEmpty());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.export) {
            writeToCsv();
            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.abort) {
            if (csvAsyncTask != null) {
                csvAsyncTask.cancel(true);
                csvAsyncTask = null;
            }
            hideProgressBar();
        } else if(item.getItemId() == R.id.delete) {
            deleteTestResults();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (permsRequestCode) {
            case PERMISSION_WRITE_REQUEST_CODE:
                boolean fileSystemGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (fileSystemGranted) {
                    writeToCsv();
                } else {
                    Toast.makeText(this, "Access to file system was denied.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void showProgressBar(int optionalText) {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        outputTV.setVisibility(View.VISIBLE);
        outputTV.setText(R.string.exporting);
        list.setVisibility(View.GONE);
    }

    @Override
    public void updateProgress(double progress) {
        progressBar.setProgress((int) progress);
        String out = getString(R.string.exporting) + " "
                + NumberHelper.formatDecimal(this, progress)
                + " " + getString(R.string.of) + " 100%";
        outputTV.setText(out);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        outputTV.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
        csvAsyncTask = null;
        invalidateOptionsMenu();
    }

    private void initializeView() {
        setContentView(R.layout.list_layout);
        outputTV = (AppCompatTextView) findViewById(R.id.listOutputTV);
        progressBar = (ProgressBar) findViewById(R.id.listProgressBar);
        list = (ListView) findViewById(R.id.list);
        if (testResults != null && !testResults.isEmpty()) {
            list.setAdapter(new TestResultAdapter(this, testResults));
        } else {
            list.setVisibility(View.GONE);
            outputTV.setVisibility(View.VISIBLE);
        }
    }

    private void writeToCsv() {
        if (test != null && testResults != null && !testResults.isEmpty()) {
            int writePermission = ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (shouldAskPermission() && writePermission != PackageManager.PERMISSION_GRANTED) {
                requestPermission();
            } else {
                csvAsyncTask = new WriteToCsvTask(this, test.getType().name(), testResults);
                csvAsyncTask.execute();
            }
        }
    }

    private void deleteTestResults() {
        for (TestResult t : testResults) {
            t.deleteSelf(this);
        }
        testResultDao.detachAll();
        finish();
    }

    @TargetApi(23)
    private void requestPermission() {
        String[] perms = {"android.permission.WRITE_EXTERNAL_STORAGE"};
        requestPermissions(perms, PERMISSION_WRITE_REQUEST_CODE);
    }
}
