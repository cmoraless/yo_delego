package kiwigroup.yodelego;

import android.support.v4.app.Fragment;

public interface OnRegisterFragmentListener {
    void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId);
    void getEducationalInstitutions(RegisterActivity.OnEducationalInstitutionsListener listener);
    void getCareerCategories(RegisterActivity.OnCareerCategoriesListener listener);
    void createAccount(boolean student,
                       String name,
                       String lastName,
                       String rut,
                       String email,
                       final String phone,
                       String password,
                       int university,
                       int university_area,
                       String career,
                       String semester,
                       int bank,
                       int bank_account_type,
                       String bank_account_number);
    void finish();
    void goToWall();
    void goToLogin();
}