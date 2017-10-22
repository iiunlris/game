package com.example.pushboxtest;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.Menu;
import android.widget.TextView;

public class PushBoxMain extends Activity {
	public static TextView textView;
	public static TextView textView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.activity_main);
		textView = (TextView)findViewById(R.id.textView);
		textView2 = (TextView)findViewById(R.id.textView2);
	}
}
