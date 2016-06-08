package com.comtop.pdf.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;

import com.comtop.pdf.R;

/**
 * Created by linyong on 2015/1/28.
 */
public class SbztByqActivity extends Activity {

    private Spinner byqSpinner;
    private Button byqQueryBtn;
    private CheckBox deviceTypeMp;
    private CheckBox deviceTypeRz;
    private CheckBox deviceTypeTx;
    private LinearLayout byqContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sbzt_byq);
        init();
    }

    private void init(){
        /*byqSpinner = (Spinner) findViewById(R.id.byq_spinner_id);
        SpinnerAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,getByqData());
        byqSpinner.setAdapter(adapter);
        
        byqQueryBtn = (Button) findViewById(R.id.byq_query_id);
        byqQueryBtn.setOnClickListener(this.byqQueryClickListener);

        deviceTypeMp = (CheckBox) findViewById(R.id.deviceType_mp_id);
        deviceTypeRz = (CheckBox) findViewById(R.id.deviceType_rz_id);
        deviceTypeTx = (CheckBox) findViewById(R.id.deviceType_tx_id);*/
        //byqContainer = (LinearLayout) findViewById(R.id.byq_container_id);
    }
    
    OnClickListener byqQueryClickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//byqContainer.addView(View.inflate(SbztByqActivity.this, R.layout.tab1, null));
		}
	};

    private List<String> getByqData(){
        List<String> data = new ArrayList<String>();
        data.add("变压器1");
        data.add("变压器2");
        data.add("变压器3");
        return data;
    }
}
