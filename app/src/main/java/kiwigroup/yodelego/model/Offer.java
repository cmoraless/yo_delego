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

public class Offer implements Serializable, WallItem {

    private long id;
    private String publisher;
    private Date creationDate;
    private Date startDate;
    private Date endDate;
    private String publicationResume;
    private boolean open;
    private String title;
    private int dailyWage;
    private int hourlyWage;
    private int totalWage;
    private float rating;
    private String summary;
    private String commune;
    private String address;
    private String schedule;
    private OfferStatus status;
    private boolean applied;
    private Application application;

    public Offer(){}

    public static Offer parseFromJson(JSONObject object){
        Offer offer = new Offer();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.US);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        try {

            Log.d("Offer", "****** " + object.toString());

            offer.setId(object.getLong("id"));
            offer.setTitle(object.getString("title"));
            offer.setSummary(object.getString("description"));
            offer.setCommune(object.getString("commune"));
            offer.setPublisher(object.getString("publisher"));
            offer.setCreationDate(df.parse(object.getString("created_at")));

            if(!object.isNull("start_date"))
                offer.setStartDate(df2.parse(object.getString("start_date")));
            if(!object.isNull("end_date"))
                offer.setEndDate(df2.parse(object.getString("end_date")));
            if(!object.isNull("daily_wage"))
                offer.setDailyWage(object.getInt("daily_wage"));
            if(!object.isNull("hourly_wage"))
                offer.setHourlyWage(object.getInt("hourly_wage"));
            if(!object.isNull("total_wage"))
                offer.setTotalWage(object.getInt("total_wage"));

            offer.setStatus(Offer.OfferStatus.fromInteger(object.getInt("status")));


            if(!object.isNull("rating")){
                try {
                    offer.setRating(BigDecimal.valueOf(object.getDouble("rating")).floatValue());
                } catch(Exception ex){
                    ex.printStackTrace();
                    offer.setRating(-1.0f);
                }
            } else {
                offer.setRating(-1.0f);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return offer;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public void setStatus(OfferStatus status) {
        this.status = status;
    }

    public String getPublicationResume() {
        return publicationResume;
    }

    public void setPublicationResume(String publicationResume) {
        this.publicationResume = publicationResume;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(int hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public int getTotalWage() {
        return totalWage;
    }

    public void setTotalWage(int totalWage) {
        this.totalWage = totalWage;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDailyWage() {
        return dailyWage;
    }

    public void setDailyWage(int dailyWage) {
        this.dailyWage = dailyWage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public boolean isApplied() {
        return applied;
    }

    public void setApplied(boolean applied) {
        this.applied = applied;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getCommune() {
        return commune;
    }

    public void setCommune(String commune) {
        this.commune = commune;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public enum OfferStatus {
        CANCELED,
        ENTERED,
        PUBLISHED,
        PAUSED,
        FILLED,
        CLOSED;

        public static OfferStatus fromInteger(int number) {
            switch(number) {
                case -1:
                    return CANCELED;
                case 0:
                    return ENTERED;
                case 1:
                    return PUBLISHED;
                case 2:
                    return PAUSED;
                case 3:
                    return FILLED;
                case 4:
                    return CLOSED;
            }
            return null;
        }
    }
}
