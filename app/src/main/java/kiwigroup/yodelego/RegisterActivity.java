package kiwigroup.yodelego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import java.util.List;

import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

public class RegisterActivity extends AppCompatActivity implements OnRegisterFragmentListener{

    private RegisterMainFragment mainFragment;
    private RegisterStudentFragment studentFragment;
    private RegisterEndFragment endFragment;

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
                .POST()
                .tokenized(false)
                .arrayReturnListener(new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response != null) {
                            ArrayList<String> educational = new ArrayList<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    String name = object.getString("name");
                                    //String commerce_id = object.getString("commerce_id");
                                    Log.d("RegisterActivity", "-> -> name: " + name);
                                    educational.add(name);
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
    public void createAccount(boolean student, String name, String lastName, String rut, String email, String password, String university, String career, String semester){
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
        User user = new User();
        user.setName("Paulina");
        user.setLastName("Miranda");
        user.setEmail("paulina.miranda@yodelego.com");
        user.setRut("16.508.909-k");
        user.setEducationalInstitution("Universidad Técnica Federico Santa Maria");
        user.setCareer("Ingeniería Civil Informática");
        user.setEnrollmentYear(2009);

        mainIntent.putExtra("user", user);
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

    public interface onEducationalInstitutionsListener {
        void onEducationalInstitutionsResponse(List<String> response);
        void onError(String error);
    }

}
