package kiwigroup.yodelego;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

public class RegisterActivity extends AppCompatActivity implements OnRegisterFragmentListener{

    private RegisterMainFragment mainFragment;
    private RegisterStudentFragment studentFragment;
    private RegisterEndFragment endFragment;
    private Fragment currentFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainFragment = RegisterMainFragment.newInstance();
        addFragmentToMainContent(mainFragment, false, getString(R.string.id_main_fragment));

        /*RegisterStudentFragment studentFragment = RegisterStudentFragment.newInstance("", "", "", "", "");
        addFragmentToMainContent(studentFragment, false, getString(R.string.id_student_fragment));*/
    }

    @Override
    public boolean onSupportNavigateUp() {
        goToLogin();

        Fragment mainFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.id_main_fragment));
        if (mainFragment != null && mainFragment.isVisible()) {
            goToLogin();
        }
        Fragment studentFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.id_student_fragment));
        if (studentFragment != null && studentFragment.isVisible()) {
            getSupportFragmentManager().popBackStack();
        }
        Fragment registerEndFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.id_end_fragment));
        if (registerEndFragment != null && registerEndFragment.isVisible()) {
            goToLogin();
        }
        return true;
    }


    @Override
    public void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId) {
        if (fragment != null) {
            currentFragment = fragment;
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
    public void getEducationalInstitutions(final onEducationalInstitutionsListener listener) {
        Log.d("RegisterActivity", "-call: getEducationalInstitutions");
        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "/educational-institutions")
                .GET()
                .tokenized(false)
                .arrayReturnListener(new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response != null) {
                            Map<String, Integer> educational = new HashMap<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    Log.d("RegisterActivity", "-call: getEducationalInstitutions " + object.toString());
                                    int id = object.getInt("id");
                                    String name = object.getString("name");
                                    educational.put(name, id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onError(getString(R.string.error_json_exception));
                                    return;
                                }
                            }
                            listener.onEducationalInstitutionsResponse(educational);
                        }
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("getEducationalInstitut", "volleyError: " + volleyError.toString());
                        String message = "";
                        if (volleyError instanceof NetworkError) {
                            message = getString(R.string.error_network);
                        } else if (volleyError instanceof ServerError) {
                            if(volleyError.networkResponse != null){
                                try {
                                    JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                                    JSONObject errorsObject = responseObject.getJSONObject("error");
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
                        listener.onError(message);
                    }
                })
                .build();
        sc.execute();
    }

    @Override
    public void createAccount(boolean student,
                              String name,
                              String lastName,
                              String rut,
                              final String email,
                              final String password,
                              int university,
                              String career,
                              String year){
        HashMap<String, Object> args = new HashMap<>();
        args.put("first_name", name);
        args.put("last_name", lastName);
        args.put("rut", rut);
        args.put("educational_institution", university);
        args.put("email", email);
        args.put("password", password);
        args.put("enrollment_year", year);
        args.put("career", career);

        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "users/")
            .POST()
            .tokenized(false)
            .parameters(args)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("username", email);
                    editor.putString("password", password);
                    editor.apply();

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(getString(R.string.account_created_title));
                    builder.setMessage(getString(R.string.account_created));
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    endFragment = RegisterEndFragment.newInstance();
                                    addFragmentToMainContent(endFragment, false, getString(R.string.id_end_fragment));
                                }
                            });
                    builder.show();
                }
            })
            .errorListener(new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    /*signUpButton.setEnabled(true);
                    signUpButton.setAlpha(1f);
                    cancelButton.setEnabled(true);
                    cancelButton.setAlpha(1f);
                    showProgress(false);*/
                    Log.d("createAccount", "volleyError: " + volleyError.toString());

                    String message = "";
                    if (volleyError instanceof NetworkError) {
                        message = getString(R.string.error_network);
                    } else if (volleyError instanceof ServerError) {
                        if(volleyError.networkResponse != null){
                            Log.d("createAccount", "serverError: " + new String(volleyError.networkResponse.data));
                            try {
                                JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));

                                String firstNameError = checkForError(responseObject, "first_name");
                                String lastNameError = checkForError(responseObject, "last_name");
                                String rutError = checkForError(responseObject, "rut");
                                String emailError = checkForError(responseObject, "email");
                                String passwordError = checkForError(responseObject, "password");

                                if( firstNameError != null ||
                                    lastNameError != null ||
                                    rutError != null ||
                                    emailError != null ||
                                    passwordError != null) {
                                    getSupportFragmentManager().popBackStack();
                                    RegisterMainFragment mainFragment = (RegisterMainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.id_main_fragment));
                                    mainFragment.updateErrors(
                                            firstNameError,
                                            lastNameError,
                                            emailError,
                                            passwordError,
                                            rutError);

                                    return;
                                }

                                String firstEducational = checkForError(responseObject, "educational_institution");
                                String yearError = checkForError(responseObject, "enrollment_year");
                                String careerError = checkForError(responseObject, "career");

                                if( firstEducational == null ||
                                    yearError == null ||
                                    careerError == null){
                                    RegisterStudentFragment studentFragment = (RegisterStudentFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.id_student_fragment));
                                    studentFragment.updateErrors(firstEducational, yearError, careerError);
                                    return;
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        return;
                    } else if (volleyError instanceof AuthFailureError) {
                        message = getString(R.string.error_incorrect_password_or_login);
                    } else if (volleyError instanceof ParseError) {
                        message = getString(R.string.error_parser);
                    } else if (volleyError instanceof TimeoutError) {
                        message = getString(R.string.error_timeout);
                    }

                    AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
                    builder.setTitle(getString(R.string.error_creating_account));
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
        sc.execute();
    }

    @Override
    public void goToWall(){
        Intent mainIntent = new Intent().setClass(RegisterActivity.this, SplashActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void goToLogin(){
        Intent mainIntent = new Intent().setClass(RegisterActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private static String checkForError(JSONObject object, String tag){
        try {
            if(object.has(tag)){
                JSONArray errorsArray = null;
                errorsArray = object.getJSONArray(tag);
                String errors = "";
                for(int i=0; i<errorsArray.length(); i++){
                    errors += errorsArray.getString(i);
                }
                return errors;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface onEducationalInstitutionsListener {
        void onEducationalInstitutionsResponse(Map<String, Integer> response);
        void onError(String error);
    }

}
