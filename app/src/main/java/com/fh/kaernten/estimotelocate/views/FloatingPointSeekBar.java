package com.fh.kaernten.estimotelocate.views;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSeekBar;
import android.text.Editable;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.helper.NumberHelper;


public class FloatingPointSeekBar extends LinearLayout implements SeekBar.OnSeekBarChangeListener, TextView.OnEditorActionListener {

    /**
     * Implement in your activity if progress required there
     */
    public interface ProgressChangedListener {
        /**
         * @param seekBar     seekBar of which the progress changed
         * @param actualValue progress - already converted to correct float value
         * @param fromUser    indicates if user triggered action
         */
        void onProgressChanged(FloatingPointSeekBar seekBar, double actualValue, boolean fromUser);
    }

    private static final double DEFAULT_MAX = 20d;

    private double minValue = 0d;
    private int multiplier = 10;

    private AppCompatSeekBar seekBar;
    private AppCompatEditText editText;

    public FloatingPointSeekBar(Context context) {
        this(context, null, 0);
    }

    public FloatingPointSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingPointSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    public void setSelectedValue(double value) {
        seekBar.setProgress(scaleValue(value));
    }

    /**
     * @return value selected in this SeekBar
     */
    public double getSelectedValue() {
        return getActualValue(seekBar.getProgress());
    }

    public void setMin(double newMin) {
        this.minValue = newMin < 0 ? 0 : newMin;
        seekBar.invalidate();
    }

    /**
     * @param newMax sets the default max value in full int steps
     */
    public void setMax(double newMax) {
        newMax = newMax < minValue + 1f ? minValue + 1f : newMax;
        seekBar.setMax(scaleValue(newMax));
    }

    /**
     * Sets floating point precision, call before calling setMax()!
     *
     * @param newMultiplier e.g. 10 defines one decimal place; 100 defines two decimal places..
     */
    public void setPrecision(int newMultiplier) {
        this.multiplier = newMultiplier;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        updateEditText(progress);
        if (getContext() instanceof ProgressChangedListener) {
            ((ProgressChangedListener) getContext())
                    .onProgressChanged(this, getActualValue(progress), fromUser);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }

    @Override
    public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
        if (actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_DONE) {
            setProgressToUserInput();
        }
        return false;
    }

    private void initializeView() {
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.floating_point_seekbar, this);
        seekBar = (AppCompatSeekBar) v.findViewById(R.id.numberSeekBar);
        editText = (AppCompatEditText) v.findViewById(R.id.numberInput);
        editText.setOnEditorActionListener(this);
        setMax(DEFAULT_MAX);
        seekBar.setOnSeekBarChangeListener(this);
        seekBar.setProgress(multiplier);
    }

    private void setProgressToUserInput() {
        Editable e = editText.getText();
        if (e != null) {
            Double userInput = NumberHelper.convertToDouble(e.toString(), minValue);
            int convertedValue = scaleValue(userInput);
            seekBar.setProgress(convertedValue);
            resetEditTextIfInputInvalid(convertedValue);
        }
    }

    private void resetEditTextIfInputInvalid(int convertedValue) {
        if (convertedValue > seekBar.getMax() || convertedValue < multiplier)
            updateEditText(seekBar.getProgress());
    }

    private void updateEditText(int progress) {
        editText.setText(NumberHelper.formatDecimal(getContext(), getActualValue(progress)));
    }

    private int scaleValue(Double userInput) {
        return (int) ((userInput - minValue) * multiplier);
    }

    private double getActualValue(int progress) {
        return NumberHelper.roundTo1Decimal((double) progress / multiplier + minValue);
    }
}