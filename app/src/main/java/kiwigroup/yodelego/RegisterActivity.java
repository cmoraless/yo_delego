package kiwigroup.yodelego;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import kiwigroup.yodelego.server.ServerCommunication;

public class RegisterActivity extends FragmentActivity implements OnRegisterFragmentListener{

    private RegisterMainFragment mainFragment;
    private RegisterStudentFragment studentFragment;
    private RegisterEndFragment endFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mainFragment = RegisterMainFragment.newInstance();
        addFragmentToMainContent(mainFragment, false, getString(R.string.id_main_fragment));
    }


    @Override
    public void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(addToBackStack)
                ft.addToBackStack(fragmentId);
            //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.container, fragment, fragmentId);
            ft.commit();
            //getActionBar().setTitle(getString(R.string.title_dashboard));
        }
    }

    @Override
    public void createStudentAccount(boolean student, String name, String lastName, String rut, String email, String password, String university, String career, String semester){
        /*HashMap<String, String> args = new HashMap<>();
        args.put("name", name);
        args.put("last_name", lastName);
        args.put("email", email);
        args.put("password", password);
        if(!referralCode.isEmpty())
            args.put("referral_code", referralCode);

        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "api/auth/signup")
            .POST()
            .tokenized(false)
            .parameters(args)
            .nullableListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response == null) {
                        signUpButton.setEnabled(true);
                        signUpButton.setAlpha(1f);
                        cancelButton.setEnabled(true);
                        cancelButton.setAlpha(1f);
                        showProgress(false);

                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                        builder.setTitle(getString(R.string.account_created_title));
                        builder.setMessage(getString(R.string.account_created));
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        finish();
                                    }
                                });
                        builder.show();

                    } else {
                        Log.d("RegisterActivity", "creation return: " + response);
                    }
                }
            })
            .errorListener(new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    signUpButton.setEnabled(true);
                    signUpButton.setAlpha(1f);
                    cancelButton.setEnabled(true);
                    cancelButton.setAlpha(1f);
                    showProgress(false);

                    String message = "";
                    if (volleyError instanceof NetworkError) {
                        message = getString(R.string.error_network);
                    } else if (volleyError instanceof ServerError) {
                        if(volleyError.networkResponse != null){
                            try {
                                JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                                JSONObject errorsObject = responseObject.getJSONObject("error");

                                boolean fieldError = false;
                                if(checkForError(errorsObject, "name", textViewFirstName)) fieldError = true;
                                if(checkForError(errorsObject, "last_name", textViewLastName)) fieldError = true;
                                if(checkForError(errorsObject, "email", textViewMail)) fieldError = true;
                                if(checkForError(errorsObject, "password", textViewPassword)) fieldError = true;
                                if(checkForError(errorsObject, "referral_code", textViewReferralCode)) fieldError = true;
                                if(fieldError)
                                    return;
                                else
                                    message = errorsObject.getString("message");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (volleyError instanceof AuthFailureError) {
                        message = getString(R.string.error_incorrect_password_or_login);
                    } else if (volleyError instanceof ParseError) {
                        message = getString(R.string.error_parser);
                    } else if (volleyError instanceof TimeoutError) {
                        message = getString(R.string.error_timeout);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(getString(R.string.error_login));
                    builder.setMessage(message);
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            })
            .build();
        sc.execute();*/

        endFragment = RegisterEndFragment.newInstance();
        addFragmentToMainContent(endFragment, false, getString(R.string.id_end_fragment));
    }

    @Override
    public void goToWall(){
        Intent mainIntent = new Intent().setClass(RegisterActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void goToLogin(){
        Intent mainIntent = new Intent().setClass(RegisterActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private static boolean checkForError(JSONObject object, String tag, TextView textView){
        JSONObject errorsObject = null;
        try {
            errorsObject = object.getJSONObject("errors");
            if(errorsObject.has(tag)){
                JSONArray errorsArray = null;
                errorsArray = errorsObject.getJSONArray(tag);
                String errors = "";
                for(int i=0; i<errorsArray.length(); i++){
                    errors += errorsArray.getString(i);
                }
                textView.setError(errors);
                textView.requestFocus();
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

}
