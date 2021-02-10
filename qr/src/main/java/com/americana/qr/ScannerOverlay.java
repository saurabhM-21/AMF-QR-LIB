package com.americana.qr;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.ViewGroup;


/**
 * Use of class
 * This class will used to draw overlay over the QR scaner screen
 * TRgis class will draw a black shadow on screen and left transparent square area between center of the screen
 */

public class ScannerOverlay extends ViewGroup {
    private int mLeft, mRight, mTop,mBottom;

    /**
     * //constructor
     * @param context
     */

    public ScannerOverlay(Context context) {
        super(context);
    }

    /**
     * constructor
     * @param context
     * @param attrs
     */

    public ScannerOverlay(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    /**
     * constructor
     * @param context
     * @param attrs
     * @param defStyle
     */


    public ScannerOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public void onLayout(boolean changed, int left, int top, int right, int bottom) {
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }


    @Override
    public boolean shouldDelayChildPressedState() {
        return false;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // draw transparent rect
        int cornerRadius = 0;
        Paint eraser = new Paint();
        eraser.setAntiAlias(true);
        eraser.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

        RectF rect = new RectF(mLeft, mTop, mRight, mBottom);
        canvas.drawRoundRect(rect, (float) cornerRadius, (float) cornerRadius, eraser);
        invalidate();

    }

    /**
     * Method will call to update position of transparent square area
     * @param left This is first parameter used to identify left position of view
     * @param right This is second parameter used to identify right position of view
     * @param top This is third parameter used to identify top position of view
     * @param bottom This is fourth  parameter used to identify bottom position of view
     * @param qrBoarderWidth This is fifth parameter used  update view actual position with the border
     */

    public void changeSquarePosition(int left, int right, int top, int bottom,int qrBoarderWidth) {
        mLeft=left+qrBoarderWidth;
        mRight=right-qrBoarderWidth;
        mTop=top+qrBoarderWidth;
        mBottom=bottom-qrBoarderWidth;
        invalidate();
    }
    /**
     * Use of method
     * << This method will use to get left position to start draw of transparent square area>>
     * @return
     */

    public int getLeftPos() {
        return mLeft;
    }

    /**
     * Use of method
     * << This method will use to get right position to start draw of transparent square area>>
     * @return
     */

    public int getRightPos() {
        return mRight;
    }

    /**
     * Use of method
     * << This method will use to get right position to start draw of transparent square area>>
     * @return
     */


    public int getTopPos() {
        return mTop;
    }

    /**
     * Use of method
     * << This method will use to get bottom position to start draw of transparent square area>>
     * @return
     */

    public int getBottomPos() {
        return mBottom;
    }
}

