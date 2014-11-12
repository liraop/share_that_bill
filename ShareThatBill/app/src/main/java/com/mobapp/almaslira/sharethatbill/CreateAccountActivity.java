package com.mobapp.almaslira.sharethatbill;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
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

import java.util.logging.Handler;
import java.util.logging.LogRecord;


public class CreateAccountActivity extends Activity implements View.OnClickListener {
	static final String TAG = "CreateAccountActivity";

	private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_account);

		this.handler = new Handler() {
			@Override
			public void close() {
			}
			@Override
			public void flush() {
			}
			@Override
			public void publish(LogRecord logRecord) {
			}
		};

		Button createButton = (Button) findViewById(R.id.buttonCreateAccountCreate);
		createButton.setOnClickListener(this);
    }

	public final static boolean isValidEmail(CharSequence target) {
		return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	private void createWarningAlert (String title, String warning) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccountActivity.this);

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
	public void onClick(View view) {
		Log.d(TAG, "onClick");

		switch (view.getId()) {
			case R.id.buttonCreateAccountCreate:
				// Check if userEmail and userPassword are valid

				EditText email = (EditText) findViewById(R.id.editTextCreateAccountEmail);
				EditText password = (EditText) findViewById(R.id.editTextCreateAccountPassword);
				EditText confirmPassword = (EditText) findViewById(R.id.editTextCreateAccountConfirmPassword);

/*
				final String emailString = new String("userEmail");
				final String passwordString = new String("userPassword");
				final String confirmPasswordString = new String("userPassword");
*/

				final String emailString = email.getText().toString();
				final String passwordString = password.getText().toString();


				if (! isValidEmail(emailString)) {
					Log.d(TAG, "invalid userEmail");
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.warning_invalid_email));

				}
				else if (! (passwordString.length() >= 6)) {
					Log.d(TAG, "userPassword is too short");
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.create_account_warning_short_password));
				}
				else if (!  passwordString.equals(confirmPassword.getText().toString())) {
					Log.d(TAG, "passwords do not match");
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.create_account_warning_passwords_not_match));

				}
				else {
					Log.d(TAG, "account created:");
					Log.d(TAG, emailString.toString());
					Log.d(TAG, passwordString.toString());

					sendCreateRequest(emailString, passwordString);
				}

				break;
		}

	}

	public void sendCreateRequest(String email, String password) {
		Log.d(TAG, "sendCreateRequest");

		final ProgressDialog pd = new ProgressDialog(CreateAccountActivity.this);
		pd.setMessage("loading");
		pd.show();

		new Thread() {
			public void run() {
				Log.d(TAG, "in thread");
				Log.d(TAG, "sleeping");

				try {
					Thread.sleep(10000);
				} catch (InterruptedException ex) {
				}

				pd.dismiss();

				createWarningAlert(getResources().getString(R.string.warning_success),
						getResources().getString(R.string.create_account_warning_account_created));
			}
		}.start();


	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.create_account, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
