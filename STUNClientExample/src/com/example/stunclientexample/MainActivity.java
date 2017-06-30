package com.example.stunclientexample;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class MainActivity extends Activity
{
	private TextView tv1;
	private STUNClient s_client;
	
	private class HandleReceiver extends Handler
	{
		 @Override
		  public void handleMessage(Message message)
		  {
			 if(s_client.GetPort()>0)
			 {
				 String natOut=s_client.GetIp()+":"+String.valueOf(s_client.GetPort());
				 s_client.GetUDPSock().close();
				 tv1.setText(natOut);
			 }
			 else
			 {
				 tv1.setText(s_client.GetErrors());
			 }
		  }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tv1=(TextView)findViewById(R.id.Tv1);
		s_client = new STUNClient(5,new HandleReceiver(),"stun.l.google.com");
		Thread t=new Thread(s_client);
		t.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
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
