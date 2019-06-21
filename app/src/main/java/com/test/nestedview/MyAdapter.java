package com.test.nestedview;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

/***
 *   created by android on 2019/6/21
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private List<String> list=new ArrayList<>();
    private int layoutId;

    public MyAdapter(int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(layoutId, viewGroup, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.textView.setText(list.get(position));
        Log.i("=====","====="+list.get(position));
    }
    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView= (TextView) itemView;
            textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,dp2px(textView.getContext(),40)));
            textView.setGravity(Gravity.CENTER_VERTICAL);
        }
        private int dp2px(Context context,int dp){
            return (int) (context.getResources().getDisplayMetrics().density*dp+0.5f);
        }
    }


}
