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
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by cristian on 1/21/18.
 */

public class Offer implements Serializable, WallItem{

    private long id;
    private Date creationDate;
    private Date startDate;
    private Date endDate;
    private Date startTime;
    private String publicationResume;
    private String title;
    private int dailyWage;
    private int hourlyWage;
    private int totalWage;
    private int totalHours;
    private String summary;
    private String commune;
    private String location;
    private String schedule;
    private OfferStatus status;
    private boolean appliedByMe;
    private boolean isPaid;

    private Publisher publisher;

    private Application application;
    private List<String> images;
    public Offer(){}

    public static Offer parseFromJson(JSONObject object){
        Offer offer = new Offer();
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSSZ", Locale.US);
        DateFormat df2 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat df3 = new SimpleDateFormat("HH:mm:ss", Locale.US);
        try {

            Log.d("Offer", " OFFER ****** " + object.toString());

            offer.setId(object.getLong("id"));
            offer.setTitle(object.getString("title"));
            offer.setSummary(object.getString("description"));
            if(object.has("location"))
                offer.setLocation(object.getString("location"));
            offer.setCommune(object.getString("commune"));

            Publisher publisher = Publisher.parseFromJson(object.getJSONObject("publisher"));
            offer.setPublisher(publisher);

            offer.setCreationDate(df.parse(object.getString("created_at")));
            offer.setPaid(object.getBoolean("is_paid"));

            if(!object.isNull("start_date"))
                offer.setStartDate(df2.parse(object.getString("start_date")));
            if(!object.isNull("start_time"))
                offer.setStartTime(df3.parse(object.getString("start_time")));
            if(!object.isNull("end_date"))
                offer.setEndDate(df2.parse(object.getString("end_date")));
            if(!object.isNull("daily_wage"))
                offer.setDailyWage(object.getInt("daily_wage"));
            if(!object.isNull("hourly_wage"))
                offer.setHourlyWage(object.getInt("hourly_wage"));
            if(!object.isNull("total_hours\n"))
                offer.setTotalHours(object.getInt("total_hours"));

            if(!object.isNull("total_wage"))
                offer.setTotalWage(object.getInt("total_wage"));
            if(!object.isNull("images"))
                offer.setImages(object.getJSONArray("images"));

            offer.setStatus(Offer.OfferStatus.fromInteger(object.getInt("status")));

        } catch (JSONException | ParseException e) {
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

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {
        this.publisher = publisher;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
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

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getSchedule() {
        return schedule;
    }

    public void setSchedule(String schedule) {
        this.schedule = schedule;
    }

    public boolean isAppliedByMe() {
        return appliedByMe;
    }

    public void setAppliedByMe(boolean appliedByMe) {
        this.appliedByMe = appliedByMe;
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

    public boolean hasExpired() {
        Date currentTime = Calendar.getInstance().getTime();
        if(getEndDate() != null && currentTime.after(getEndDate())){
            return true;
        }
        return false;
    }

    public void setTotalHours(int totalHours) {
        this.totalHours = totalHours;
    }

    public int getTotalHours() {
        return totalHours;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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
