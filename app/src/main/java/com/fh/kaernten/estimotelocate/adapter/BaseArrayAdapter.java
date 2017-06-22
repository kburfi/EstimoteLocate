package com.fh.kaernten.estimotelocate.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.fh.kaernten.estimotelocate.helper.DisplayHelper;

import java.util.List;

public abstract class BaseArrayAdapter<T> extends ArrayAdapter<T> {

    protected List<T> items;

    public BaseArrayAdapter(Context context, int textViewResourceId, List<T> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }

    @Override
    public T getItem(int position) {
        if (items != null)
            return items.get(position);
        return null;
    }

    /**
     * Overwrite this method if you want to use multiple layouts
     *
     * @param position the current item position
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = vi.inflate(getLayout(), parent, false);
        }
        if (items != null && !items.isEmpty() && items.size() > position) {
            T object = items.get(position);
            if (object != null)
                fillItem(position, object, convertView);
        }
        return convertView;
    }

    /**
     * @return resID of used Layout
     */
    protected int getLayout() {
        return -1;
    }

    /**
     * Overwrite this method
     *
     * @param position Position of the item in the adapter
     * @param object   the current data item to add
     * @param v        the inflated view that shall represent the item
     */
    protected void fillItem(int position, T object, View v) {
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if (convertView != null)
            convertView.setMinimumHeight(DisplayHelper.convertPxToDp(60, getContext()));
        return super.getDropDownView(position, convertView, parent);
    }

    /**
     * Sets the items of this adapter and notifies that it should reload itself
     * This method may only be called by UiThread
     *
     * @param items the new items to be represented in this adapter
     */
    public void setItems(List<T> items) {
        synchronized (getItems()) {
            getItems().clear();
            getItems().addAll(items);
        }
        notifyDataSetChanged();
    }

    public List<T> getItems() {
        return items;
    }
}