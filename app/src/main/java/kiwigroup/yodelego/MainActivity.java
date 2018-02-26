package kiwigroup.yodelego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

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

import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

public class MainActivity extends AppCompatActivity implements OnUserFragmentsListener {

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle bundle = getIntent().getExtras();
        user = (User) bundle.getSerializable("user");

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    displayView(item.getItemId());
                    return true;
                }
            });
        displayView(R.id.action_wall);
    }

    public void displayView(int viewId) {
        Fragment fragment = null;
        String fragmentId = "";
        boolean addToBackStack = false;
        switch (viewId) {
            case R.id.action_wall:
                fragment = WallFragment.newInstance(user);
                fragmentId = getString(R.string.id_wall);
                addToBackStack = false;
                break;
            case R.id.action_applications:
                fragment = ApplicationsFragment.newInstance(user);
                fragmentId = getString(R.string.id_applications);
                addToBackStack = false;
                break;
            case R.id.action_profile:
                fragment = ProfileFragment.newInstance(user);
                fragmentId = getString(R.string.id_profile);
                addToBackStack = false;
                break;

        }
        addFragmentToMainContent(fragment, addToBackStack, fragmentId);
    }

    @Override
    public void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            if(addToBackStack)
                ft.addToBackStack(fragmentId);
            //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
            ft.replace(R.id.content_main, fragment, fragmentId);
            ft.commit();
            //setFragmentTitle(fragmentId);
        }
    }

    @Override
    public void closeSession() {
        SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
        Intent loginIntent = new Intent().setClass(MainActivity.this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    @Override
    public void getEducationalInstitutions(final RegisterActivity.onEducationalInstitutionsListener listener) {
        Log.d("MinActivity", "-call: getEducationalInstitutions");
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
}
