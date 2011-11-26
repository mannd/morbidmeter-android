package org.epstudios.morbidmeter;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MMConfigure extends Activity {
	private Context self = this;
	private int appWidgetId;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
				AppWidgetManager.INVALID_APPWIDGET_ID);
		Intent cancelResultValue = new Intent();
		cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
				appWidgetId);
		setResult(RESULT_CANCELED, cancelResultValue);
		setContentView(R.layout.configure);

		Button ok = (Button) findViewById(R.id.ok_button);
		ok.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				// stuff
				Intent resultValue = new Intent();
				resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
						appWidgetId);
				setResult(RESULT_OK, resultValue);
				finish();
			}
		});
		
		
		Button cancel = (Button) findViewById(R.id.cancel_button);
		
	}

}
