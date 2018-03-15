package kiwigroup.yodelego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;
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
                        Map<String, Integer> educational = new HashMap<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                String name = object.getString("name");
                                int id = object.getInt("id");
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

    private OnWallUpdateListener listener;
    private List<Offer> wallOffers;
    private List<Application> myApplications;

    @Override
    public void getWallItems(final OnWallUpdateListener listener) {
        this.listener = listener;
        if(myApplications == null){
            getMyApplications(new OnApplicationUpdateListener() {
                @Override
                public void onApplicationsResponse(List<Application> applications) {
                    myApplications = applications;
                    if(wallOffers == null){
                        listener.onLoadingWallItems();
                        wallOffers = new ArrayList<>();
                    }
                    if(wallOffers.size() > 0){
                        listener.onWallItemsResponse(wallOffers);
                    } else {
                        getWallItemsFromServer(listener, applications);
                    }
                }

                @Override
                public void onApplicationError(String error) {

                }
            }, true);
        } else {
            if(wallOffers == null){
                listener.onLoadingWallItems();
                wallOffers = new ArrayList<>();
            }
            if(wallOffers.size() > 0){
                listener.onWallItemsResponse(wallOffers);
            } else {
                getWallItemsFromServer(listener, myApplications);
            }
        }
    }

    @Override
    public void getMoreWallItems() {
        listener.onLoadingWallItems();
        getWallItemsFromServer(listener, myApplications);
    }

    private void getWallItemsFromServer(final OnWallUpdateListener listener, final List<Application> myApplications) {
        /*final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                List<Offer> newOffers = new ArrayList<>();
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = df.parse("28/01/2018");
                    date2 = df.parse("04/01/2018");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Offer offer = new Offer(
                        "Daniela Villalobos",
                        date1,
                        "Daniela Villalobos publicó un nuevo trabajo",
                        true,
                        "Toma de inventarios",
                        15000,
                        "Se necesita personal para toma de inventarios, son 3 noches consecutivas en el Mall Marina Arauco ubicado en 15 norte, Viña del Mar.",
                        "15 norte 123234, Viña del Mar",
                        "28/01/2018",
                        Offer.OfferStatus.OPEN
                );
                Offer offer3 = new Offer(
                        "Lily Anguita",
                        date2,
                        "Lily Anguita publicó un nuevo trabajo",
                        true,
                        "Toma de inventarios",
                        15000,
                        "Se necesita personal para toma de inventarios, son 3 noches consecutivas en el Mall Marina Arauco ubicado en 15 norte, Viña del Mar.",
                        "15 norte 123234, Viña del Mar",
                        "28/01/2018",
                        Offer.OfferStatus.OPEN
                );
                newOffers.add(offer);
                newOffers.add(offer3);
                wallOffers.addAll(newOffers);
                listener.onWallItemsResponse(newOffers);
            }
        }, 3000);*/

        ServerCommunication userSC = new ServerCommunication.ServerCommunicationBuilder(MainActivity.this, "offers/")
            .GET()
            .tokenized(true)
            .arrayReturnListener(new Response.Listener<JSONArray> (){
                @Override
                public void onResponse(JSONArray response) {
                    if(response != null) {
                        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.US);
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                Offer offer = new Offer();

                                Log.d("RegisterActivity", "->-> offers response : " + object.toString());

                                offer.setId(object.getLong("id"));
                                offer.setTitle(object.getString("title"));
                                offer.setSummary(object.getString("description"));
                                offer.setPublisher(object.getString("publisher"));
                                offer.setDate(df.parse(object.getString("created_at")));

                                if(!object.isNull("daily_wage"))
                                    offer.setDailyWage(object.getInt("daily_wage"));
                                if(!object.isNull("hourly_wage"))
                                    offer.setHourlyWage(object.getInt("hourly_wage"));
                                if(!object.isNull("total_wage"))
                                    offer.setTotalWage(object.getInt("total_wage"));

                                offer.setStatus(Offer.OfferStatus.fromInteger(object.getInt("status")));

                                for(Application application : myApplications){
                                    if(application.getId() == offer.getId()){
                                        offer = application;
                                    }
                                }
                                wallOffers.add(offer);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    listener.onWallItemsResponse(wallOffers);
                }
            })
            .errorListener(new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                }
            })
            .build();
        userSC.execute();
    }

    private ServerCommunication MyApplicationsWS;

    @Override
    public void getMyApplications(final OnApplicationUpdateListener mOnApplicationUpdateListener,  boolean forceReload) {

        if(myApplications == null){
            myApplications = new ArrayList<>();
        }

        if(forceReload || myApplications.size() == 0){
            /*final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    DateFormat df = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
                    Date date1 = null;
                    Date date2 = null;
                    try {
                        date1 = df.parse("28/01/2018");
                        date2 = df.parse("04/01/2018");
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    myApplications.clear();
                    Application application = new Application(
                            "Daniela Villalobos",
                            date1,
                            "Daniela Villalobos publicó un nuevo trabajo",
                            true,
                            "Toma de inventarios",
                            15000,
                            "Se necesita personal para toma de inventarios, son 3 noches consecutivas en el Mall Marina Arauco ubicado en 15 norte, Viña del Mar.",
                            "15 norte 123234, Viña del Mar",
                            "28/01/2018",
                            Offer.OfferStatus.ADJUDICATED
                    );
                    Application application2 = new Application(
                            "Lily Anguita",
                            date2,
                            "Lily Anguita publicó un nuevo trabajo",
                            true,
                            "Toma de inventarios",
                            15000,
                            "Se necesita personal para toma de inventarios, son 3 noches consecutivas en el Mall Marina Arauco ubicado en 15 norte, Viña del Mar.",
                            "15 norte 123234, Viña del Mar",
                            "28/01/2018",
                            Offer.OfferStatus.CLOSED
                    );
                    Application application3 = new Application(
                            "Lily Anguita",
                            date2,
                            "Lily Anguita publicó un nuevo trabajo",
                            true,
                            "Toma de inventarios",
                            15000,
                            "Se necesita personal para toma de inventarios, son 3 noches consecutivas en el Mall Marina Arauco ubicado en 15 norte, Viña del Mar.",
                            "15 norte 123234, Viña del Mar",
                            "28/01/2018",
                            Offer.OfferStatus.OPEN
                    );
                    Application application4 = new Application(
                            "Lily Anguita",
                            date2,
                            "Lily Anguita publicó un nuevo trabajo",
                            true,
                            "Toma de inventarios",
                            15000,
                            "Se necesita personal para toma de inventarios, son 3 noches consecutivas en el Mall Marina Arauco ubicado en 15 norte, Viña del Mar.",
                            "15 norte 123234, Viña del Mar",
                            "28/01/2018",
                            Offer.OfferStatus.REVIEWED
                    );
                    myApplications.add(application);
                    myApplications.add(application2);
                    myApplications.add(application3);
                    myApplications.add(application4);
                    mOnApplicationUpdateListener.onApplicationsResponse(myApplications);
                }
            }, 3000);*/
            Log.d("RegisterActivity", "->-> get applications");
            if(MyApplicationsWS == null){
                MyApplicationsWS = new ServerCommunication.ServerCommunicationBuilder(MainActivity.this, "applications/")
                    .GET()
                    .tokenized(true)
                    .arrayReturnListener(new Response.Listener<JSONArray> () {
                        @Override
                        public void onResponse(JSONArray response) {
                            if (response != null) {
                                Log.d("RegisterActivity", "->-> applications response : " + response.toString());
                                DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.US);
                                myApplications.clear();

                                for (int i = 0; i < response.length(); i++) {
                                    try {
                                        JSONObject object = response.getJSONObject(i);

                                        Application postulation = new Application();

                                        /*postulation.setId(object.getLong("id"));
                                        postulation.setTitle(object.getString("title"));
                                        postulation.setSummary(object.getString("description"));
                                        postulation.setPublisher(object.getString("publisher"));
                                        postulation.setDate(df.parse(object.getString("created_at")));

                                        if(!object.isNull("daily_wage"))
                                            postulation.setDailyWage(object.getInt("daily_wage"));
                                        if(!object.isNull("hourly_wage"))
                                            postulation.setHourlyWage(object.getInt("hourly_wage"));
                                        if(!object.isNull("total_wage"))
                                            postulation.setTotalWage(object.getInt("total_wage"));

                                        postulation.setStatus(Offer.OfferStatus.fromInteger(object.getInt("status")));

                                        wallOffers.add(postulation);*/

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                mOnApplicationUpdateListener.onApplicationsResponse(myApplications);
                            }
                            MyApplicationsWS = null;
                        }
                    })
                    .errorListener(new Response.ErrorListener() {
                           @Override
                           public void onErrorResponse(VolleyError error) {
                               error.printStackTrace();
                               MyApplicationsWS = null;
                           }
                       }
                    ).build();
                MyApplicationsWS.execute();
            }
        } else {
            mOnApplicationUpdateListener.onApplicationsResponse(myApplications);
        }
    }

    @Override
    public void onListFragmentInteraction(Application application) {

    }
}
