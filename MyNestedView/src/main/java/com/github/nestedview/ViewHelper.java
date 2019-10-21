package com.github.nestedview;

import android.view.View;

/***
 *   created by android on 2019/6/21
 */
class ViewHelper {
    View view;
    int childViewHeight;
    int position;
    boolean isGone;
    int beforeViewTotalHeight;

    public int getIncludeOwnHeight(){
        return childViewHeight+beforeViewTotalHeight;
    }
}
