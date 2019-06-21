package com.test.nestedview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends AppCompatActivity {
    RecyclerView rv;
    RecyclerView rv2;
    RecyclerView rv3;
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        initView();
        initRv1();
        initRv2();
        initRv3();
    }

    private void initRv3() {
        rv3=findViewById(R.id.rv3);
        MyAdapter adapter=new MyAdapter(android.R.layout.test_list_item);
        List<String> list=new ArrayList<>();
        for (int i = 0; i <30; i++) {
            list.add("第三个recyclerview的第"+i+"个item");
        }
        adapter.setList(list);
        rv3.setLayoutManager(new LinearLayoutManager(this));
        rv3.setAdapter(adapter);
    }

    private void initRv2() {
        rv2=findViewById(R.id.rv2);
        MyAdapter adapter=new MyAdapter(android.R.layout.test_list_item);
        List<String> list=new ArrayList<>();
        for (int i = 0; i <30; i++) {
            list.add("第二个recyclerview的第"+i+"个item");
        }
        adapter.setList(list);
        rv2.setLayoutManager(new LinearLayoutManager(this));
        rv2.setAdapter(adapter);
    }

    private void initRv1() {
        rv=findViewById(R.id.rv);
        adapter=new MyAdapter(android.R.layout.test_list_item);
        List<String> list=new ArrayList<>();
        for (int i = 0; i <30; i++) {
            list.add("第一个recyclerview的第"+i+"个item");
        }
        adapter.setList(list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);
    }

    private void initView() {
        rv=findViewById(R.id.rv);
        adapter=new MyAdapter(android.R.layout.test_list_item);
        List<String> list=new ArrayList<>();
        for (int i = 0; i <30; i++) {
            list.add("第一个recyclerview的第"+i+"个item");
        }
        adapter.setList(list);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setAdapter(adapter);

    }
}
