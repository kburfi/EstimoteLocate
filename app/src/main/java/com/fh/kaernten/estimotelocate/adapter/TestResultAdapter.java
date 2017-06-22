package com.fh.kaernten.estimotelocate.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.fh.kaernten.estimotelocate.objects.TestResult;

import java.util.List;

public class TestResultAdapter extends BaseArrayAdapter<TestResult> {

    private static final int LAYOUT_RES = android.R.layout.simple_list_item_2;

    public TestResultAdapter(Context context, List<TestResult> items) {
        super(context, LAYOUT_RES, items);
    }

    @Override
    protected void fillItem(int position, TestResult object, View v) {
        TextView text = (TextView) v.findViewById(android.R.id.text1);
        text.setText(object.printResult(getContext()));
        String details = object.printDetails(getContext());
        TextView text2 = (TextView) v.findViewById(android.R.id.text2);
        text2.setText(details);
    }

    @Override
    protected int getLayout() {
        return LAYOUT_RES;
    }
}