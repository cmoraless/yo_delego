package kiwigroup.yodelego.model;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
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
    private float publisherRating;
    private String publisherProfilePicture;
    private String summary;
    private String commune;
    private String address;
    private String schedule;
    private OfferStatus status;
    private boolean applied;
    private boolean isPaid;

    private Application application;
    private List<String> images;
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
            if(!object.isNull("images"))
                offer.setImages(object.getJSONArray("images"));
            if(!object.isNull("publisher_profile_picture"))
                offer.setPublisherProfilePicture(object.getString("publisher_profile_picture"));

            offer.setStatus(Offer.OfferStatus.fromInteger(object.getInt("status")));

            if(!object.isNull("publisher_rating")){
                try {
                    offer.setPublisherRating(BigDecimal.valueOf(object.getDouble("publisher_rating")).floatValue());
                } catch(Exception ex){
                    ex.printStackTrace();
                    offer.setPublisherRating(-1.0f);
                }
            } else {
                offer.setPublisherRating(-1.0f);
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

    public float getPublisherRating() {
        return publisherRating;
    }

    public void setPublisherRating(float publisherRating) {
        this.publisherRating = publisherRating;
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

    public List<String> getImages() {
        return images;
    }

    public void setImages(JSONArray JSONArrayImages) {
        this.images = new ArrayList<>();
        for(int i = 0; i < JSONArrayImages.length(); i++){
            try {
                String path = JSONArrayImages.getString(i);
                if(!path.contains("/web.yodelego.com")){
                    path = "http://web.yodelego.com/" + path;
                }
                this.images.add(path);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public void setPublisherProfilePicture(String publisherProfilePicture) {
        this.publisherProfilePicture = publisherProfilePicture;
    }

    public String getPublisherProfilePicture(){
        return this.publisherProfilePicture;
    }

    public enum OfferStatus {
        CANCELED,
        ENTERED,
        REVISION,
        ACCEPTED_APPLICATION,
        FILLED,
        PAUSED,
        CLOSED,
        DEACTIVATED;

        public static OfferStatus fromInteger(int number) {
            switch(number) {
                case -1:
                    return CANCELED;
                case 0:
                    return ENTERED;
                case 1:
                    return REVISION;
                case 2:
                    return ACCEPTED_APPLICATION;
                case 3:
                    return FILLED;
                case 4:
                    return PAUSED;
                case 5:
                    return CLOSED;
                case 6:
                    return DEACTIVATED;
            }
            return null;
        }
    }
}
