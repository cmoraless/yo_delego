package kiwigroup.yodelego.model;

public class NotificationResume implements WallItem {
    private String resume;
    private int accepted_offers;
    private int rejected_offers;

    public NotificationResume(String resume){
        this.resume = resume;
    }

    public String getResume() {
        return resume;
    }

    public void setResume(String resume) {
        this.resume = resume;
    }

    public int getAcceptedOffers() {
        return accepted_offers;
    }

    public void setAcceptedOffers(int accepted_offers) {
        this.accepted_offers = accepted_offers;
    }

    public int getRejectedOffers() {
        return rejected_offers;
    }

    public void setRejectedOffers(int rejected_offers) {
        this.rejected_offers = rejected_offers;
    }
}
