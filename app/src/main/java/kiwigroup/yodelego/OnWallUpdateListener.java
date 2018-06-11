package kiwigroup.yodelego;

import java.util.List;

import kiwigroup.yodelego.model.NotificationResume;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.WallItem;

public interface OnWallUpdateListener {
    void cleanWall();
    void onLoadingWallItems();
    void onWallItemsResponse(List<WallItem> wallOffers);
    void onWallItemsError(String error);
    void onApplicationsResponse(List<Offer> applications);
    void onApplicationError(String error);
    void onNotificationResponse(NotificationResume notificationResume);

}