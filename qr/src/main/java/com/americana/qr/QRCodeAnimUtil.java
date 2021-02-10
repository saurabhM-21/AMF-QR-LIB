package com.americana.qr;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;

import androidx.core.content.ContextCompat;



import static android.view.View.VISIBLE;

/**
 * Use of class
 * <<
 * This class is used to perform animation on scanner view from top to bottom and vice-versa
 * In this class we are also managing scanner view width and height
 * >>
 */

public class QRCodeAnimUtil {

    // object that contains reference of #Animation class to show scanner animation from top to bottom
    private Animation animationStart;
    // object that contains reference of #Animation class to show scanner animation from bottom to top
    private Animation animationReverse;
    // variable that contains width ratio of scanner view
    private final float DEFAULT_FRAME_ASPECT_RATIO_WIDTH = 1.01f;
    // variable that contains height ratio of scanner view
    private final float DEFAULT_FRAME_ASPECT_RATIO_HEIGHT = 1.03f;
    // variable that contains frame size of scanner view (i.e scanner with with in transparent square area)
    private final float DEFAULT_FRAME_SIZE = 0.49f;

    /**
     * Method call to start scanner animation
     * @param context this is first parameter used to access resources
     * @param flBar this is second parameter used to update scanner view width and height
     * @param bar this is third parameter used to show scanner view widget
     * @param width this is fourth parameter used to set width of scanner view
     * @param height this is fifth parameter used to set height of scanner view
     * @param  qrBoarderWidth this is sixth parameter used to calculate scanner view width and height by subtracting its value
     *
     */

    public void setQRCodeFrameAnimation(Activity context, FrameLayout flBar, final View bar, int width,int height, int qrBoarderWidth) {
        if(animationStart!=null)
            return;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int widthOfScannerFrame;
        int heightOfScannerFrame;
        //noinspection ConstantConditions
        if (DEFAULT_FRAME_ASPECT_RATIO_WIDTH <= DEFAULT_FRAME_ASPECT_RATIO_HEIGHT) {
            widthOfScannerFrame = (int) (displayMetrics.widthPixels * DEFAULT_FRAME_ASPECT_RATIO_WIDTH * DEFAULT_FRAME_SIZE);
            heightOfScannerFrame = (int) (widthOfScannerFrame * DEFAULT_FRAME_ASPECT_RATIO_HEIGHT);
        } else {
            heightOfScannerFrame = (int) (displayMetrics.heightPixels * DEFAULT_FRAME_ASPECT_RATIO_HEIGHT * DEFAULT_FRAME_SIZE);
            widthOfScannerFrame = (int) (heightOfScannerFrame * DEFAULT_FRAME_ASPECT_RATIO_WIDTH);
        }
        if(width==0 || height==0) {
            flBar.getLayoutParams().width = widthOfScannerFrame;
            flBar.getLayoutParams().height = heightOfScannerFrame;
        }else {

            flBar.getLayoutParams().width = width-2*qrBoarderWidth;
            flBar.getLayoutParams().height = height-qrBoarderWidth;
        }

        //setup the scanner animation
        animationStart = AnimationUtils.loadAnimation(context, R.anim.scanner_transition);
        animationReverse = AnimationUtils.loadAnimation(context, R.anim.scanner_transition_reverse);

        final Drawable drawableStartScan = ContextCompat.getDrawable(context, R.drawable.d_rectangle_qr_scanner);
        final Drawable drawableRevScan = ContextCompat.getDrawable(context, R.drawable.d_rectangle_qr_scanner_rev);

      /*  if(bar!=null)
            bar.setVisibility(VISIBLE);
      */  animationStart.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if(bar!=null)
                    bar.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(bar!=null && animationReverse!=null) {
                    bar.startAnimation(animationReverse);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        animationReverse.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                if(bar!=null)
                    bar.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if(bar!=null && animationStart!=null) {
                    bar.startAnimation(animationStart);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        //initiate with start
        if(bar!=null && animationStart!=null)
            bar.startAnimation(animationStart);
    }

    /**
     * Use of method
     * << This method will call to stop animation and release animation instances >>
     */

    public void clearAnimation()
    {

        if(animationReverse!=null) {
            animationReverse.cancel();
            animationReverse=null;
        }
        if(animationStart!=null) {
            animationStart.cancel();
            animationStart=null;
        }
    }

}
