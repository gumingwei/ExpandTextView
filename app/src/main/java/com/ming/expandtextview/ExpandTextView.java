package com.ming.expandtextview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by mingwei on 1/20/16.
 */
public class ExpandTextView extends RelativeLayout {

    /**
     * 收缩后
     */
    private TextView mText;

    /**
     * 展开后
     */
    private TextView mExpandText;

    private int mTextColor = DEFAULT_COLOR;

    private int mVisiableLines = DEFAULT_LINES;

    private int mDuration = DEFAULT_DURATION;

    private Animation mExpandAnimation;

    private boolean mAnimaLock;

    private int mStartH;

    private int mEndH;

    private boolean isExpand = false;

    private final static int DEFAULT_LINES = 1;

    private final static int DEFAULT_COLOR = Color.GRAY;

    private final static int DEFAULT_DURATION = 500;

    public OnExpandListener mListener;

    public OnExpandStateListener mStateListener;

    public interface OnExpandListener {
        void onExpand(float progess);
    }

    public interface OnExpandStateListener {

        void onExpandStart();

        void onExpandEnd();

        void onShrinkStart();

        void onShrinkEnd();
    }

    public ExpandTextView(Context context) {
        this(context, null);
    }

    public ExpandTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ExpandTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(context, attrs);
        initView(context);
    }

    private void initAttr(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ExpandText);
        mVisiableLines = array.getInteger(R.styleable.ExpandText_visiable_lines, 1);
        mTextColor = array.getColor(R.styleable.ExpandText_text_color, Color.GRAY);
        array.recycle();
    }

    private void initView(Context context) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        mText = new TextView(context);
        mText.setTextColor(mTextColor);
        mText.setEllipsize(TextUtils.TruncateAt.END);
        mText.setMaxLines(mVisiableLines);
        addView(mText, params);
        mExpandText = new TextView(context);
        mExpandText.setTextColor(Color.TRANSPARENT);
        addView(mExpandText, params);
    }

    public void setText(String text) {
        mText.setText(text);
        mExpandText.setText(text);
        this.post(new Runnable() {
            @Override
            public void run() {
                ViewGroup.LayoutParams params = getLayoutParams();
                mStartH = mText.getLineHeight() * mText.getLineCount();
                mEndH = mExpandText.getLineHeight() * mExpandText.getLineCount();
                params.height = mStartH;
                setLayoutParams(params);
            }
        });
        requestLayout();
    }

    public void setTextColor(int color) {
        mTextColor = color;
        mText.setTextColor(color);
    }

    public void setTextSize(int size) {
        mText.setTextSize(size);
        mExpandText.setTextSize(size);
        requestLayout();
    }

    public void setVisiableLines(int lines) {
        mVisiableLines = lines;
        mText.setMaxLines(mVisiableLines);
    }

    public void setGravity(int gravity) {
        mText.setGravity(gravity);
        mExpandText.setGravity(gravity);
    }

    public void setEllipsize(TextUtils.TruncateAt ell) {
        mText.setEllipsize(ell);
    }

    public void setTextLineSpacingExtra(float spac) {
        mText.setLineSpacing(spac, 1.0f);
        mExpandText.setLineSpacing(spac, 1.0f);
    }

    public TextView text() {
        return mText;
    }

    public TextView expandText() {
        return mExpandText;
    }

    public int getVisiableLines() {
        return mVisiableLines;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void expand() {
        if (!isExpand && !mAnimaLock) {
            mAnimaLock = true;
            showExpandText();
            mExpandAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ViewGroup.LayoutParams params = ExpandTextView.this.getLayoutParams();
                    params.height = mStartH + (int) ((mEndH - mStartH) * interpolatedTime);
                    setLayoutParams(params);
                    if (mListener != null) {
                        mListener.onExpand(interpolatedTime);
                    }
                }
            };
            mExpandAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (mStateListener != null) {
                        mStateListener.onExpandStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isExpand = true;
                    mAnimaLock = false;
                    if (mStateListener != null) {
                        mStateListener.onExpandEnd();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mExpandAnimation.setDuration(mDuration);
            startAnimation(mExpandAnimation);
        }

    }

    public void shrink() {
        if (isExpand && !mAnimaLock) {
            mAnimaLock = true;
            mExpandAnimation = new Animation() {
                @Override
                protected void applyTransformation(float interpolatedTime, Transformation t) {
                    ViewGroup.LayoutParams params = ExpandTextView.this.getLayoutParams();
                    params.height = mStartH + (int) ((mEndH - mStartH) * (1 - interpolatedTime));
                    setLayoutParams(params);
                    if (mListener != null) {
                        mListener.onExpand(1 - interpolatedTime);
                    }
                }
            };
            mExpandAnimation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    if (mStateListener != null) {
                        mStateListener.onShrinkStart();
                    }
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    isExpand = false;
                    mAnimaLock = false;
                    showText();
                    if (mStateListener != null) {
                        mStateListener.onShrinkEnd();
                    }
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            mExpandAnimation.setDuration(mDuration);
            startAnimation(mExpandAnimation);
        }
    }

    public void showText() {
        mText.setTextColor(mTextColor);
        mExpandText.setTextColor(Color.TRANSPARENT);
    }

    public void showExpandText() {
        mText.setTextColor(Color.TRANSPARENT);
        mExpandText.setTextColor(mTextColor);
    }

    public boolean isExpandable() {
        return mExpandText.getLineCount() > mVisiableLines;
    }

    public void setOnExpandListener(OnExpandListener listener) {
        mListener = listener;
    }

    public void setOnExpandStateListener(OnExpandStateListener listener) {
        mStateListener = listener;
    }

    public void switchs() {
        if (isExpand) {
            shrink();
        } else {
            expand();
        }
    }
}
