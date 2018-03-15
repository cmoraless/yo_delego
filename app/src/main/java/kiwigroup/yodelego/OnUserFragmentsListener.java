package kiwigroup.yodelego;

import android.support.v4.app.Fragment;

import kiwigroup.yodelego.model.Application;

public interface OnUserFragmentsListener {
    void closeSession();
    void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId);
    void getEducationalInstitutions(RegisterActivity.onEducationalInstitutionsListener listener);
    void getWallItems(OnWallUpdateListener listener);
    void getMoreWallItems();
    void getMyApplications(OnApplicationUpdateListener listener, boolean forceReload);
    void onListFragmentInteraction(Application application);
}