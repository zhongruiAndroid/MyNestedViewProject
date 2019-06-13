package com.github.nestedview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import static android.widget.FrameLayout.LayoutParams.UNSPECIFIED_GRAVITY;

/***
 *   created by android on 2019/6/13
 */
public class MyNestedView extends ViewGroup implements NestedScrollingParent2 {
    private NestedScrollingParentHelper helper;
    private int canScrollHeight = 0;
    private int childAllHeight=0;

    public MyNestedView(Context context) {
        super(context);
    }

    public MyNestedView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyNestedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public NestedScrollingParentHelper getHelper() {
        if (helper == null) {
            helper = new NestedScrollingParentHelper(this);
        }
        return helper;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        int resultWidth = 0;
        int resultHeight = 0;
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == GONE) {
                continue;
            }
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int measuredWidth = childView.getMeasuredWidth();
            int measuredHeight = childView.getMeasuredHeight();

            resultWidth = Math.max(resultWidth, measuredWidth);
            resultHeight += measuredHeight;
        }


        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : resultWidth,
                heightMode == MeasureSpec.EXACTLY ? heightSize : resultHeight);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        childAllHeight = 0;
        canScrollHeight = 0;
        int childCount = getChildCount();
        if (childCount == 0) {
            return;
        }
        int paddingLeft = getPaddingLeft();
        int paddingTop = getPaddingTop();
        int paddingRight = getPaddingRight();
        int paddingBottom = getPaddingBottom();

        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == GONE) {
                continue;
            }
            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();
            childView.layout(
                    paddingLeft,
                    childAllHeight+paddingTop,
                    childWidth+paddingLeft,
                    childHeight+childAllHeight+paddingTop
            );
            childAllHeight+=childHeight;
            canScrollHeight+=childHeight;
        }
        canScrollHeight-=getMeasuredHeight();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

    }

    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
    public static class LayoutParams extends MarginLayoutParams{
        public static final int UNSPECIFIED_GRAVITY = -1;
        public int gravity = UNSPECIFIED_GRAVITY;

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
            TypedArray typedArray = c.obtainStyledAttributes(attrs, R.styleable.MyNestedView_Layout);
            gravity = typedArray.getInt(R.styleable.MyNestedView_Layout_layout_gravity, UNSPECIFIED_GRAVITY);
            typedArray.recycle();
        }
        public LayoutParams(int width, int height) {
            super(width, height);
        }
        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }
        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }
        public LayoutParams(int width, int height,LayoutParams source) {
            super(width, height);
            this.gravity = source.gravity;
        }
        public LayoutParams(LayoutParams source) {
            super(source);
            this.gravity = source.gravity;
        }
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if (axes == ViewCompat.SCROLL_AXIS_VERTICAL) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        getHelper().onNestedScrollAccepted(child, target, axes, type);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        getHelper().onStopNestedScroll(target, type);
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return super.dispatchNestedPreFling(velocityX, velocityY);
    }
}
