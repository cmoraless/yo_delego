package kiwigroup.yodelego.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by cristian on 1/21/18.
 */

public class Application implements Serializable {

    private long id;
    private ApplicationStatus status;
    private Date creationDate;
    private boolean isPaid;
    private boolean isClosed;
    private boolean isQualifiable;
    private List<Review> reviews = new ArrayList<>();

    public static Application parseFromJson(JSONObject object){
        Application application = new Application();

        Log.d("Offer", " APPLICATION ****** " + object.toString());

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", new Locale("es", "ES"));
        try {
            application.setId(object.getLong("id"));

            application.setStatus(Application.ApplicationStatus.fromInteger(object.getInt("status")));
            application.setCreationDate(df.parse(object.getString("created_at")));
            application.setPaid(object.getBoolean("is_paid"));

            JSONArray reviews = object.getJSONArray("reviews");
            for (int i = 0; i < reviews.length(); i++) {
                try {
                    Review review = Review.parseFromJson(reviews.getJSONObject(i));
                    application.reviews.add(review);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
        return application;
    }

    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public boolean isClosed() {
        return isClosed;
    }

    public void setClosed(boolean closed) {
        isClosed = closed;
    }

    public boolean wasReviewedByApplicant(){
        for(int i = 0 ; i < reviews.size(); i++){
            if(reviews.get(i).getKind() == Review.ReviewKind.OFFER){
                return true;
            }
        }
        return false;
    }

    public boolean wasReviewedByApplicantAndPublisher(){
        boolean app = false;
        boolean pub = false;
        for(int i = 0 ; i < reviews.size(); i++){
            if(reviews.get(i).getKind() == Review.ReviewKind.OFFER)
                app = true;
            if(reviews.get(i).getKind() == Review.ReviewKind.APPLICATION)
                pub = true;
        }
        return app && pub;
    }

    public boolean isQualifiable() {
        return isQualifiable;
    }

    public void setQualifiable(boolean qualifiable) {
        isQualifiable = qualifiable;
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

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

}
