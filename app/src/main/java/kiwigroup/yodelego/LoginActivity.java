package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RelativeLayout;

import kiwigroup.yodelego.model.User;

public class LoginActivity extends BaseLoginActivity {

    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private RelativeLayout mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEmailView = findViewById(R.id.email);
        mPasswordView = findViewById(R.id.pin);
        mLoginFormView = findViewById(R.id.email_login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    public void loginClicked(View view){
        attemptLogin();
    }

    private void attemptLogin() {
        mEmailView.setError(null);
        mPasswordView.setError(null);

        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }
        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            startLoginProcess(email, password);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return true;
    }

    private void showProgress(final boolean show) {

        for (int i = 0; i < mLoginFormView.getChildCount(); i++) {
            View child = mLoginFormView.getChildAt(i);
            child.setEnabled(!show);
        }

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    public void forgotPasswordClicked(View v){
        Intent mainIntent = new Intent().setClass(LoginActivity.this, ForgotPasswordActivity.class);
        startActivity(mainIntent);
    }

    public void createAccountClicked(View v){
        Intent mainIntent = new Intent().setClass(LoginActivity.this, RegisterActivity.class);
        startActivity(mainIntent);
    }

    @Override
    protected void onLoginError(final String errorMessage) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle(getString(R.string.error_login));
                builder.setMessage(getString(R.string.error_login_login) + ". " + errorMessage);
                builder.setPositiveButton(getString(R.string.message_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
                showProgress(false);
            }
        });
    }

    @Override
    protected void onLoginFieldError(final String tag, final String errorMessage) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (tag.equals("email")){
                    mEmailView.setError(errorMessage);
                    mEmailView.requestFocus();
                } else if (tag.equals("password")){
                    mPasswordView.setError(errorMessage);
                    mPasswordView.requestFocus();
                }
                showProgress(false);
            }
        });
    }

    @Override
    protected void onUserInfoError(final String errorMessage) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
                builder.setTitle(getString(R.string.error_login));
                builder.setMessage(getString(R.string.error_login_user) + ". " + errorMessage);
                builder.setPositiveButton(getString(R.string.message_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
                showProgress(false);
            }
        });
    }

    @Override
    protected void onLoginSuccess(User user) {
        Intent mainIntent = new Intent().setClass(LoginActivity.this, MainActivity.class);
        mainIntent.putExtra("user", user);
        startActivity(mainIntent);
        finish();
    }

    public void registerClicked(View view){
        Intent mainIntent = new Intent().setClass(LoginActivity.this, RegisterActivity.class);
        startActivity(mainIntent);
        finish();
    }
}

