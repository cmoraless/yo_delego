package kiwigroup.yodelego.model;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by cristian on 1/21/18.
 */

public class Offer implements Serializable {

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

    public enum OfferStatus{
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

    private long id;
    private String publisher;
    private Date date;
    private String publicationResume;
    private boolean open;
    private String title;
    private int dailyWage;
    private int hourlyWage;
    private int totalWage;
    private String summary;
    private String address;
    private String schedule;
    private OfferStatus status;


    public Offer(){
    }

    public Offer(long id, String publisher, Date date, String publicationResume, boolean open, String title, int dailyWage, String summary, String address, String schedule, OfferStatus status) {
        this.id = id;
        this.publisher = publisher;
        this.date = date;
        this.publicationResume = publicationResume;
        this.open = open;
        this.title = title;
        this.dailyWage = dailyWage;
        this.summary = summary;
        this.address = address;
        this.schedule = schedule;
        this.status = status;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
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
}
