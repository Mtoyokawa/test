package com.example.synthesis;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;


public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 setContentView(R.layout.activity_main);
		 
	//ƒ{ƒ^ƒ“ID‚ðŽæ“¾
	final Button btn = (Button) findViewById(R.id.Button01);
	
	btn.setText("Synthesis");
	btn.setOnClickListener(new View.OnClickListener() {
	@Override
		public void onClick(View v) {
        	Intent intent = new Intent(MainActivity.this, SynthesisActivity.class);
        	startActivity(intent);
          }
        });
    }
	
}

