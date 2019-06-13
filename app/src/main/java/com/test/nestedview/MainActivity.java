package com.test.nestedview;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View btTest =findViewById(R.id.btTest);
        btTest.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btTest:
                goAct(TestActivity.class);
            break;
        }
    }

    private void goAct(Class clazz){
        startActivity(new Intent(this,clazz));
    }
}
