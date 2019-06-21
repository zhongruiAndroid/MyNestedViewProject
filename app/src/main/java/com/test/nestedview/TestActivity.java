package com.test.nestedview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    RecyclerView rv;
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
    }

    private void initView() {
        rv=findViewById(R.id.rv);
        adapter=new MyAdapter(android.R.layout.test_list_item);
        List<String> list=new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("第一个recyclerview的第"+i+"个item");
        }
        adapter.setList(list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

    }
}
