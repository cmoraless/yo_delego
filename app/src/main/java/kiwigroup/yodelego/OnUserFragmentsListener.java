package kiwigroup.yodelego;

import android.support.v4.app.Fragment;

import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;

public interface OnUserFragmentsListener {
    void closeSession();
    void askAddFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId);
    void getEducationalInstitutions(RegisterActivity.OnEducationalInstitutionsListener listener);
    void getCareerCategories(RegisterActivity.OnCareerCategoriesListener listener);
    void getWallItems(OnWallUpdateListener listener);
    void getMoreWallItems();
    void getMyApplications(OnApplicationUpdateListener listener, boolean forceReload);
    void refreshWall(OnWallUpdateListener listener);
    void onWallOfferSelected(Offer offer);
    void onApplicationSelected(Offer application);
    void updateUser();
    void closeNotifications();
    void onNotificationSelected();
}