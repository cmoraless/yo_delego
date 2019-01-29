package kiwigroup.yodelego;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.MediaStore;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.StatusNotification;
import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.model.WallItem;
import kiwigroup.yodelego.server.ServerCommunication;
import kiwigroup.yodelego.services.NotificationsListenerService;

import static kiwigroup.yodelego.model.Offer.OfferStatus.CANCELED;
import static kiwigroup.yodelego.model.Offer.OfferStatus.DEACTIVATED;
import static kiwigroup.yodelego.model.Offer.OfferStatus.PAUSED;
import static kiwigroup.yodelego.services.NotificationsListenerService.ACTION_START_SERVICE;

public class MainActivity
    extends
        AppCompatActivity
    implements
        OnUserFragmentsListener,
        NotificationsListenerService.NotificationListener {

    public static final int PICK_IMAGE = 888;
    public static final int APPLICATION_MODIFIED = 432;

    private User user;
    private NotificationsListenerService notificationService;
    private BottomNavigationView bottomNavigationView;

    private boolean mShouldUnbind;
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

        ServerCommunication.setTOKEN(user.getToken());

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
        bindPollingService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        doUnbindService();
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

    private final List<OnWallUpdateListener> listeners = new ArrayList<>();
    private List<WallItem> wallOffers;
    private List<Offer> myApplications;
    private String next;
    private List<StatusNotification> statusNotifications;
    private boolean wallEnded;

    @Override
    public void addWallUpdateListener(OnWallUpdateListener listener) {
        synchronized(listeners){
            listeners.add(listener);
        }
    }

    @Override
    public void removeWallUpdateListener(OnWallUpdateListener listener) {
        synchronized(listeners) {
            listeners.remove(listener);
        }
    }

    @Override
    public void getWallItems() {
        getWallItemsForListener();
        if(statusNotifications != null && statusNotifications.size() > 0)
            synchronized(listeners) {
                for(OnWallUpdateListener listener : listeners)
                    if(listener != null)
                        listener.onNotificationResponse(statusNotifications);
            }
    }

    private void getWallItemsForListener() {
        if(myApplications == null){
            getMyApplications(new OnApplicationUpdateListener() {
                @Override
                public void onApplicationsResponse(List<Offer> applications) {
                    myApplications = applications;
                    returnWallItems(myApplications);
                }

                @Override
                public void onApplicationError(String error) {
                    synchronized(listeners) {
                        for(OnWallUpdateListener listener : listeners)
                            if(listener != null)
                                listener.onApplicationsResponse(new ArrayList<Offer>());
                    }
                }
            }, true);
        } else {
            returnWallItems(myApplications);
        }
    }

    private void returnWallItems(List<Offer> applications){
        synchronized(listeners) {
            for(OnWallUpdateListener listener : listeners)
                if(listener != null)
                    listener.cleanWall();
        }

        if(wallOffers == null){
            synchronized(listeners) {
                for(OnWallUpdateListener listener : listeners)
                    if(listener != null)
                        listener.onLoadingWallItems();
            }
            wallOffers = new ArrayList<>();
        }
        if(wallOffers.size() > 0){
            synchronized(listeners) {
                for(OnWallUpdateListener listener : listeners)
                    if(listener != null) {
                        listener.onWallItemsResponse(wallOffers);
                        listener.onApplicationsResponse(applications);
                    }
            }
        } else {
            getWallItemsFromServer(applications);
        }
    }

    private void getWallItemsFromServer(final List<Offer> myApplications) {
        ServerCommunication userSC = new ServerCommunication.ServerCommunicationBuilder(
                MainActivity.this,
                next == null ? "offers/" : next)
                .GET()
                .tokenized(true)
                .objectReturnListener(new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        if(response != null) {
                            Log.d("MainActivity", "**** WALL: " + response.toString());
                            try {
                                //Log.d("MainActivity", "**** myApplications: " + myApplications.size());
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
                                    for(Offer savedOffer : myApplications){
                                        if(savedOffer.getId() == offer.getId()){
                                            offer = savedOffer;
                                        }
                                    }
                                    // update WallAdapter if this status are supported
                                    if(!(offer.getStatus() == CANCELED ||
                                            offer.getStatus() == PAUSED ||
                                            offer.getStatus() == DEACTIVATED))
                                        newWallOffers.add(offer);
                                }
                                if(wallOffers != null){
                                    synchronized(listeners) {
                                        for(OnWallUpdateListener listener : listeners)
                                            if(listener != null) {
                                                listener.onWallItemsResponse(newWallOffers);
                                                listener.onApplicationsResponse(myApplications);
                                            }
                                    }
                                    wallOffers.addAll(newWallOffers);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            synchronized(listeners) {
                                for(OnWallUpdateListener listener : listeners)
                                    if(listener != null) {
                                        listener.onWallItemsResponse(new ArrayList<WallItem>());
                                        listener.onApplicationsResponse(new ArrayList<Offer>());
                                    }
                            }
                        }
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        synchronized(listeners) {
                            for(OnWallUpdateListener listener : listeners)
                                if(listener != null) {
                                    listener.onWallItemsResponse(new ArrayList<WallItem>());
                                    listener.onApplicationsResponse(new ArrayList<Offer>());
                                }
                        }
                    }
                })
                .build();
        userSC.execute();
    }

    @Override
    public void getMoreWallItems() {
        if(!wallEnded && wallOffers != null){
            synchronized(listeners) {
                for(OnWallUpdateListener listener : listeners)
                    if(listener != null)
                        listener.onLoadingWallItems();
            }
            getWallItemsFromServer(myApplications);
        }
    }

    @Override
    public void refreshWall() {
        synchronized(listeners) {
            for(OnWallUpdateListener listener : listeners)
                if(listener != null)
                    listener.cleanWall();
        }
        wallOffers = null;
        myApplications = null;
        wallEnded = false;
        next = null;

        getWallItems();
    }

    private ServerCommunication MyApplicationsWS;

    @Override
    public void getMyApplications(final OnApplicationUpdateListener mOnApplicationUpdateListener,  boolean forceReload) {
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
                                //Log.d("MainActivity", "**** applications response: " + response.toString());
                                myApplications.clear();
                                if(response.length() > 0){
                                    for (int i = 0; i < response.length(); i++) {
                                        try {
                                            Application application = Application.parseFromJson(response.getJSONObject(i));

                                            Offer offer = Offer.parseFromJson(response.getJSONObject(i).getJSONObject("offer"));
                                            offer.setAppliedByMe(true);
                                            offer.setApplication(application);

                                            //
                                            // CLOSE AND COMPLETE STATES LOGIC FOR APPLICATIONS
                                            //
                                            if(offer.isPaid()){
                                                if(offer.hasFinished()) {
                                                    Calendar cal = Calendar.getInstance();
                                                    cal.add(Calendar.DAY_OF_MONTH, 7);

                                                    if (application.wasReviewedByApplicantAndPublisher()) {
                                                        application.setQualifiable(false); // cerrado
                                                        application.setClosed(true);
                                                    } else {
                                                        // only
                                                        if (application.wasReviewedByApplicant()) {
                                                            application.setQualifiable(false);
                                                            if (offer.getEndDate().after(cal.getTime())) {
                                                                // complete
                                                                application.setClosed(true);
                                                            } else {
                                                                application.setClosed(false);
                                                            }
                                                            // neither
                                                        } else {
                                                            // adjudicada
                                                            application.setQualifiable(true);
                                                            application.setClosed(false);
                                                        }
                                                    }
                                                } else {
                                                    // adjudicada
                                                    application.setQualifiable(false);
                                                    application.setClosed(false);
                                                }
                                            } else {
                                                // closed by no-payment (incomplete)
                                                if(offer.hasStarted()){
                                                    application.setClosed(true);
                                                    application.setQualifiable(false);
                                                } else {
                                                    application.setQualifiable(false);
                                                    application.setClosed(false);
                                                }
                                            }

                                            /*// closed by no-payment (incomplete)
                                            if(!offer.isPaid() && offer.hasStarted()){
                                                application.setClosed(true);
                                                application.setQualifiable(false);
                                            // complete
                                            } else if(offer.isPaid() && offer.hasStarted()) {
                                                Calendar cal = Calendar.getInstance();
                                                cal.add(Calendar.DAY_OF_MONTH, 7);

                                                if(application.wasReviewedByApplicantAndPublisher()){
                                                    application.setQualifiable(false);
                                                    application.setClosed(true);
                                                } else {
                                                    // only
                                                    if(application.wasReviewedByApplicant()){
                                                        application.setQualifiable(false);
                                                        if(offer.getStartDate().after(cal.getTime())){
                                                            application.setClosed(true);
                                                        } else {
                                                            application.setClosed(false);
                                                        }
                                                    // neither
                                                    } else {
                                                        application.setQualifiable(true);
                                                        application.setClosed(false);
                                                    }
                                                }
                                            } else {
                                                application.setQualifiable(false);
                                                application.setClosed(false);
                                            }*/

                                            myApplications.add(offer);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    mOnApplicationUpdateListener.onApplicationsResponse(myApplications);
                                } else {
                                    mOnApplicationUpdateListener.onApplicationsResponse(myApplications);
                                }
                            } else {
                                mOnApplicationUpdateListener.onApplicationError("");
                            }
                            MyApplicationsWS = null;
                        }
                    })
                    .errorListener(new Response.ErrorListener() {
                           @Override
                           public void onErrorResponse(VolleyError error) {
                               error.printStackTrace();
                               mOnApplicationUpdateListener.onApplicationError("");
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
        Intent mainIntent = new Intent().setClass(MainActivity.this, OfferDetailsActivity.class);
        mainIntent.putExtra("offer", offer);
        startActivityForResult(mainIntent, APPLICATION_MODIFIED);
    }

    @Override
    public void onApplicationSelected(Offer offer) {
        Intent mainIntent = new Intent().setClass(MainActivity.this, OfferDetailsActivity.class);
        mainIntent.putExtra("offer", offer);
        startActivityForResult(mainIntent, APPLICATION_MODIFIED);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if(resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    bitmap = rotate(bitmap, getCameraPhotoOrientation(selectedImage));
                    if(bitmap.getWidth() > 256){
                        int width = 256;
                        int heightofBitMap = bitmap.getHeight();
                        int widthofBitMap = bitmap.getWidth();
                        heightofBitMap = width * heightofBitMap / widthofBitMap;
                        widthofBitMap = width;
                        bitmap = Bitmap.createScaledBitmap(bitmap, widthofBitMap, heightofBitMap, true);
                        }
                    onGalleryImageListener.onImageSelected(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } if (requestCode == APPLICATION_MODIFIED) {
            if(resultCode == Activity.RESULT_OK) {
                Log.d("MainActivity", "application modified. Refreshing wall");
                refreshWall();
            }
        }
    }

    public int getCameraPhotoOrientation(Uri imageUri) {
        int rotate = 0;
        try {

            android.support.media.ExifInterface exif;

            InputStream input = getContentResolver().openInputStream(imageUri);
            if (Build.VERSION.SDK_INT > 23)
                exif = new android.support.media.ExifInterface(input);
            else
                exif = new android.support.media.ExifInterface(imageUri.getPath());

            String exifOrientation = exif
                    .getAttribute(android.support.media.ExifInterface.TAG_ORIENTATION);
            Log.d("exifOrientation", exifOrientation);
            int orientation = exif.getAttributeInt(
                    android.support.media.ExifInterface.TAG_ORIENTATION,
                    android.support.media.ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case android.support.media.ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case android.support.media.ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case android.support.media.ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotate;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
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

                            user = User.parseFromJson(response);

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
    public void closeNotification(StatusNotification notification) {
        //statusNotifications.remove(notification);
        this.statusNotifications.remove(notification);
        notificationService.removeNotification(notification);
    }

    @Override
    public void onNotificationSelected() {
        bottomNavigationView.setSelectedItemId(R.id.action_applications);
    }

    private OnGalleryImageListener onGalleryImageListener;

    @Override
    public void getImageFromGallery(String message, OnGalleryImageListener listener) {
        this.onGalleryImageListener = listener;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, message), PICK_IMAGE);
    }

    private void bindPollingService(){
        Intent serviceIntent = new Intent(this, NotificationsListenerService.class);
        serviceIntent.setAction(ACTION_START_SERVICE);
        serviceIntent.putExtra("user", user);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
        if (bindService(serviceIntent, mConnection, Context.BIND_AUTO_CREATE)){
            mShouldUnbind = true;
        }
    }

    private void doUnbindService() {
        if (mShouldUnbind) {
            unbindService(mConnection);
            mShouldUnbind = false;
        }
    }

    @Override
    public void notification(List<StatusNotification> statusNotifications) {
        this.statusNotifications = statusNotifications;
        if(this.statusNotifications.size() > 0 && listeners != null){
            //listener.onNotificationResponse(this.statusNotifications);
            refreshWall();
        }
    }

    public interface OnGalleryImageListener {
        void onImageSelected(Bitmap bitmap);
    }
}
