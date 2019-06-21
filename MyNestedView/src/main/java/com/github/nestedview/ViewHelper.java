package com.github.nestedview;

import android.view.View;

/***
 *   created by android on 2019/6/21
 */
public class ViewHelper {
    View view;
    int childViewHeight;
    int position;
    boolean isRecyclerView;
    boolean isGone;
    int beforeViewTotalHeight;

    @Override
    public boolean equals(Object obj) {
        if(obj==this){
            return true;
        }
        return super.equals(obj);

    }
}
