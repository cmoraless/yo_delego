package kiwigroup.yodelego;

import android.support.v4.app.Fragment;

import com.android.volley.Response;

import java.util.List;

public interface OnRegisterFragmentListener {
    void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId);
    void getEducationalInstitutions(RegisterActivity.onEducationalInstitutionsListener listener);
    void createAccount(boolean student,
                       String name,
                       String rut,
                       String lastName,
                       String email,
                       String password,
                       int university,
                       String career,
                       String semester,
                       int bank,
                       int bank_account_type,
                       String bank_account_number);
    void finish();
    void goToWall();
    void goToLogin();
}