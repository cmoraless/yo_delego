package kiwigroup.yodelego.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Notification implements Serializable{
    private long id;
    private long offer_id;
    private String receiver;
    private String offer;
    private String application;
    private int status;
    private int kind;
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

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getKind() {
        return kind;
    }

    public void setKind(int kind) {
        this.kind = kind;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public static Notification parseFromJson(JSONObject object){
        Notification notification = new Notification();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.US);
        try {
            Log.d("Offer", "****** " + object.toString());
            notification.setId(object.getLong("id"));
            notification.setOfferId(object.getLong("offer_id"));
            notification.setReceiver(object.getString("receiver"));
            notification.setOffer(object.getString("offer"));
            notification.setApplication(object.getString("application"));
            notification.setStatus(object.getInt("status"));
            notification.setKind(object.getInt("kind"));
            //notification.setCreationDate(df.parse(object.getString("date")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return notification;
    }
}
