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

/**
 * Created by cristian on 1/21/18.
 */

public class Application implements Serializable {

    private long id;
    private String offerTitle;
    private long offerId;
    private float rating;
    private ApplicationStatus status;
    private Date date;

    public static Application parseFromJson(JSONObject object){
        Application application = new Application();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.US);
        try {
            application.setId(object.getLong("id"));
            application.setOfferTitle(object.getString("offer"));
            application.setOfferId(object.getLong("offer_id"));
            application.setStatus(Application.ApplicationStatus.fromInteger(object.getInt("status")));
            application.setDate(df.parse(object.getString("created_at")));
            if(!object.isNull("rating")){
                try {
                    application.setRating(BigDecimal.valueOf(object.getDouble("rating")).floatValue());
                } catch(Exception ex){
                    ex.printStackTrace();
                    application.setRating(-1.0f);
                }
            } else {
                application.setRating(-1.0f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return application;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public enum ApplicationStatus {
        CANCELED_BY_PUBLISHER,
        CANCELED_BY_APPLICANT,
        REJECTED,
        REVISION,
        ACCEPTED;

        public static ApplicationStatus fromInteger(int number) {
            switch(number) {
                case -3:
                    return CANCELED_BY_PUBLISHER;
                case -2:
                    return CANCELED_BY_APPLICANT;
                case -1:
                    return REJECTED;
                case 0:
                    return REVISION;
                case 1:
                    return ACCEPTED;
            }
            return null;
        }

        public static int toInt(ApplicationStatus status) {
            switch(status) {
                case CANCELED_BY_PUBLISHER:
                    return -3;
                case CANCELED_BY_APPLICANT:
                    return -2;
                case REJECTED:
                    return -1;
                case REVISION:
                    return 0;
                case ACCEPTED:
                    return 1;
            }
            return -4;
        }
    }

    public ApplicationStatus getApplicationStatus() {
        return status;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getOfferTitle() {
        return offerTitle;
    }

    public void setOfferTitle(String offerTitle) {
        this.offerTitle = offerTitle;
    }

    public long getOfferId() {
        return offerId;
    }

    public void setOfferId(long offerId) {
        this.offerId = offerId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    /*public Offer getOffer() {
        return offer;
    }

    public void setOffer(Offer offer) {
        this.offer = offer;
    }*/
}
