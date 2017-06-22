package com.fh.kaernten.estimotelocate.views;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.estimote.coresdk.observation.region.RegionUtils;
import com.estimote.coresdk.recognition.packets.Beacon;
import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.helper.NumberHelper;
import com.fh.kaernten.estimotelocate.objects.EstimoteBeacon;

/**
 * LinearLayout representing a beacon with icon and textual description
 */
public class BeaconListItem extends LinearLayout {

    private AppCompatTextView beaconText;
    private AppCompatImageView beaconImage;

    public BeaconListItem(Context context) {
        this(context, null, 0);
    }

    public BeaconListItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeaconListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = layoutInflater.inflate(R.layout.beacon_list_item, this);
        beaconText = (AppCompatTextView) v.findViewById(R.id.beaconText);
        beaconImage = (AppCompatImageView) v.findViewById(R.id.beaconIcon);
    }

    /**
     * Updates view to represent beacon
     *
     * @param beacon      beacon to show
     * @param estimoteBeacon database reference to beacon
     */
    public void setBeacon(Beacon beacon, EstimoteBeacon estimoteBeacon) {
        if (beacon != null) {
            updateBeaconIcon(estimoteBeacon);
            setBeaconText(beacon);
            invalidate();
            requestLayout();
        }
    }

    /**
     * Alternative view mode where this beacon is used as headline
     *
     * @param estimoteBeacon defines the beacon represented in headline
     */
    public void setBeaconHeadline(EstimoteBeacon estimoteBeacon) {
        if (estimoteBeacon != null) {
            updateBeaconIcon(estimoteBeacon);
            setHeadline();
        }
    }

    private void updateBeaconIcon(EstimoteBeacon estimoteBeacon) {
        beaconImage.clearColorFilter();
        if (estimoteBeacon != null) {
            beaconImage.setColorFilter(
                    estimoteBeacon.getBeaconColor(),
                    PorterDuff.Mode.MULTIPLY);
        }
    }

    private void setBeaconText(Beacon b) {
        double accuracy = RegionUtils.computeAccuracy(b);
        String s = "major: " + b.getMajor()
                + "\nminor: " + b.getMinor()
                + "\npower: " + b.getMeasuredPower()
                + "\nrssi: " + b.getRssi()
                + "\ndistance: " + NumberHelper.formatDecimal(getContext(), accuracy);
        beaconText.setText(s);
    }

    private void setHeadline() {
        beaconText.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
        beaconText.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 18);
    }
}