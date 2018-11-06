package kiwigroup.yodelego.services;


import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import kiwigroup.yodelego.MainActivity;
import kiwigroup.yodelego.R;
import kiwigroup.yodelego.model.StatusNotification;
import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

/**
 * Created by cristian on 5/3/17.
 */

public class NotificationsListenerService extends Service {
    private int ID = 0;
    public static boolean isServiceRunning = false;
    public static final String NOTIFICATION_CHANNEL_ID = "yodelego_channel";
    public static final String SERVICE_NOTIFICATION_CHANNEL_ID = "yodelego_service";
    public static final String ACTION_START_SERVICE = "ACTION_START_SERVICE";
    private static final long DEFAULT_SYNC_INTERVAL = 5 * 1000;
    private Handler mHandler;
    private NotificationListener listener;
    private final IBinder mBinder = new LocalBinder();
    private User user;
    private List<StatusNotification> statusNotifications;

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
    public void onDestroy() {
        super.onDestroy();

        isServiceRunning = false;
        mHandler.removeCallbacksAndMessages(null);
        mHandler.removeMessages(0);
        stopForeground(true);
        stopSelf();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null){
            Bundle bundle = intent.getExtras();
            user = (User) bundle.getSerializable("user");
            if (intent.getAction().equals(ACTION_START_SERVICE)) {
                startServiceWithNotification();
            }
        }
        return START_STICKY;
    }

    public void removeNotification(StatusNotification notification) {
        statusNotifications.remove(notification);
        updateNotifications();
    }

    public void addListener(NotificationListener listener){
        this.listener = listener;
        updateNotifications();
    }

    private void updateNotifications(){
        if(listener != null && statusNotifications != null && statusNotifications.size() > 0){
            listener.notification(statusNotifications);
        }
    }

    private void syncData(){
        ServerCommunication userSC = new ServerCommunication.ServerCommunicationBuilder(getApplication(), "notifications/")
            .GET()
            .tokenized(true)
            .arrayReturnListener(new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    if(response != null) {
                        Log.d("NotificationsService", "*** notifications response: " + response.toString() );
                        List<StatusNotification> newStatusNotifications = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject object = response.getJSONObject(i);
                                StatusNotification statusNotification = StatusNotification.parseFromJson(object);
                                newStatusNotifications.add(statusNotification);
                                if(statusNotification.getKind() == StatusNotification.NotificationKinds.OFFER_AVAILABLE){
                                    sendNotification("Existe una nueva oferta disponible");
                                } else if (statusNotification.getKind() == StatusNotification.NotificationKinds.APPLICATION_ACCEPTED){
                                    sendNotification(String.format("Tu postulación a %s ha sido aceptada", statusNotification.getOffer()));
                                } else if (statusNotification.getKind() == StatusNotification.NotificationKinds.APPLICATION_REJECTED){
                                    sendNotification(String.format("Tu postulación a %s ha sido rechazada", statusNotification.getOffer()));
                                } else if (statusNotification.getKind() == StatusNotification.NotificationKinds.APPLICATION_CANCELED_BY_APPLICANT){
                                    sendNotification(String.format("Tu postulación a %s ha sido cancelada con éxito", statusNotification.getOffer()));
                                } else if (statusNotification.getKind() == StatusNotification.NotificationKinds.OFFER_CANCELED){
                                    sendNotification(String.format("La oferta %s ha sido cancelada", statusNotification.getOffer()));
                                }
                                checkNotification(statusNotification.getId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                                return;
                            }
                        }

                        if(statusNotifications != null){
                            for(StatusNotification savedStatusNotification : statusNotifications){
                                boolean delete = false;
                                for(StatusNotification statusNotification : newStatusNotifications){
                                    if(statusNotification.getId() == savedStatusNotification.getId())
                                        delete = true;
                                }
                                if(!delete)
                                    newStatusNotifications.add(savedStatusNotification);
                            }
                        }
                        statusNotifications = newStatusNotifications;

                        if(response.length() > 0)
                            updateNotifications();
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

        /*if(ID < 3){
            String str = "[{\"id\":" + ID +",\"offer_id\":48,\"offer\":\"tarea con notificación 4\",\"receiver\":\"Cristian Morales\",\"application\":\"tarea con notificación 4 [Aceptada]\",\"status\":0,\"kind\":5,\"created_at\":\"2018-11-02T17:28:10.228138-03:00\"}]";
            JSONArray response = null;
            try {
                response = new JSONArray(str);
                List<StatusNotification> newStatusNotifications = new ArrayList<>();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject object = response.getJSONObject(i);
                        StatusNotification statusNotification = StatusNotification.parseFromJson(object);
                        newStatusNotifications.add(statusNotification);
                        if(statusNotification.getKind() == StatusNotification.NotificationKinds.OFFER_AVAILABLE){
                            sendNotification("Existe una nueva oferta disponible");
                        } else if (statusNotification.getKind() == StatusNotification.NotificationKinds.APPLICATION_ACCEPTED){
                            sendNotification(String.format("Tu postulación a %s ha sido aceptada", statusNotification.getOffer()));
                        } else if (statusNotification.getKind() == StatusNotification.NotificationKinds.APPLICATION_REJECTED){
                            sendNotification(String.format("Tu postulación a %s ha sido rechazada", statusNotification.getOffer()));
                        } else if (statusNotification.getKind() == StatusNotification.NotificationKinds.APPLICATION_CANCELED_BY_APPLICANT){
                            sendNotification(String.format("Tu postulación a %s ha sido cancelada con éxito", statusNotification.getOffer()));
                        } else if (statusNotification.getKind() == StatusNotification.NotificationKinds.OFFER_CANCELED){
                            sendNotification(String.format("La oferta %s ha sido cancelada", statusNotification.getOffer()));
                        }
                        checkNotification(statusNotification.getId());
                    } catch (JSONException e) {
                        e.printStackTrace();
                        return;
                    }
                }
                if(statusNotifications != null){
                    for(StatusNotification savedStatusNotification : statusNotifications){
                        boolean delete = false;
                        for(StatusNotification statusNotification : newStatusNotifications){
                            if(statusNotification.getId() == savedStatusNotification.getId())
                                delete = true;
                        }
                        if(!delete)
                            newStatusNotifications.add(savedStatusNotification);
                    }
                }
                statusNotifications = newStatusNotifications;

                updateNotifications();

                ID ++;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
    }

    private void checkNotification(long id){
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
                "YoDelegoChannel",
                NotificationManager.IMPORTANCE_HIGH);

            notificationChannel.setDescription("YoDelego Channel");
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                this, NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
            .setDefaults(Notification.DEFAULT_ALL)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.mipmap.ic_notification)
            .setTicker("YoDelego")
            .setPriority(Notification.PRIORITY_DEFAULT)
            .setContentTitle("YoDelego")
            .setContentText(remoteMessage)
            .setContentInfo("Info");

        Intent notificationIntent = new Intent(this, MainActivity.class);

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        notificationIntent.putExtra("user", user);
        PendingIntent intent = PendingIntent.getActivity(
                this,
                (int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setContentIntent(intent);
        Notification notification = notificationBuilder.build();
        notification.flags |= Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(
                (int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE),
                notification);
    }

    private static final int NOTIFICATION_ID = 543;

    private void startServiceWithNotification() {
        if (isServiceRunning)
            return;
        isServiceRunning = true;

        mHandler = new Handler();
        mHandler.post(runnableService);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel notificationChannel = new NotificationChannel(
                    SERVICE_NOTIFICATION_CHANNEL_ID,
                    "YoDelegoService",
                    NotificationManager.IMPORTANCE_MIN);

            notificationChannel.setDescription("YoDelego Channel");
            notificationChannel.enableVibration(true);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setAction("");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        notificationIntent.putExtra("user", user);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this,
                (int)((new Date().getTime() / 1000L) % Integer.MAX_VALUE),
                notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, SERVICE_NOTIFICATION_CHANNEL_ID);
        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_notification)
                .setTicker("YoDelego")
                .setPriority(Notification.PRIORITY_MIN)
                .setContentTitle("YoDelego")
                .setContentIntent(pendingIntent)
                .setContentInfo("Info");

        Notification notification = notificationBuilder.build();
        startForeground(NOTIFICATION_ID, notification);
    }

    public interface NotificationListener {
        void notification(List<StatusNotification> statusNotifications);
    }

    public class LocalBinder extends Binder {
        public NotificationsListenerService getServiceInstance(){
            return NotificationsListenerService.this;
        }
    }
}
