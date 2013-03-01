package com.oaks.bitsnow;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends Activity
{
	EditText uname,pass;
	Button login,forgot;
	String send_user,send_pass,response;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_layout);
		setupViews();
		login.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				send_user = uname.getText().toString();
				send_pass = pass.getText().toString();
				AuthenticateUser task = new AuthenticateUser();
				task.execute();
			}
		});
		forgot.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) 
			{
				forgotPassword();
			}
		});
	}
	/**
	 * @param none
	 * @return none
	 * This function sets up the initial links for the views with the code
	 */
	public void setupViews()
	{
		uname = (EditText)findViewById(R.id.etUsername);
		pass = (EditText)findViewById(R.id.etPassword);
		login = (Button)findViewById(R.id.buttonLogin);
		forgot = (Button)findViewById(R.id.buttonForgot);
		
	}

	public void forgotPassword()
	{
		//dialog box to get id, then send as mail
		Toast.makeText(getApplicationContext(), "Password sent to mail", Toast.LENGTH_SHORT).show();
	}
	/*
	 *  AsyncTask to authenticate user
	 * */
	public class AuthenticateUser extends AsyncTask<String,Void,Boolean>
	{

		@Override
		protected Boolean doInBackground(String... params) 
		{
			return authenticateUser();
		}

		@Override
		protected void onPostExecute(Boolean result)
		{
			if(result == true)
			{
				Toast.makeText(getApplicationContext(), "User authenticated", Toast.LENGTH_SHORT).show();
				SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
			    SharedPreferences.Editor editor = prefs.edit();
			    editor.putString("user",send_user);
			    editor.commit();
				startActivity(new Intent(getApplicationContext(),MainActivity.class));
				finish();
				
			}
			
			else
			{
				Toast.makeText(getApplicationContext(), "Incorrect username or password!", Toast.LENGTH_SHORT).show();
			}
		}

		public boolean authenticateUser() 
		{
			//send_user & send_pass --> HTTP request using AsyncTask
			try
			{
				ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
				nameValuePairs.add(new BasicNameValuePair("uname", send_user));
				nameValuePairs.add(new BasicNameValuePair("pass", send_pass));
				HttpClient httpclient = new DefaultHttpClient();
				String folder = getString(R.string.network_folder);
				String postURL = folder+"authenticate.php";
				HttpPost httppost = new HttpPost(postURL);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				HttpResponse httpResponse = httpclient.execute(httppost);
				HttpEntity resEntityGet = httpResponse.getEntity();  
				if (resEntityGet != null) 
		        {  
					response = EntityUtils.toString(resEntityGet);
		            Log.d("login response", response);
		            char responseCharZero =response.charAt(0);
		            if(responseCharZero == '1')
		            {	
			        	return true;
			        }
			        else
			        {
			        	return false;
			        }	             
		        }
				else return false;
			}
			catch(Exception e)
			{
				Log.d("LoginPage error",e.getMessage());
				return false;
			}
		}
		
	}
}
