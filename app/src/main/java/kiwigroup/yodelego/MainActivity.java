package kiwigroup.yodelego;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.NotificationResume;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.model.WallItem;
import kiwigroup.yodelego.server.ServerCommunication;
import kiwigroup.yodelego.services.NotificationsListenerService;

public class MainActivity
    extends
        AppCompatActivity
    implements
        OnUserFragmentsListener,
        NotificationsListenerService.NotificationListener {

    private User user;
    private NotificationsListenerService notificationService;
    private BottomNavigationView bottomNavigationView;
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            NotificationsListenerService.LocalBinder binder = (NotificationsListenerService.LocalBinder) service;
            notificationService = binder.getServiceInstance();
            notificationService.addListener(MainActivity.this);
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            notificationService = null;
        }
    };

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

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    Fragment editProfileFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.id_profile_edit));
                    if (editProfileFragment != null && editProfileFragment.isVisible() && !alreadyAsked) {
                        handleProfileEditionBack(item.getItemId());
                        return false;
                    } else {
                        alreadyAsked = false;
                        displayView(item.getItemId());
                        return true;
                    }
                }
            });
        displayView(R.id.action_wall);
        startPolling();
    }

    boolean alreadyAsked = false;

    public void handleProfileEditionBack(final int viewId) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edición de perfil");
            builder.setMessage("¿Quieres volver sin guardar los cambios?");
            builder.setPositiveButton("Sí",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        alreadyAsked = true;
                        bottomNavigationView.setSelectedItemId(viewId);
                    }
                });
            builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
            builder.show();
    }

    private void displayView(int viewId){
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
        askAddFragmentToMainContent(fragment, addToBackStack, fragmentId);
    }

    @Override
    public void askAddFragmentToMainContent(final Fragment fragment, final boolean addToBackStack, final String fragmentId) {
        if (fragment != null) {
            addFragmentToMainContent(fragment, addToBackStack, fragmentId);
        }
    }

    private void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(addToBackStack)
            ft.addToBackStack(fragmentId);
        //ft.setCustomAnimations(R.anim.enter_from_right, R.anim.exit_to_left, R.anim.enter_from_left, R.anim.exit_to_right);
        ft.replace(R.id.content_main, fragment, fragmentId);
        ft.commit();
        //setFragmentTitle(fragmentId);
    }

    @Override
    public void closeSession() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Cerrar sesión");
        builder.setMessage("¿Estas seguro que deseas cerrar tu sesión?");

        builder.setPositiveButton("Sí",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.clear();
                        editor.apply();

                        ServerCommunication.setTOKEN("");

                        Intent intent = new Intent();
                        intent.setClass(MainActivity.this, NotificationsListenerService.class);
                        stopService(intent);

                        Intent loginIntent = new Intent().setClass(MainActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }

    @Override
    public void getEducationalInstitutions(final RegisterActivity.OnEducationalInstitutionsListener listener) {
        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "/educational-institutions")
            .GET()
            .tokenized(false)
            .arrayReturnListener(new Response.Listener<JSONArray> (){
                @Override
                public void onResponse(JSONArray response) {
                    if(response != null) {
                        LinkedHashMap<String, Integer> educational = new LinkedHashMap<>();
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

    @Override
    public void getCareerCategories(final RegisterActivity.OnCareerCategoriesListener listener) {
        Log.d("RegisterActivity", "-call: getCareerCategories");
        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "/career-categories")
                .GET()
                .tokenized(false)
                .arrayReturnListener(new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response != null) {
                            LinkedHashMap<String, Integer> categories = new LinkedHashMap<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    Log.d("RegisterActivity", "-call: getCareerCategories " + object.toString());
                                    int id = object.getInt("id");
                                    String name = object.getString("name");
                                    categories.put(name, id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onError(getString(R.string.error_json_exception));
                                    return;
                                }
                            }
                            listener.onCareerCategoriesResponse(categories);
                        }
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("RegisterActivity", "volleyError: " + volleyError.toString());
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
    public void onBackPressed() {
        Fragment editProfileFragment = getSupportFragmentManager().findFragmentByTag(getString(R.string.id_profile_edit));
        if (editProfileFragment != null && editProfileFragment.isVisible()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Edición de perfil");
            builder.setMessage("¿Quieres volver sin guardar los cambios?");

            builder.setPositiveButton("Sí",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                           MainActivity.super.onBackPressed();
                        }
                    });
            builder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            builder.show();
        } else {
            super.onBackPressed();
        }

    }

    private OnWallUpdateListener listener;
    private List<WallItem> wallOffers;
    private List<Offer> myApplications;
    private String next;
    private NotificationResume notificationResume;
    private List<kiwigroup.yodelego.model.Notification> notifications;
    private boolean wallEnded;

    @Override
    public void getWallItems(final OnWallUpdateListener listener) {
        this.listener = listener;
        getWallItemsForListener();
        if(notificationResume != null)
            listener.onNotificationResponse(notificationResume);
    }

    @Override
    public void getMoreWallItems() {
        if(!wallEnded && wallOffers != null){
            listener.onLoadingWallItems();
            getWallItemsFromServer(listener, myApplications);
        }
    }

    @Override
    public void refreshWall(final OnWallUpdateListener listener) {
        this.listener = listener;
        listener.cleanWall();
        wallOffers = null;
        myApplications = null;
        wallEnded = false;
        next = null;
        getWallItems(listener);
    }

    private void getWallItemsForListener() {
        if(listener == null)
            return;

        listener.cleanWall();
        if(myApplications == null){
            getMyApplications(new OnApplicationUpdateListener() {
                @Override
                public void onApplicationsResponse(List<Offer> applications) {
                    myApplications = applications;
                    returnWallItems(applications);
                }

                @Override
                public void onApplicationError(String error) {

                }
            }, true);
        } else {
            returnWallItems(myApplications);
        }
    }

    private void returnWallItems(List<Offer> applications){
        if(wallOffers == null){
            listener.onLoadingWallItems();
            wallOffers = new ArrayList<>();
        }
        if(wallOffers.size() > 0){
            listener.onWallItemsResponse(wallOffers);
            listener.onApplicationsResponse(applications);
        } else {
            getWallItemsFromServer(listener, applications);
        }
    }

    private void getWallItemsFromServer(final OnWallUpdateListener listener, final List<Offer> myApplications) {
        ServerCommunication userSC = new ServerCommunication.ServerCommunicationBuilder(
            MainActivity.this,
            next == null ? "offers/" : next)
                .GET()
                .tokenized(true)
                .objectReturnListener(new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null) {
                            try {
                                Log.d("MainActivity", "**** WALL: " + response.toString());
                                Log.d("MainActivity", "**** myApplications: " + myApplications.size());
                                String url = response.getString("next");
                                if(response.isNull("next")){
                                    wallEnded = true;
                                } else if(url.contains("offers")) {
                                    next = url.substring(url.indexOf("offers"));
                                }

                                JSONArray array = response.getJSONArray("results");
                                ArrayList<WallItem> newWallOffers = new ArrayList<>();
                                for (int i = 0; i < array.length() ; i++) {
                                    Log.d("MainActivity", "**** WALL ITEM: " + i);
                                    JSONObject object = array.getJSONObject(i);
                                    Offer offer = Offer.parseFromJson(object);
                                    for(Offer application : myApplications){
                                        if(application.getId() == offer.getId()){
                                            offer.setApplied(true);
                                            offer.setApplication(application.getApplication());
                                        }
                                    }
                                    newWallOffers.add(offer);
                                }
                                if(wallOffers != null){
                                    listener.onWallItemsResponse(newWallOffers);
                                    listener.onApplicationsResponse(myApplications);
                                    wallOffers.addAll(newWallOffers);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
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
        Log.d("MAINACTIVITY", "*** getMyApplications");
        if(myApplications == null){
            myApplications = new ArrayList<>();
        }

        if(forceReload || myApplications.size() == 0){
            if(MyApplicationsWS == null){
                MyApplicationsWS = new ServerCommunication.ServerCommunicationBuilder(MainActivity.this, "applications/")
                    .GET()
                    .tokenized(true)
                    .arrayReturnListener(new Response.Listener<JSONArray> () {
                        @Override
                        public void onResponse(JSONArray response) {
                            if (response != null) {
                                Log.d("MainActivity", "**** applications response: " + response.toString());
                                myApplications.clear();
                                if(response.length() > 0){
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            Application application = Application.parseFromJson(response.getJSONObject(i));
                                            getOfferForApplication(application, mOnApplicationUpdateListener, response.length());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                } else {
                                    mOnApplicationUpdateListener.onApplicationsResponse(myApplications);
                                }
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
    public void onWallOfferSelected(Offer offer) {
        Log.d("MainActivity", "******* onWallOfferSelected. offer.isApplied(): " + offer.isApplied());
        Intent mainIntent = new Intent().setClass(MainActivity.this, OfferDetailsActivity.class);
        mainIntent.putExtra("offer", offer);
        startActivityForResult(mainIntent, 1);
    }

    @Override
    public void onApplicationSelected(Offer offer) {
        Log.d("MainActivity", "******* onApplicationSelected. offer.isApplied(): " + offer.isApplied());
        Intent mainIntent = new Intent().setClass(MainActivity.this, OfferDetailsActivity.class);
        mainIntent.putExtra("offer", offer);
        startActivityForResult(mainIntent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if(resultCode == Activity.RESULT_OK){
                Log.d("MAIN", "****  Activity.RESULT_OK");
                refreshWall(listener);
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d("MAIN", "****  Activity.RESULT_CANCELED");
            }
        }
    }

    private void getOfferForApplication(final Application application,
                                        final OnApplicationUpdateListener mOnApplicationUpdateListener,
                                        final int expectedAmount){
        ServerCommunication serverCommunication = new ServerCommunication.ServerCommunicationBuilder(
                MainActivity.this,
                String.format(Locale.US,"offers/%d/", application.getOfferId()))
            .GET()
            .tokenized(true)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Offer offer = Offer.parseFromJson(response);
                        offer.setApplied    (true);
                        offer.setApplication(application);
                        myApplications.add(offer);

                        if (myApplications.size() == expectedAmount)
                            mOnApplicationUpdateListener.onApplicationsResponse(myApplications);

                    }
                }
            })
            .errorListener(new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   error.printStackTrace();
               }
            }).build();
        serverCommunication.execute();
    }

    @Override
    public void updateUser() {
        ServerCommunication userSC = new ServerCommunication.ServerCommunicationBuilder(MainActivity.this, "profile/")
            .GET()
            .tokenized(true)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response != null) {
                        Log.e("LoginActivity", "SCUser response " + response.toString());
                        try {
                            user = new User();
                            user.setName(response.getString("first_name"));
                            user.setLastName(response.getString("last_name"));
                            user.setEmail(response.getString("email"));

                            if(response.has("rut") && !response.isNull("rut") && !response.getString("rut").isEmpty())
                                user.setRut(response.getString("rut"));

                            if(response.has("phone_number") && !response.isNull("phone_number") && !response.getString("phone_number").isEmpty())
                                user.setPhone(response.getString("phone_number"));

                            if(response.has("educational_institution") && !response.isNull("educational_institution") && !response.getString("educational_institution").isEmpty())
                                user.setEducationalInstitution(response.getString("educational_institution"));

                            if(response.has("career") && !response.isNull("career") && !response.getString("career").isEmpty())
                                user.setCareer(response.getString("career"));

                            if(response.has("enrollment_year") && !response.isNull("enrollment_year") && !response.getString("enrollment_year").isEmpty())
                                user.setEnrollmentYear(Integer.parseInt(response.getString("enrollment_year")));

                            if(response.has("bank") && !response.isNull("bank") && !response.getString("bank").isEmpty())
                                user.setBank(response.getString("bank"));

                            if(response.has("bank_account_kind") && !response.isNull("bank_account_kind"))
                                user.setAccountType(response.getInt("bank_account_kind"));

                            if(response.has("bank_account_number") && !response.isNull("bank_account_number") && !response.getString("bank_account_number").isEmpty())
                                user.setAccountNumber(response.getString("bank_account_number"));

                            if(response.has("bank_account_kind") && !response.isNull("bank_account_kind"))
                                user.setAccountType(response.getInt("bank_account_kind"));

                            if(response.has("publisher_rating") && !response.isNull("publisher_rating") && !response.getString("publisher_rating").isEmpty())
                                user.setAccountNumber(response.getString("publisher_rating"));

                            if(!response.isNull("publisher_rating")){
                                try {
                                    user.setPublisherRating(BigDecimal.valueOf(response.getDouble("publisher_rating")).floatValue());
                                } catch(Exception ex){
                                    ex.printStackTrace();
                                    user.setPublisherRating(-1.0f);
                                }
                            }

                            if(!response.isNull("applicant_rating")){
                                try {
                                    user.setApplicantRating(BigDecimal.valueOf(response.getDouble("applicant_rating")).floatValue());
                                } catch(Exception ex){
                                    ex.printStackTrace();
                                    user.setApplicantRating(-1.0f);
                                }
                            }

                            Log.e("LoginActivity", "SCUser getName " + user.getName());
                            Log.e("LoginActivity", "SCUser getLastName " + user.getLastName());
                            Log.e("LoginActivity", "SCUser getSemesters " + user.getSemesters());
                            Log.e("LoginActivity", "SCUser getCareer " + user.getCareer());
                            Log.e("LoginActivity", "SCUser getEducationalInstitution " + user.getEducationalInstitution());
                            Log.e("LoginActivity", "SCUser getEmail " + user.getEmail());
                            Log.e("LoginActivity", "SCUser getRut " + user.getRut());
                            Log.e("LoginActivity", "SCUser getEnrollmentYear " + user.getEnrollmentYear());
                            Log.e("LoginActivity", "SCUser applicant_rating " + user.getApplicantRating());
                            Log.e("LoginActivity", "SCUser publisher_rating " + user.getPublisherRating());

                            ProfileFragment mainFragment = (ProfileFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.id_profile));
                            if(mainFragment != null){
                                mainFragment.updateUser(user);
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                    }
                }
            })
            .errorListener(new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    if (volleyError instanceof NetworkError) {
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
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof AuthFailureError) {
                    } else if (volleyError instanceof ParseError) {
                    } else if (volleyError instanceof TimeoutError) {
                    }
                }
            })
            .build();
        userSC.execute();
    }

    @Override
    public void closeNotifications() {
        notificationResume = null;
    }

    @Override
    public void onNotificationSelected() {
        //displayView(R.id.action_applications);
        bottomNavigationView.setSelectedItemId(R.id.action_applications);

    }

    private void startPolling(){
        /*Calendar cal = Calendar.getInstance();
        AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        long interval = 1000 * 5;
        Intent serviceIntent = new Intent(this, NotificationsListenerService.class);
        PendingIntent servicePendingIntent =
            PendingIntent.getService(
                this,
                2497, // integer constant used to identify the service
                serviceIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);  // FLAG to avoid creating a second service if there's already one running
        // there are other options like setInexactRepeating, check the docs
        am.setRepeating(
            AlarmManager.RTC_WAKEUP,//type of alarm. This one will wake up the device when it goes off, but there are others, check the docs
            cal.getTimeInMillis(),
            interval,
            servicePendingIntent
        );*/
        Intent serviceIntent = new Intent(this, NotificationsListenerService.class);
        startService(serviceIntent);
        bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void notification(List<kiwigroup.yodelego.model.Notification> notifications) {
        List<kiwigroup.yodelego.model.Notification> newNotifications = new ArrayList<>();
        if(this.notifications != null){
            for(kiwigroup.yodelego.model.Notification savedNotification : this.notifications){
                boolean delete = false;
                for(kiwigroup.yodelego.model.Notification notification : notifications){
                    if(notification.getId() == savedNotification.getId())
                        delete = true;
                }
                if(!delete)
                    newNotifications.add(savedNotification);
            }
        }
        newNotifications.addAll(notifications);
        this.notifications = newNotifications;

        if(this.notifications.size() == 0){
            notificationResume = null;
            return;
        }

        int accepted_offers = 0;
        int rejected_offers = 0;
        for(kiwigroup.yodelego.model.Notification notification : this.notifications){
            // Offer accepted
            if (notification.getKind() == 1){
                accepted_offers ++;
            // Application rejected
            } else if (notification.getKind() == 2){
                rejected_offers ++;
            }
        }

        if(accepted_offers > 0 && rejected_offers > 0){
            this.notificationResume = new NotificationResume(String.format(Locale.US, "Tienes %d postulaciones adjudicadas y %d rechazadas", accepted_offers, rejected_offers));
        } else if (accepted_offers > 0) {
            this.notificationResume = new NotificationResume(String.format(Locale.US, "Tienes %d postulaciones adjudicadas", accepted_offers));
        } else if (rejected_offers > 0) {
            this.notificationResume = new NotificationResume(String.format(Locale.US, "Tienes %d postulaciones rechazadas", rejected_offers));
        } else
            return;

        notificationResume.setAcceptedOffers(accepted_offers);
        notificationResume.setRejectedOffers(rejected_offers);

        if(listener!= null)
            listener.onNotificationResponse(notificationResume);

        refreshWall(listener);
        /*getMyApplications(new OnApplicationUpdateListener() {
            @Override
            public void onApplicationsResponse(List<Offer> applications) {
                myApplications = applications;
                returnWallItems(applications);
            }

            @Override
            public void onApplicationError(String error) {

            }
        }, true);*/

    }
}
