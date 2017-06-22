package com.fh.kaernten.estimotelocate.helper;

import android.os.AsyncTask;
import android.os.Environment;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.activities.BaseActivity;
import com.fh.kaernten.estimotelocate.interfaces.ProgressBarCallback;
import com.fh.kaernten.estimotelocate.interfaces.WritableAsCsv;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

/**
 * Async Task which writes a list of <? extends WritableAsCsv> to a csv document.
 * It Publishes progress if calling class implements ProgressBarCallback.
 */
public class WriteToCsvTask extends AsyncTask<Void, Double, Void> {

    private CsvWriter csvWriter;

    private BaseActivity activity;
    private String csvName;
    private List<? extends WritableAsCsv> csvObjects;

    private Result result;


    public WriteToCsvTask(BaseActivity activity, String csvName, List<? extends WritableAsCsv> csvObjects) {
        this.activity = activity;
        this.csvName = csvName;
        this.csvObjects = csvObjects;
    }

    @Override
    protected void onPreExecute() {
        if (activity instanceof ProgressBarCallback) {
            ((ProgressBarCallback) activity).showProgressBar(R.string.exporting);
        }
    }

    @Override
    protected Void doInBackground(Void... lists) {
        writeFile();
        return null;
    }

    @Override
    protected void onProgressUpdate(Double... values) {
        if (activity instanceof ProgressBarCallback) {
            ((ProgressBarCallback) activity).updateProgress(values[0]);
        }
    }

    @Override
    protected void onPostExecute(Void results) {
        if (activity instanceof ProgressBarCallback) {
            ((ProgressBarCallback) activity).hideProgressBar();
        }
        if (result == null || result == Result.Failure) {
            activity.showTextInToast(R.string.error_export);
        } else if (result == Result.Success) {
            activity.showTextInToast(R.string.export_done);
        }
    }

    @Override
    protected void onCancelled() {
        activity.showTextInToast(R.string.export_aborted);
        super.onCancelled();
    }

    private void writeFile() {
        try {
            getCsvWriter();
            if (csvWriter != null) {
                csvWriter.writeNext(csvObjects.get(0).getHeadlinesAsArray(activity));
                writeEachLineToCsv();
                csvWriter.close();
                result = Result.Success;
            }
        } catch (IOException e) {
            result = Result.Failure;
        }
    }

    private void getCsvWriter() throws IOException {
        File exportDir = new File(Environment.getExternalStorageDirectory(), "");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }
        File file = new File(exportDir, csvName + ".csv");
        file.createNewFile();
        csvWriter = new CsvWriter(new FileWriter(file), ';', CsvWriter.NO_QUOTE_CHARACTER,
                CsvWriter.DEFAULT_ESCAPE_CHARACTER, CsvWriter.DEFAULT_LINE_END);
    }

    private void writeEachLineToCsv() {
        for (int i = 0; i < csvObjects.size(); i++) {
            if (isCancelled()) {
                result = Result.Aborted;
                return;
            }
            WritableAsCsv t = csvObjects.get(i);
            csvWriter.writeNext(t.getValuesAsArray(activity));
            publishProgress((double) i / csvObjects.size() * 100);
        }
    }

    private enum Result {
        Success,
        Failure,
        Aborted
    }
}