package com.github.nestedview;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.NestedScrollingParent2;
import android.support.v4.view.NestedScrollingParentHelper;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.Scroller;

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

    private VelocityTracker velocityTracker;
    private Scroller scroller;
    //不能继续上滑
    private boolean canNotTopScroll;

    private Map<View, ViewHelper> viewHelperMap;

    private List<ViewHelper> viewHelpers;
    private View lastFlingView;
    private boolean needStopOwnScroll;

    private Map<View, ViewHelper> getViewHelperMap() {
        if (viewHelperMap == null) {
            viewHelperMap = new HashMap<>();
        }
        return viewHelperMap;
    }

    private List<ViewHelper> getViewHelperList() {
        if (viewHelpers == null) {
            viewHelpers = new ArrayList<>();
        }
        return viewHelpers;
    }


    public MyNestedView(Context context) {
        super(context);
        init(context, null);
    }


    public MyNestedView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public MyNestedView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    public NestedScrollingParentHelper getHelper() {
        if (helper == null) {
            helper = new NestedScrollingParentHelper(this);
        }
        return helper;
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.MyNestedView);
        mGravity = typedArray.getInt(R.styleable.MyNestedView_Layout_layout_gravity, Gravity.LEFT);
        typedArray.recycle();

        scroller = new Scroller(getContext());

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
            viewHelper.position = i;
            getViewHelperList().add(viewHelper);

            if (childView.getVisibility() == GONE) {
                viewHelper.isGone = true;
                continue;
            }
            measureChildWithMargins(childView, widthMeasureSpec, 0, heightMeasureSpec, 0);
            LayoutParams lp = (LayoutParams) childView.getLayoutParams();
            int measuredWidth = childView.getMeasuredWidth()+lp.leftMargin+lp.rightMargin;
            int measuredHeight = childView.getMeasuredHeight()+lp.topMargin+lp.bottomMargin;


            viewHelper.childViewHeight = measuredHeight;
            viewHelper.beforeViewTotalHeight = resultHeight;

            resultWidth = Math.max(resultWidth, measuredWidth);
            resultHeight += measuredHeight;
        }


        setMeasuredDimension(
                widthMode == MeasureSpec.EXACTLY ? widthSize : Math.min(resultWidth, widthSize),
                heightMode == MeasureSpec.EXACTLY ? heightSize : Math.min(resultHeight, heightSize));
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
            if (gravity < 0) {
                gravity = mGravity;
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
                    childLeft = (width - childWidth) / 2 + layoutParams.leftMargin - layoutParams.rightMargin;
                    break;
                case Gravity.RIGHT:
                    childLeft = width - paddingRight - layoutParams.rightMargin - childWidth;
                    break;
                case Gravity.LEFT:
                default:
                    childLeft = paddingLeft + layoutParams.leftMargin;
                    break;
            }


            childView.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight);

            childAllHeight += childHeight;
            canScrollHeight += childHeight;
        }
        canScrollHeight -= getMeasuredHeight();
    }

    @Override
    public void addView(View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);
        ViewHelper viewHelper = new ViewHelper();
        viewHelper.view = child;
        if (viewHelperMap == null) {
            viewHelperMap = new HashMap<>();
        }
        viewHelperMap.put(child, viewHelper);
        getViewHelperList().add(viewHelper);

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

        if (viewHelperMap == null || viewHelperMap.size() == 0) {
            return;
        }
        ViewHelper viewHelper = viewHelperMap.get(target);
        if (viewHelper == null) {
            return;
        }

        //上滑动View，将如果view上面还有view可以显示，则开始隐藏上面的view
        boolean needHiddenTopView = getScrollY() < viewHelper.beforeViewTotalHeight && dy > 0;
        if (needHiddenTopView) {
            //修复过度偏移
            dy = Math.min(dy, viewHelper.beforeViewTotalHeight - getScrollY());
        }
        //如果view到顶部了，下滑动View,则开始滑动parent,显示上面的view
        boolean needShowTopView = ViewCompat.canScrollVertically(target, -1) == false && dy < 0 && getScrollY() > 0;
        //如果view显示底部，下滑动view，滑动parent ，直到view的底部视图将要滑出去的时候,才滑动自身
        boolean needScrollParent = dy < 0 && getScrollY() > viewHelper.beforeViewTotalHeight;
        if (needScrollParent) {
            //修复过度偏移  此时dy是负数，所以比较最大值
            dy = Math.max(dy, viewHelper.beforeViewTotalHeight - getScrollY());
        }
        if (needHiddenTopView || needShowTopView || needScrollParent) {
            scrollBy(0, dy);
            consumed[1] = dy;
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
/*
        if(dyUnconsumed<=0){
            //不考虑view下滑的情况
            return;
        }*/
        if(dyUnconsumed>0){
            //根据距离获取view,判断是否能向上滑动
            if (getViewHelperList() == null || getViewHelperList().size() < 2) {
                if (dyUnconsumed > 0 && getScrollY() < canScrollHeight) {
                    scrollBy(0, dyUnconsumed);
                }
                return;
            }
            if(dyUnconsumed>0&&getScrollY() >= canScrollHeight){
                //如果当前容器滚动到最底部，判断最后一个view是否可以滑动自身
                if(getViewHelperList().size()==0){
                    return;
                }
                ViewHelper viewHelper = getViewHelperList().get(getViewHelperList().size() - 1);
                if(ViewCompat.canScrollVertically(viewHelper.view,1)==false){
                    canNotTopScroll=true;
                if(viewHelper.view instanceof RecyclerView){
                    ((RecyclerView)viewHelper.view).stopScroll();
                }
                    dyUnconsumed=0;
                }
//                viewHelper.view.scrollBy(0,dyUnconsumed);
                return;
            }
            ViewHelper viewHelper = getCurrentTopView(dyUnconsumed);
            if (viewHelper == null) {
                scrollBy(0, dyUnconsumed);
                return;
            }
            if (ViewCompat.canScrollVertically(viewHelper.view, 1)) {
                int scrollOffset = dyUnconsumed;
                //容器滑动跨过两个view的情况
                if (getScrollY() < viewHelper.beforeViewTotalHeight) {
                    int i = viewHelper.beforeViewTotalHeight - getScrollY();
                    scrollOffset = scrollOffset - i;
                    scrollBy(0, i);
                }
                viewHelper.view.scrollBy(0, scrollOffset);
            }else{
                scrollBy(0, dyUnconsumed);
            }
            return;
        }
        if(dyUnconsumed<0){
            //根据距离获取view,判断是否能向上滑动
            if (getViewHelperList() == null || getViewHelperList().size() < 2) {
                if (dyUnconsumed < 0 && getScrollY() >0) {
                    scrollBy(0, dyUnconsumed);
                }
                return;
            }
            if(dyUnconsumed<0&&getScrollY() <=0){
                //如果当前容器滚动到最底部，判断最后一个view是否可以滑动自身
                if(getViewHelperList().size()==0){
                    return;
                }
                ViewHelper viewHelper = getViewHelperList().get(getViewHelperList().size() - 1);
                if(ViewCompat.canScrollVertically(viewHelper.view,-1)==false){
                    canNotTopScroll=true;
                    dyUnconsumed=0;
                }
                viewHelper.view.scrollBy(0,dyUnconsumed);
                return;
            }
            ViewHelper viewHelper = getCurrentTopView(dyUnconsumed);
            if (viewHelper == null) {
                scrollBy(0, dyUnconsumed);
                return;
            }
            if (ViewCompat.canScrollVertically(viewHelper.view, 1)) {
                int scrollOffset = dyUnconsumed;
                //容器滑动跨过两个view的情况
                if (getScrollY() < viewHelper.beforeViewTotalHeight) {
                    int i = viewHelper.beforeViewTotalHeight - getScrollY();
                    scrollOffset = scrollOffset - i;
                    scrollBy(0, i);
                }
                viewHelper.view.scrollBy(0, scrollOffset);
            }else{
                scrollBy(0, dyUnconsumed);
            }
            return;
        }


    }

    /*根据当前滑动的距离计算出当前屏幕第一个view,用来判断自身能否滑动*/
    private ViewHelper getCurrentTopView(int offset) {
        int scrollY = getScrollY() + offset;
        int start = 0;
        int end = getViewHelperList().size() - 1;
        boolean flag = true;
        ViewHelper viewHelper=null;
        while (flag) {
            int middle = (start + end) / 2;
            viewHelper = getViewHelperList().get(middle);
            if (viewHelper.beforeViewTotalHeight <= scrollY && scrollY <= viewHelper.getIncludeOwnHeight()) {
                flag=false;
            } else if (viewHelper.beforeViewTotalHeight > scrollY) {
                end   = middle - 1;
            } else {
                start = middle + 1;
            }
        }
        return viewHelper;
    }

    @Override
    public boolean onNestedPreFling(View target, float velocityX, float velocityY) {
        parentFling(target,velocityY);
        return false;
    }
    private void parentFling(View target,float velocityY) {
        lastFlingView=target;
        scroller.fling(0, getScrollY(), 0, (int) velocityY, 0, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
        invalidate();
    }
    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            //上滑
            if(lastFlingView!=null&&ViewCompat.canScrollVertically(lastFlingView,1)){
                int currY = scroller.getCurrY();
            }
        }
    }

    private void initVelocityTracker() {
        if (velocityTracker == null){
            velocityTracker = VelocityTracker.obtain();
        }else{
            velocityTracker.clear();
        }
    }
    private VelocityTracker getVelocityTracker() {
        if (velocityTracker == null) {
            velocityTracker = VelocityTracker.obtain();
        }
        return velocityTracker;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastFlingView=null;
                initVelocityTracker();
                break;
            case MotionEvent.ACTION_MOVE:
                getVelocityTracker().addMovement(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getVelocityTracker().computeCurrentVelocity(1000, ViewConfiguration.get(getContext()).getScaledMaximumFlingVelocity());
                float yVelocity = getVelocityTracker().getYVelocity();
                scroller.fling(getScrollX(),getScrollY(),0, (int) yVelocity,0,Integer.MAX_VALUE,0,Integer.MAX_VALUE);
                computeFling();
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void computeFling() {
        while (lastFlingView!=null&&scroller.computeScrollOffset()){
            int currY = scroller.getCurrY();
            lastFlingView.scrollBy(0,currY);
        }
    }

    @Override
    public void scrollTo(int x, int y) {
        if (y <= 0) {
            y = 0;
        }
        if (y > canScrollHeight) {
            y = canScrollHeight;
        }
        super.scrollTo(x, y);
        /*if (lastFlingView != null) {
            RecyclerView recyclerView = recyclerViewList.get(1);
            ViewHelper viewHelper = viewHelperMap.get(recyclerView);
            int beforeViewTotalHeight = viewHelper.beforeViewTotalHeight;
            int alreadyScrollHeight=getScrollY();
            if(alreadyScrollHeight>=beforeViewTotalHeight){
//                recyclerView.fling(0, (int) velocityTracker.getYVelocity());
                recyclerView.scrollTo(0,y-alreadyScrollHeight);

            }else{
                super.scrollTo(x, y);
            }
            lastFlingView=null;
        }else{
            super.scrollTo(x, y);
        }*/
    }

    public void setGravity(int gravity) {
        if (gravity != Gravity.LEFT || gravity != Gravity.RIGHT || gravity != Gravity.CENTER_HORIZONTAL) {
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
