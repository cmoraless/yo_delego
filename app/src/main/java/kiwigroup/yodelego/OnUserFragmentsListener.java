package kiwigroup.yodelego;

import android.support.v4.app.Fragment;

public interface OnUserFragmentsListener {
    void closeSession();
    void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId);
    void getEducationalInstitutions(RegisterActivity.onEducationalInstitutionsListener listener);
}