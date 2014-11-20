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


public class CreateAccountActivity extends Activity implements View.OnClickListener {
	static final String TAG = "CreateAccountActivity";
    private static DBhandler dbhandler = new DBhandler();

	ProgressDialog progressDialog;
	boolean successCreating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");

        super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_create_account);

		Button createButton = (Button) findViewById(R.id.buttonCreateAccountCreate);
		createButton.setOnClickListener(this);

		progressDialog = new ProgressDialog(CreateAccountActivity.this);
		progressDialog.setMessage(getResources().getString(R.string.warning_loading));
        progressDialog.setCancelable(false);
    }

	public void done() {

        Log.d(TAG, "done");

        if (successCreating) {
            createWarningAlert(getResources().getString(R.string.warning_success),
                    getResources().getString(R.string.warning_creating_account_success), true);
        }
        else {
            createWarningAlert(getResources().getString(R.string.warning_error),
                    getResources().getString(R.string.warning_creating_account_fail), false);
        }

	}
	public final static boolean isValidEmail(CharSequence target) {
		return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
	}

	private void createWarningAlert (String title, String warning, final boolean terminate) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(CreateAccountActivity.this);

		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(warning);

		alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog,int id) {
				if (terminate)
					finish();
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

				final String emailString = email.getText().toString();
				final String passwordString = password.getText().toString();


				if (! isValidEmail(emailString)) {
					Log.d(TAG, "invalid userEmail");
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.warning_invalid_email), false);

				}
				else if (! (passwordString.length() >= 6)) {
					Log.d(TAG, "userPassword is too short");
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.create_account_warning_short_password), false);
				}
				else if (!  passwordString.equals(confirmPassword.getText().toString())) {
					Log.d(TAG, "passwords do not match");
					createWarningAlert(getResources().getString(R.string.warning_error),
							getResources().getString(R.string.create_account_warning_passwords_not_match), false);

				}
				else {
					Log.d(TAG, "creating account:");
					Log.d(TAG, emailString.toString());
					Log.d(TAG, passwordString.toString());

					progressDialog.show();

					sendCreateAccountRequest(emailString, passwordString);
				}

				break;
		}
	}

	public void sendCreateAccountRequest(final String email, final String password) {
		Log.d(TAG, "sendCreateAccountRequest");

		new Thread() {
			public void run() {
				Log.d(TAG, "in thread");

				successCreating = dbhandler.createUserAccount(email, password);

				Log.d(TAG, "create account: " + successCreating);

				progressDialog.dismiss();

				if (successCreating)
                    CreateAccountActivity.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Log.d("TAG", "on the UI thread");
                            done();
                        }
                    });
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
