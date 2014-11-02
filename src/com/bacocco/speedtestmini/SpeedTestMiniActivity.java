package com.bacocco.speedtestmini;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import com.bacocco.speedtestminilib.ProgressReportListener;
import com.bacocco.speedtestminilib.SpeedTestMini;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SpeedTestMiniActivity extends Activity {
	
	private Button bttest;
	private AutoCompleteTextView txthost;
	private EditText txtport;
	private TextView lblspeed;
	private SpeedGauge gauge;
	private SpeedGauge gauge2;
	private Button bttest2;
	private class SpeedTestDownloadTask extends AsyncTask<String, Long, Long>
	{
		
		private boolean fullduplex;
		public SpeedTestDownloadTask(boolean fullduplex ) {
			this.fullduplex = fullduplex;
		}
		class DownloadThread implements Runnable 
		{
			private SpeedTestMini mini;
			public DownloadThread(SpeedTestMini mini) {
				this.mini = mini;
			}
			@Override
			public void run() {
				try {
					mini.doDownloadtest();
				} catch (IOException e) {
					// TODO Blocco catch generato automaticamente
					e.printStackTrace();
				}
				
			}
			
		}
		
		LinkedList<Long> speedsavg;
		@Override
		protected Long doInBackground(String... params) {

			publishProgress(0L,0L);
			SpeedTestMini mini = new SpeedTestMini(params[0], Integer.parseInt(params[1]));
			mini.setProgressReportListener(new ProgressReportListener() {
				
				long dlbits_last = 0;
				long ulbits_last = 0;
				@Override
				public void reportCurrentDownloadSpeed(long bits) {

					
					SpeedTestDownloadTask.this.publishProgress(bits,ulbits_last);
					dlbits_last = bits;
				}

				@Override
				public void reportCurrentUploadSpeed(long bits) {
					SpeedTestDownloadTask.this.publishProgress(dlbits_last,bits);
					ulbits_last = bits;
					
				}
			});
			
			try {
				if ( ! fullduplex )
				{
					mini.doDownloadtest();
					mini.doUploadtest();
				}else{
					Thread t = new Thread(new DownloadThread(mini));
					t.start();
					mini.doUploadtest();
					t.join();
					
				}
				return 0L;
			} catch (IOException e) {
				// TODO Blocco catch generato automaticamente
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Blocco catch generato automaticamente
				e.printStackTrace();
			}
			return 0L;
		}
		
		@Override
		protected void onProgressUpdate(Long... values) {
			String suffix = "bits/sec";
			long s = values[0];
			double sout2 = values[1];
			double gval2 = sout2 / 1000000.0;
			double sout = s;
			double gval = sout / 1000000.0;
			if ( s > 1000000 )
			{
				suffix = "Mbits/sec";
				sout /= 1000000.0;
				sout2 /= 1000000.0;
			}else if ( s > 1000 )
			{
				suffix = "Kbits/sec";
				sout /= 1000.0;
				sout2 /= 1000.0;
			}
			

			
			

			lblspeed.setText(String.format("DL: %.2f UL: %.2f", sout,sout2)+" "+suffix);
			gauge.setValue(gval);
			gauge2.setValue(gval2);
		}
		
		@Override
		protected void onPostExecute(Long result) {
			bttest.setEnabled(true);
			bttest2.setEnabled(true);
		}
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speed_test_mini);
		bttest = (Button)findViewById(R.id.btTest);
		bttest2 = (Button)findViewById(R.id.btfdtest);
		txthost = (AutoCompleteTextView)findViewById(R.id.txthost);
		txtport = (EditText)findViewById(R.id.txtport);
		lblspeed = (TextView)findViewById(R.id.txtResult);
		gauge = (SpeedGauge)findViewById(R.id.speedGauge1);
		gauge2 = (SpeedGauge)findViewById(R.id.speedGauge2);
		bttest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE); 

				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				                           InputMethodManager.HIDE_NOT_ALWAYS);
				
				bttest.setEnabled(false);
				bttest2.setEnabled(false);
				new SpeedTestDownloadTask(false).execute(txthost.getText().toString(),txtport.getText().toString());
				
			}
		});
		bttest2.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE); 

				inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
				                           InputMethodManager.HIDE_NOT_ALWAYS);
				
				bttest2.setEnabled(false);
				bttest.setEnabled(false);
				new SpeedTestDownloadTask(true).execute(txthost.getText().toString(),txtport.getText().toString());
				
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.speed_test_mini, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
