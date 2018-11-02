package kiwigroup.yodelego.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Date;

public class StatusNotification implements Serializable, WallItem{
    private long id;
    private long offer_id;
    private String receiver;
    private String offer;
    private String application;
    private NotificationKinds kind;
    private Date date;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOfferId() {
        return offer_id;
    }

    public void setOfferId(long offer_id) {
        this.offer_id = offer_id;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }

    public String getOffer() {
        return offer;
    }

    public void setOffer(String offer) {
        this.offer = offer;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public NotificationKinds getKind() {
        return kind;
    }

    public void setKind(NotificationKinds status) {
        this.kind = status;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static StatusNotification parseFromJson(JSONObject object){
        StatusNotification statusNotification = new StatusNotification();
        //DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", new Locale("es", "ES"));
        try {
            Log.d("StatusNotification", "****** " + object.toString());
            statusNotification.setId(object.getLong("id"));
            statusNotification.setOfferId(object.getLong("offer_id"));
            statusNotification.setReceiver(object.getString("receiver"));
            statusNotification.setOffer(object.getString("offer"));
            statusNotification.setApplication(object.getString("application"));
            statusNotification.setKind(NotificationKinds.fromInteger(object.getInt("kind")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return statusNotification;
    }

    public enum NotificationKinds {
        OFFER_AVAILABLE,
        APPLICATION_ACCEPTED,
        APPLICATION_REJECTED,
        APPLICATION_CANCELED_BY_APPLICANT,
        OFFER_PAUSED,
        OFFER_CANCELED;

        public static NotificationKinds fromInteger(int number) {
            switch(number) {
                case 0:
                    return OFFER_AVAILABLE;
                case 1:
                    return APPLICATION_ACCEPTED;
                case 2:
                    return APPLICATION_REJECTED;
                case 3:
                    return APPLICATION_CANCELED_BY_APPLICANT;
                case 4:
                    return OFFER_PAUSED;
                case 5:
                    return OFFER_CANCELED;
            }
            return null;
        }
    }
}
