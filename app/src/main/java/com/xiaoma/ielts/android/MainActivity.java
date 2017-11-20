package com.xiaoma.ielts.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.xiaoma.ielts.android.step.BaseStepOneActivity;
import com.xiaoma.ielts.android.step.BaseStepThirdActivity;
import com.xiaoma.ielts.android.step.BaseStepTwoActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private android.widget.Button btn1;
    private android.widget.Button btn2;
    private android.widget.Button btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.btn3 = (Button) findViewById(R.id.btn3);
        this.btn2 = (Button) findViewById(R.id.btn2);
        this.btn1 = (Button) findViewById(R.id.btn1);
        this.btn1.setOnClickListener(this);
        this.btn2.setOnClickListener(this);
        this.btn3.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn1:
                jumpActivity(BaseStepOneActivity.class);
                break;
            case R.id.btn2:
                jumpActivity(BaseStepTwoActivity.class);
                break;
            case R.id.btn3:
                jumpActivity(BaseStepThirdActivity.class);
                break;
        }
    }

    private void jumpActivity(Class<?> className){
        startActivity(new Intent(this,className));
    }
}
