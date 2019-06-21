package com.github.nestedview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/***
 *   created by zhongruiAndroid on 2019/6/13
 */
public class MyNestedView extends ViewGroup implements NestedScrollingParent2 {
    private NestedScrollingParentHelper helper;
    private int canScrollHeight = 0;
    private int childAllHeight = 0;
    private final int AREA_TOP = 1;
    private final int AREA_CENTER = 2;
    private final int AREA_BOTTOM = 3;
    private int scrollArea = AREA_TOP;
    private int mGravity = Gravity.LEFT;


    private Map<View,ViewHelper> viewHelperMap;



    public MyNestedView(Context context) {
        super(context);
        init(context,null);
    }


    public MyNestedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    public MyNestedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    public NestedScrollingParentHelper getHelper() {
        if (helper == null) {
            helper = new NestedScrollingParentHelper(this);
        }
        return helper;
    }
    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyNestedView);
        mGravity = typedArray.getInt(R.styleable.MyNestedView_Layout_layout_gravity,  Gravity.LEFT);
        typedArray.recycle();
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
            ViewHelper viewHelper = viewHelperMap.get(childView);
            viewHelper.position=i;
            viewHelper.view=childView;

            if (childView.getVisibility() == GONE) {
                viewHelper.isGone=true;
                continue;
            }
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            int measuredWidth = childView.getMeasuredWidth();
            int measuredHeight = childView.getMeasuredHeight();


            viewHelper.childViewHeight=measuredHeight;
            if(childView instanceof RecyclerView){
                viewHelper.isRecyclerView=true;
            }
            viewHelper.beforeViewTotalHeight=resultHeight;

            resultWidth = Math.max(resultWidth, measuredWidth);
            resultHeight += measuredHeight;
        }


        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : Math.min(resultWidth,widthSize),
                heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(resultHeight,heightSize));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        childAllHeight = 0;
        canScrollHeight = 0;
        final int width = right - left;

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
            LayoutParams layoutParams = (LayoutParams) childView.getLayoutParams();
            int gravity = layoutParams.gravity;
            if(gravity<0){
                gravity=mGravity;
            }

            int childWidth = childView.getMeasuredWidth();
            int childHeight = childView.getMeasuredHeight();

            int childLeft;
            int childTop = childAllHeight + paddingTop;
//            int childRight = childWidth + paddingLeft;
//            int childBottom = childHeight + childAllHeight + paddingTop;

            final int layoutDirection = getLayoutDirection();
            final int absoluteGravity = Gravity.getAbsoluteGravity(gravity, layoutDirection);
            switch (absoluteGravity & Gravity.HORIZONTAL_GRAVITY_MASK) {
                case Gravity.CENTER_HORIZONTAL:
                    childLeft=(width-childWidth)/2+layoutParams.leftMargin-layoutParams.rightMargin;
                    break;
                case Gravity.RIGHT:
                    childLeft=width-paddingRight-layoutParams.rightMargin-childWidth;
                    break;
                case Gravity.LEFT:
                default:
                    childLeft=paddingLeft+layoutParams.leftMargin;
                    break;
            }



            childView.layout(childLeft, childTop, childLeft+childWidth, childTop+childHeight);

            childAllHeight += childHeight;
            canScrollHeight += childHeight;
        }
        canScrollHeight -= getMeasuredHeight();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ViewHelper viewHelper=new ViewHelper();
        viewHelper.view=child;
        if(viewHelperMap==null){
            viewHelperMap=new HashMap<>();
        }
        viewHelperMap.put(child,viewHelper);
    }
    @Override
    public ViewGroup.LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }
    public static class LayoutParams extends MarginLayoutParams {
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
        public LayoutParams(int width, int height, LayoutParams source) {
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
        if(viewHelperMap==null||viewHelperMap.size()==0){
            return;
        }
        ViewHelper viewHelper = viewHelperMap.get(target);
        if(viewHelper==null){
            return;
        }

        boolean hiddenTopView=dy>0&&getScrollY()<viewHelper.beforeViewTotalHeight;
        boolean showTopView=dy<0&&getScrollY()>0&&ViewCompat.canScrollVertically(target,-1)==false;
        if(hiddenTopView||showTopView){
            scrollBy(0,dy);
            consumed[1]=dy;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
        if(dyUnconsumed>0&&getScrollY()<canScrollHeight){
            scrollBy(0,dyUnconsumed);
        }

    }

    @Override
    public boolean dispatchNestedPreFling(float velocityX, float velocityY) {
        return super.dispatchNestedPreFling(velocityX, velocityY);
    }


    @Override
    public void scrollTo(int x, int y) {
        if(y<=0){
            y=0;
        }
        super.scrollTo(x, y);
    }

    public void setGravity(int gravity) {
        if(gravity!=Gravity.LEFT||gravity!=Gravity.RIGHT||gravity!=Gravity.CENTER_HORIZONTAL){
            return;
        }
        if (mGravity != gravity) {
            mGravity = gravity;
            requestLayout();
        }
    }
    public int getGravity() {
        return mGravity;
    }
}
