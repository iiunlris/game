package com.example.pushboxtest;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Sirui Lin on 2017/10/24.
 */
public class Menu extends Activity {
    public static TextView textView2, textView3, textView4, textView5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        textView2 = (TextView)findViewById(R.id.textView2);
        textView2.setTypeface(Typeface.createFromAsset(getAssets(), "SIMLI.TTF"));
        //textView2.setText("游戏规则");

        textView3 = (TextView)findViewById(R.id.textView3);
        textView3.setTypeface(Typeface.createFromAsset(getAssets(), "SIMLI.TTF"));
        //textView3.setText("皇帝诏曰，今宫中有宝，邀天下豪杰入宫探寻");

        textView4 = (TextView)findViewById(R.id.textView4);
        textView4.setTypeface(Typeface.createFromAsset(getAssets(), "SIMLI.TTF"));
        //textView4.setText("在规定时间和步数内，玩家找到所有宝藏即可通关，其中福袋为随机出现，若觅得福袋，更有惊喜");

        textView5 = (TextView)findViewById(R.id.textView5);
        textView5.setTypeface(Typeface.createFromAsset(getAssets(), "SIMLI.TTF"));
        //textView4.setText("在规定时间和步数内，玩家找到所有宝藏即可通关，其中福袋为随机出现，若觅得福袋，更有惊喜");
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imageButton:
                Intent intent = new Intent(Menu.this, PushBoxMain.class);
                startActivity(intent);
                break;
        }
    }
}
