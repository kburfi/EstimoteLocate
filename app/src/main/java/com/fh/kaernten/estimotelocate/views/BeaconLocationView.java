package com.fh.kaernten.estimotelocate.views;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.util.AttributeSet;

import com.fh.kaernten.estimotelocate.R;
import com.fh.kaernten.estimotelocate.helper.TestType;
import com.fh.kaernten.estimotelocate.helper.TrilaterationAverage;
import com.fh.kaernten.estimotelocate.interfaces.Room2Beacon;
import com.fh.kaernten.estimotelocate.objects.Room;

import org.apache.commons.math3.linear.RealVector;

import java.util.ArrayList;
import java.util.List;


public class BeaconLocationView extends MultiTouchView {

    private float beaconRadius;

    private boolean camAndBitmapsInitialized = false;
    private boolean showDebugInformation;
    private Bitmap bitmap;
    private Bitmap userBitmap;
    private Matrix myMatrix;
    private Paint paint;

    private Room roomToDraw;
    private TrilaterationAverage trilaterationAverage;
    private List<Room2Beacon> beaconsToDraw;

    public BeaconLocationView(Context context) {
        this(context, null, 0);
    }

    public BeaconLocationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BeaconLocationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.beaconsToDraw = new ArrayList<>();
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(2);
        paint.setTextSize(16f);
        myMatrix = new Matrix();
        showDebugInformation = false;
        trilaterationAverage = new TrilaterationAverage(TestType.ACCURACY_2D);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.roomToDraw != null) {
            setCam(canvas);
            canvas.drawBitmap(bitmap, 0, 0, paint);
            canvas.drawColor(Color.WHITE);    // set Background color of canvas

            canvas.concat(myMatrix);
            drawRoom(canvas);
            trilaterateUser(canvas);
            drawBeacons(canvas);
        }
    }

    /**
     * @param room the room drawn in this view
     */
    public void setRoom(Room room) {
        this.roomToDraw = room;
        defineMaximumBandwidthOfObjects((float) Math.abs(roomToDraw.getWidth()), (float) Math.abs(roomToDraw.getHeight()));
        beaconRadius = (float) Math.min(roomToDraw.getWidth(), roomToDraw.getHeight()) / 20;
    }

    /**
     * @param beacons beacons that shall be drawn
     */
    public void setBeacons(List<? extends Room2Beacon> beacons) {
        this.beaconsToDraw.clear();
        this.beaconsToDraw.addAll(beacons);
        invalidate();
    }

    public void invertVisibilityOfDebugInfo() {
        this.showDebugInformation = !showDebugInformation;
        invalidate();
    }

    /**
     * call when this view is recycled
     */
    public void recycle() {
        userBitmap.recycle();
    }

    /**
     * sets a 3D view and the origin of the coordinate system
     *
     * @param canvas cenvas to set camera on
     */
    private void setCam(Canvas canvas) {
        if (!camAndBitmapsInitialized) {
            bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.RGB_565);
            userBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.abstract_user_small);
            Matrix m = new Matrix();
            Camera myCam = new Camera();
            myCam.save();
            // translate sets the coordinate system origin
            float width = canvas.getWidth() / 2F;
            float height = -canvas.getHeight() / 2F;
            defineCoordinateSystemOrigin(width, height);
            myCam.translate(width, height, 0F);
            rotateCameraIn3D(myCam);
            myCam.getMatrix(m);
            myCam.restore();
            myMatrix = m;
        }
        camAndBitmapsInitialized = true;
    }

    /**
     * optional 3D rotation of view, call in setCam..
     *
     * @param myCam camera to rotate
     */
    private void rotateCameraIn3D(Camera myCam) {
//        myCam.rotateX(5f);
//        myCam.rotateY(0f);
//        myCam.rotateZ(-10f);
    }

    private void drawRoom(Canvas canvas) {
        if (roomToDraw != null) {
            paint.setColor(Color.BLACK);
            paint.setStyle(Style.STROKE);
            RectF room = new RectF(
                    getScaledX(0f),
                    getScaledY(0f),
                    getScaledX(roomToDraw.getWidth().floatValue()),
                    getScaledY(roomToDraw.getHeight().floatValue()));
            canvas.drawRect(room, paint);
        }
    }

    private void drawBeacons(Canvas canvas) {
        if (!beaconsToDraw.isEmpty()) {
            for (Room2Beacon beacon : beaconsToDraw) {
                drawBeacon(canvas, beacon);
                drawRadius(canvas, beacon);
            }
        }
    }

    private void drawBeacon(Canvas canvas, Room2Beacon room2Beacon) {
        paint.setColor(room2Beacon.getColor());
        paint.setStyle(Style.FILL_AND_STROKE);
        canvas.drawCircle(
                getScaledX(room2Beacon.getLocationInRoom().getxCoordinate().floatValue()),
                getScaledY(room2Beacon.getLocationInRoom().getyCoordinate().floatValue()),
                beaconRadius * getCurrentScale(),
                paint);
    }

    private void drawRadius(Canvas canvas, Room2Beacon room2Beacon) {
        if (!objectMovewmentActive() && showDebugInformation) {
            paint.setStyle(Style.STROKE);
            canvas.drawCircle(
                    getScaledX(room2Beacon.getLocationInRoom().getxCoordinate().floatValue()),
                    getScaledY(room2Beacon.getLocationInRoom().getyCoordinate().floatValue()),
                    (room2Beacon.getAccuracy().floatValue()) * getCurrentScale(),
                    paint);
        }
    }

    private void trilaterateUser(Canvas canvas) {
        if (!objectMovewmentActive()) {
            RealVector result = trilaterationAverage.getNewAverage(beaconsToDraw);
            if (result != null) {
                drawUserPosition(canvas, result);
            }
        }
    }

    private void drawUserPosition(Canvas canvas, RealVector position) {
        // drawBitmap gets top left coordinates -> shifted to center of bitmap
        canvas.drawBitmap(userBitmap,
                getScaledX((float) position.getEntry(0)) - userBitmap.getWidth() / 2,
                getScaledY((float) position.getEntry(1)) - userBitmap.getHeight() / 2,
                paint);
    }
}