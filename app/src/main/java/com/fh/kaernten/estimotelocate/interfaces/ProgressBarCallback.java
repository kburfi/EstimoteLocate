package com.fh.kaernten.estimotelocate.interfaces;

public interface ProgressBarCallback {

    void showProgressBar(int optionalText);

    void updateProgress(double progress);

    void hideProgressBar();
}
