package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends Activity implements View.OnClickListener {
	static final String TAG = "LoginActivity";
    static DBhandler dbhandler = new DBhandler();

    ProgressDialog progressDialog;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		setContentView(R.layout.activity_login);

		Button loginButton = (Button) findViewById(R.id.buttonLoginLogin);
		loginButton.setOnClickListener(this);

		Button createAccountButton = (Button) findViewById(R.id.buttonLoginCreateAccount);
		createAccountButton.setOnClickListener(this);

        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);
	}

	@Override
	public void onClick(View view) {
		Log.d(TAG, "onClick");

		switch (view.getId()) {
			case R.id.buttonLoginLogin:
				Log.d(TAG, "buttonLoginLogin");

				EditText email = (EditText) findViewById(R.id.editTextLoginEmail);
				EditText password = (EditText) findViewById(R.id.editTextLoginPassword);

				final String emailString = email.getText().toString();
				final String passwordString = password.getText().toString();

				Log.d(TAG, "userEmail: " + emailString);
				Log.d(TAG, "userPassword: " + passwordString);
                //TODO
/*
				if (! isValidEmail(emailString)) {
					Log.d(TAG, "invalid userEmail");
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.warning_invalid_email));
				}
				else if (! (passwordString.length() > 0))
				{
					Log.d(TAG, "empty userPassword");
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.warning_password_empty));
				}
				else
				{
					Log.d(TAG, "logging in");

					sendLoginRequest(emailString, passwordString);
				}
*/
                sendLoginRequest(emailString, passwordString);
				break;

			case R.id.buttonLoginCreateAccount:
				Log.d(TAG, "buttonLoginCreateAccount");

				startActivity(new Intent(LoginActivity.this, CreateAccountActivity.class));

				break;
		}
	}

	public void sendLoginRequest(final String userEmail, final String userPassword) {
		Log.d(TAG, "sendCreateAccountRequest");

        progressDialog.show();

		new Thread() {
			public void run() {
				Log.d(TAG, "in thread");

                if (!dbhandler.checkLogin(userEmail,userPassword)) {
                    Log.d(TAG, "login unsuccessful");
                } else {
                    Log.d(TAG, "login successful");

                    try {
                        ArrayList<String> userGroups = dbhandler.getUserGroups(userEmail);
                    } catch (SQLException e){
                        Log.d(TAG,e.getMessage());
                    }

                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    intent.putExtra("user_name", userEmail);
                    startActivity(intent);
                }

                progressDialog.dismiss();
			}
		}.start();
	}

	public final static boolean isValidEmail(CharSequence target) {
		return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	private void createWarningAlert (String title, String warning) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(LoginActivity.this);

		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(warning);

		alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
			}
		});

		AlertDialog alertDialog = alertDialogBuilder.create();
		alertDialog.show();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login, menu);
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
