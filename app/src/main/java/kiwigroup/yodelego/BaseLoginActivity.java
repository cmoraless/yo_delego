package kiwigroup.yodelego;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

public abstract class BaseLoginActivity extends AppCompatActivity {

    private String username;
    private String password;
    private User user;

    protected void startLoginProcess(final String username, final String password){
        this.username = username;
        this.password = password;

        HashMap<String, Object> args = new HashMap<>();
        args.put("username", username);
        args.put("password", password);
        ServerCommunication serverCommunication = new ServerCommunication.ServerCommunicationBuilder(this, "auth/")
                .POST()
                .tokenized(false)
                .parameters(args)
                .objectReturnListener(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (response != null) {
                            try {
                                String token = response.getString("token");
                                if (token != null) {
                                    ServerCommunication.setTOKEN(token);
                                    getUserProfile();
                                } else {
                                    onLoginError(getString(R.string.error_json_exception));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onLoginError(getString(R.string.error_json_exception));
                            }
                        } else {
                            Log.e("LoginActivity", "login server null response");
                        }
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        if (volleyError instanceof NetworkError) {
                            onLoginError(getString(R.string.error_network));
                        } else if (volleyError instanceof ServerError) {
                            try {
                                Log.d("startLoginProcess", "---> onErrorResponse " + new String(volleyError.networkResponse.data));
                                JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                                Log.d("startLoginProcess", "---> onErrorResponse " + responseObject.toString());
                                String genericError = "";
                                Iterator<String> iter = responseObject.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    String errors = "";
                                    JSONArray errorsArray = responseObject.getJSONArray(key);
                                    for(int i = 0; i < errorsArray.length(); i++){
                                        errors += " " + errorsArray.getString(i);
                                    }
                                    onLoginError(errors);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onLoginError(getString(R.string.error_json_exception));
                            }
                        } else if (volleyError instanceof AuthFailureError) {
                            onLoginFieldError("username", getString(R.string.error_incorrect_password_or_login));
                        } else if (volleyError instanceof ParseError) {
                            onLoginError(getString(R.string.error_parser));
                        } else if (volleyError instanceof TimeoutError) {
                            onLoginError(getString(R.string.error_timeout));
                        }
                    }
                })
                .build();
        serverCommunication.execute();
    }

    protected void getUserProfile(){
        ServerCommunication userSC = new ServerCommunication.ServerCommunicationBuilder(BaseLoginActivity.this, "profile/")
                .GET()
                .tokenized(true)
                .objectReturnListener(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null) {
                            Log.e("LoginActivity", "SCUser response " + response.toString());
                            try {

                                user = User.parseFromJson(response);

                                SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("username", username);
                                editor.putString("password", password);
                                editor.apply();

                                onLoginSuccess(user);

                            } catch (Exception e) {
                                e.printStackTrace();
                                onUserInfoError(getString(R.string.error_json_exception));
                            }
                        } else {
                            onUserInfoError("server user null response");
                        }
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        if (volleyError instanceof NetworkError) {
                            onUserInfoError(getString(R.string.error_network));
                        } else if (volleyError instanceof ServerError) {
                            try {
                                JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                                Log.d("startLoginProcess", "---> onErrorResponse " + responseObject.toString());
                                String genericError = "";
                                Iterator<String> iter = responseObject.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    String errors = "";
                                    JSONArray errorsArray = responseObject.getJSONArray(key);
                                    for(int i = 0; i < errorsArray.length(); i++){
                                        errors += " " + errorsArray.getString(i);
                                    }
                                    onLoginError(errors);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onUserInfoError(getString(R.string.error_json_exception));
                            }
                        } else if (volleyError instanceof AuthFailureError) {
                            onUserInfoError(getString(R.string.error_incorrect_password_or_login));
                        } else if (volleyError instanceof ParseError) {
                            onUserInfoError(getString(R.string.error_parser));
                        } else if (volleyError instanceof TimeoutError) {
                            onUserInfoError(getString(R.string.error_timeout));
                        }
                    }
                })
                .build();
        userSC.execute();
    }

    protected abstract void onLoginError(String errorMessage);
    protected abstract void onLoginFieldError(String tag, String errorMessage);
    protected abstract void onUserInfoError(String errorMessage);
    protected abstract void onLoginSuccess(User user);

}
