package com.atlas.airtool;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.Button;

/**
 * Created by Atlas on 2016/12/2.
 */

public class ShadowButton extends Button {
    private static final String TAG = "ShadowButton";

    private int mDelayTime;

    public ShadowButton(Context context) {
        this(context, null);
    }

    public ShadowButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        initDelayTime(context, attrs);
    }

    public ShadowButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initDelayTime(context, attrs);
    }

    private void initDelayTime(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ShadowButton);
        mDelayTime = typedArray.getInteger(R.styleable.ShadowButton_delaytime, 50);
        Log.d(TAG, "delayTime: " + mDelayTime);
        typedArray.recycle();
    }

    private int mViewWidth = 0;
    private Paint mPaint;
    private Matrix mMatrix;
    private int mTranslate = 0;
    private LinearGradient linearGradient;

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (mViewWidth == 0) {
            mViewWidth = getMeasuredWidth();
            if (mViewWidth > 0) {
                mPaint = getPaint();
                linearGradient = new LinearGradient(0, 0, mViewWidth, 0,
                        new int[]{0x33ffffff, 0xffffffff, 0x33ffffff}, null, Shader.TileMode.CLAMP);
                mPaint.setShader(linearGradient);
                mMatrix = new Matrix();
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mMatrix != null) {
            mTranslate += mViewWidth / 10;
            if (mTranslate > mViewWidth * 2) {
                mTranslate = -mViewWidth;
            }
            mMatrix.setTranslate(mTranslate, 0);
            linearGradient.setLocalMatrix(mMatrix);
//            postInvalidate();
            postInvalidateDelayed(mDelayTime);
        }
    }
}
