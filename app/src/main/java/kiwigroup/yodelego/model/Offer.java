package kiwigroup.yodelego.model;

import java.util.Date;

/**
 * Created by cristian on 1/21/18.
 */

public class Offer {

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

    public enum OfferStatus{
        CLOSE ,
        OPEN
    }

    private String publisher;
    private Date date;
    private String publicationResume;
    private boolean open;
    private String title;
    private int amount;
    private String summary;
    private String address;
    private String schedule;
    private OfferStatus status;


    public Offer(String publisher, Date date, String publicationResume, boolean open, String title, int amount, String summary, String address, String schedule, OfferStatus status) {
        this.publisher = publisher;
        this.date = date;
        this.publicationResume = publicationResume;
        this.open = open;
        this.title = title;
        this.amount = amount;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
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
