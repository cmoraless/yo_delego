package kiwigroup.yodelego.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import kiwigroup.yodelego.MainActivity;
import kiwigroup.yodelego.R;
import kiwigroup.yodelego.server.ServerCommunication;

/**
 * Created by cristian on 5/3/17.
 */

public class NotificationsListenerService extends Service {

    public static final String NOTIFICATION_CHANNEL_ID = "my_channel_id_01";
    private static final long DEFAULT_SYNC_INTERVAL = 30 * 1000;
    private Handler mHandler;
    private NotificationListener listener;
    private final IBinder mBinder = new LocalBinder();

    private Runnable runnableService = new Runnable() {
        @Override
        public void run() {
            syncData();
            mHandler.postDelayed(runnableService, DEFAULT_SYNC_INTERVAL);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
        mHandler.post(runnableService);
        return START_STICKY;
    }

    public void addListener(NotificationListener listener){
        this.listener = listener;
    }

    private void syncData(){
        Log.d("NotificationsService", "*** syncData()" );
        ServerCommunication userSC = new ServerCommunication.ServerCommunicationBuilder(getApplication(), "notifications/")
            .GET()
            .tokenized(true)
            .arrayReturnListener(new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if(response != null) {
                        int available_offers = 0;
                        List<kiwigroup.yodelego.model.Notification> notifications = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                int id = object.getInt("id");
                                int kind = object.getInt("kind");
                                String offer = object.getString("offer");


                                /*OFFER_AVAILABLE = 0
                                APPLICATION_ACCEPTED = 1
                                APPLICATION_REJECTED = 2
                                APPLICATION_CANCELED_BY_APPLICANT = 3
                                OFFER_PAUSED = 4
                                OFFER_CANCELED = 5*/

                                // Offer available
                                if(kind == 0){
                                    available_offers ++;
                                    sendNotification(String.format("Existe una nueva oferta disponible", offer));
                                // Application accepted
                                } else if (kind == 1){
                                    sendNotification(String.format("Tu postulación a %s ha sido aceptada", offer));
                                // Application rejected
                                } else if (kind == 2){
                                    sendNotification(String.format("Tu postulación a %s ha sido rechazada", offer));
                                } else if (kind == 3){
                                    sendNotification(String.format("Tu postulación a %s ha sido cancelada por su creador", offer));
                                }

                                kiwigroup.yodelego.model.Notification notification = kiwigroup.yodelego.model.Notification.parseFromJson(object);
                                notifications.add(notification);

                                checkNotification(id);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }
                        }
                        if(notifications.size() <= 0)
                            return;

                        if(listener != null){
                            listener.notification(notifications);
                        }
                        if(available_offers > 0){
                            if(available_offers == 1)
                                sendNotification("Existe una oferta nueva!");
                            else
                                sendNotification("Existen " + available_offers + " ofertas nuevas!");
                        }
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

    private void checkNotification(int id){
        HashMap<String, Object> args = new HashMap<>();
        args.put("status", 1);
        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(
                this, String.format(new Locale("es", "ES"),"notifications/%d/", id))
            .PATCH()
            .tokenized(true)
            .parameters(args)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                }
            })
            .errorListener(new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {

                }
            })
            .build();
        sc.execute();
    }

    private void sendNotification(String remoteMessage) {
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "My Notifications",
                NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("Channel description");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 500, 1000});
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_notification)
            .setTicker("YoDelego")
            .setPriority(Notification.PRIORITY_MAX)
            .setContentTitle("YoDelego")
            .setContentText(remoteMessage)
            .setContentInfo("Info");

        Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        notificationBuilder.setContentIntent(intent);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(1, notification);
    }

    public interface NotificationListener {
        void notification(List<kiwigroup.yodelego.model.Notification> notifications);
    }

    public class LocalBinder extends Binder {
        public NotificationsListenerService getServiceInstance(){
            return NotificationsListenerService.this;
        }
    }
}
