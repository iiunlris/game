package com.example.pushboxtest;
import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;

public class Act1 extends Activity {
	//MediaPlayer m;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.begin);

		//m = MediaPlayer.create(this, R.raw.bgm);
		//m.start();
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.begin:
			Intent intent = new Intent(Act1.this, Menu.class);
			startActivity(intent);
			break;			
		case R.id.exit:
			this.finish();
			break;
		}

	}

}
