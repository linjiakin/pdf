package com.comtop.pdf.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import com.comtop.pdf.R;

public class CommonDialog extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.common_dialog);
		((TextView) findViewById(R.id.msg_id)).setText(getIntent().getExtras()
				.getString("msg"));
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.finish();
		return super.onTouchEvent(event);
	}
}
