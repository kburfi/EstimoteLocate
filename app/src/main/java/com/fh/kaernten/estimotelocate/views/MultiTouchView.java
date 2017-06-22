package com.fh.kaernten.estimotelocate.views;


import android.content.Context;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewTreeObserver;

import com.fh.kaernten.estimotelocate.helper.DisplayHelper;
import com.fh.kaernten.estimotelocate.helper.Nothing;
import com.fh.kaernten.estimotelocate.interfaces.MovableObject;
import com.fh.kaernten.estimotelocate.interfaces.ObjectMovedCallback;

import java.util.ArrayList;
import java.util.List;

/**
 * Implements pinch to zoom and touch movement
 */
public abstract class MultiTouchView extends View {

    private float initScale;
    private float scale;
    private PointF lastTouch;
    private PointF coordinateSystemOrigin;
    private float offsetX;
    private float offsetY;

    private Float bandwidthX;
    private Float bandwidthY;
    private float touchRadius;

    private ScaleGestureDetector scaleGestureDetector;

    private List<MovableObject> movableObjects;
    private SparseArray<MovableObject> allTrackedPointers;

    public MultiTouchView(Context context) {
        this(context, null, 0);
    }

    public MultiTouchView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiTouchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        scaleGestureDetector = new ScaleGestureDetector(context, new ScaleListener());
        lastTouch = new PointF();
        movableObjects = new ArrayList<>();
        allTrackedPointers = new SparseArray<>();
        coordinateSystemOrigin = new PointF(0f, 0f);
        init();
    }

    /**
     * @return current scale in canvas
     */
    protected float getCurrentScale() {
        return scale;
    }

    /**
     * @param unscaledValue explicit x coordinate in canvas
     * @return scaled x coordinate
     */
    protected float getScaledX(float unscaledValue) {
        return (unscaledValue + offsetX) * scale;
    }

    /**
     * @param unscaledValue explicit y coordinate in canvas
     * @return scaled y coordinate
     */
    protected float getScaledY(float unscaledValue) {
        return (unscaledValue + offsetY) * scale;
    }

    /**
     * @param canvasValue explicit value in canvas
     * @return unscaled value
     */
    protected float getRealX(float canvasValue) {
        return (canvasValue - coordinateSystemOrigin.x) / scale - offsetX;
    }

    /**
     * @param canvasValue explicit value in canvas
     * @return unscaled value
     */
    protected float getRealY(float canvasValue) {
        return (canvasValue - coordinateSystemOrigin.y) / scale - offsetY;
    }

    /**
     * @param movableObjects list of objects which may be moved in this view
     */
    public void defineMovableObjects(List<? extends MovableObject> movableObjects) {
        this.movableObjects.clear();
        this.movableObjects.addAll(movableObjects);
    }

    protected boolean objectMovewmentActive() {
        return !this.movableObjects.isEmpty();
    }

    /**
     * Optional method to enable scaling so that all objects fit into canvas
     *
     * @param width  maximum width
     * @param height maximum height
     */
    protected void defineMaximumBandwidthOfObjects(float width, float height) {
        this.bandwidthX = width;
        this.bandwidthY = height;
        this.touchRadius = Math.min(bandwidthX, bandwidthY) / 15;
        init();
    }

    /**
     * Optional method to change default coordinate system origin
     *
     * @param x new origin width
     * @param y new origin height
     */
    protected void defineCoordinateSystemOrigin(float x, float y) {
        coordinateSystemOrigin.set(Math.abs(x), Math.abs(y));
    }

    private void init() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
                initializeScaleAndOffsets();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                // it's the first pointer, so clear all existing pointers data
                allTrackedPointers.clear();
                lastTouch.set(event.getX(0), event.getY(0));
                MovableObject m = getMovableObjectIfTouched(getRealX(lastTouch.x), getRealY(lastTouch.y));
                if (m != null) {
                    setNewCoordinatesAndCallback(m, lastTouch.x, lastTouch.y);
                    allTrackedPointers.put(event.getPointerId(0), m);
                } else {
                    allTrackedPointers.put(event.getPointerId(0), new Nothing());
                }
                break;

            case MotionEvent.ACTION_MOVE:
                // movement only allowed when not scaling
                if (!scaleGestureDetector.isInProgress()) {
                    final int pointerCount = event.getPointerCount();
                    for (int actionIndex = 0; actionIndex < pointerCount; actionIndex++) {
                        moveSelectedObject(event, actionIndex);
                    }
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
                allTrackedPointers.clear();
                invalidate();
                return true;

            case MotionEvent.ACTION_POINTER_UP:
                removeSecondaryPointer(event);
                return true;

            case MotionEvent.ACTION_CANCEL:
                allTrackedPointers.clear();
                return true;

            default:
                break;
        }
        return true;
    }

    private MovableObject getMovableObjectIfTouched(float unscaledX, float unscaledY) {
        if (!movableObjects.isEmpty()) {
            for (MovableObject m : movableObjects) {
                if (m.wasTouched(unscaledX, unscaledY, touchRadius))
                    return m;
            }
        }
        return null;
    }

    private void moveSelectedObject(MotionEvent event, int actionIndex) {
        int pointerId = event.getPointerId(actionIndex);
        MovableObject movableObject = allTrackedPointers.get(pointerId);
        if (movableObject != null) {
            final float x = event.getX(actionIndex);
            final float y = event.getY(actionIndex);
            if (movableObject instanceof Nothing) {
                changeOffsets(x, y);
            } else {
                setNewCoordinatesAndCallback(movableObject, x, y);
            }
        }
    }

    private void changeOffsets(float x, float y) {
        offsetX = offsetX + (x - lastTouch.x) / scale;
        offsetY = offsetY + (y - lastTouch.y) / scale;
        /* remember the touch position for next move event */
        lastTouch.set(x, y);
        if (getContext() instanceof ObjectMovedCallback) {
            ((ObjectMovedCallback) getContext()).onObjectMoved(null);
        }
    }

    private void setNewCoordinatesAndCallback(MovableObject movableObject, float x, float y) {
        movableObject.setNewCoordinates(
                filterForMinAndMax(getRealX(x), bandwidthX),
                filterForMinAndMax(getRealY(y), bandwidthY));
        if (getContext() instanceof ObjectMovedCallback) {
            ((ObjectMovedCallback) getContext()).onObjectMoved(movableObject);
        }
    }

    private float filterForMinAndMax(float value, float maxValue) {
        if (value < 0) {
            value = 0;
        } else if (value > maxValue) {
            value = maxValue;
        }
        return value;
    }

    private void removeSecondaryPointer(MotionEvent event) {
        int actionIndex = event.getActionIndex();
        int pointerId;
        pointerId = event.getPointerId(actionIndex);
        allTrackedPointers.remove(pointerId);
        invalidate();
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale *= detector.getScaleFactor();
            // Don't let the object get too small or too large.
            scale = Math.max(initScale / 3F, Math.min(scale, initScale * 4F));
            invalidate();
            return true;
        }
    }

    private void initializeScaleAndOffsets() {
        int twentyDP = DisplayHelper.convertPxToDp(20, getContext());
        float bandwidthX = MultiTouchView.this.bandwidthX != null ? MultiTouchView.this.bandwidthX : getWidth();
        float bandwidthY = MultiTouchView.this.bandwidthY != null ? MultiTouchView.this.bandwidthY : getHeight();
                /* always choose the lower scale */
        float scaleX = (getWidth() - twentyDP) / Math.abs(bandwidthX);
        float scaleY = (getHeight() - twentyDP) / Math.abs(bandwidthY);
        scale = Math.min(scaleX, scaleY);
        initScale = scale;
        offsetX = -(bandwidthX / 2F);
        offsetY = -(bandwidthY / 2F);
        invalidate();
    }
}