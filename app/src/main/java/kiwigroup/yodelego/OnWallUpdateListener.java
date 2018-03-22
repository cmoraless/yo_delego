package kiwigroup.yodelego;

import android.support.v4.app.Fragment;

import java.util.List;

import kiwigroup.yodelego.model.Offer;

public interface OnWallUpdateListener {
    void cleanWall();
    void onLoadingWallItems();
    void onWallItemsResponse(List<Offer> wallOffers);
    void onWallItemsError(String error);
}