package kiwigroup.yodelego;

import java.util.List;

import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.StatusNotification;
import kiwigroup.yodelego.model.WallItem;

public interface OnWallUpdateListener {
    void cleanWall();
    void onLoadingWallItems();
    void onWallItemsResponse(List<WallItem> wallOffers);
    void onApplicationsResponse(List<Offer> applications);
    void onNotificationResponse(List<StatusNotification> notificationResume);
}