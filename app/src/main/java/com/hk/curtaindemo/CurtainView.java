package com.hk.curtaindemo;

import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup;

/**
 * Created by Administrator on 2018/3/27.
 */

public class CurtainView extends android.support.v7.widget.AppCompatImageView {

    private int curtainHeight;//窗帘的长度
    private int currentLength;//松手时长度
    private int slideLength;//手指滑动长度
    private int startY;


    private int recoveryTime=1000;
    private int maxLength=400;


    ViewGroup.LayoutParams linearParams;

    public CurtainView(Context context) {
        this(context, null);
    }

    public CurtainView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CurtainView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.e("TAG","开始了");
        post(new Runnable() {
            @Override
            public void run() {
                curtainHeight=getHeight();
                linearParams = (ViewGroup.LayoutParams) getLayoutParams();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        int y=(int)event.getY();

        switch (event.getAction()){
            case MotionEvent.ACTION_MOVE:
                slideLength=y-startY;
                if (slideLength>0 && slideLength<maxLength){
                    linearParams.height = curtainHeight+slideLength;
                    setLayoutParams(linearParams);
                }

                break;

            case MotionEvent.ACTION_DOWN:
                startY=y;
                break;

            case MotionEvent.ACTION_UP:
                Log.e("TAG","手指抬起来了--"+curtainHeight);
                currentLength=getHeight();
                recoveryAnimator();
                break;
        }

        return true;
    }

    /**
     * 下拉过后复原所需时间
     * @param recoveryTime 单位毫秒
     */
    public void setRecoveryTime(int recoveryTime){
        this.recoveryTime=recoveryTime;
    }

    /**
     * 最多可下拉多少距离
     * @param maxLength 单位PX
     */
    public void setMaxLength(int maxLength){
        this.maxLength=maxLength;
    }


    /**
     * 下拉过后复原动画
     */
    public void recoveryAnimator(){

        // 初始值 = currentLength
        // 结束值 = curtainHeight
        // ValueAnimator.ofInt()内置了整型估值器,直接采用默认的.不需要设置
        // 中间为过渡
        ValueAnimator animator=ValueAnimator.ofInt(currentLength,curtainHeight-20,curtainHeight+20,curtainHeight);
        animator.setDuration(recoveryTime);// 设置动画运行的时长
        animator.setRepeatCount(0);// 设置动画重复播放次数
        //animator.setRepeatMode(ValueAnimator.RESTART);
        // 设置重复播放动画模式
        // ValueAnimator.RESTART(默认):正序重放
        // ValueAnimator.REVERSE:倒序回放
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {

                int currentValue = (Integer) valueAnimator.getAnimatedValue();
                linearParams.height = currentValue;
                requestLayout();
            }
        });
        animator.start();
    }
}
