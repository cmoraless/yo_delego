package kiwigroup.yodelego;

import android.support.v4.app.Fragment;

public interface OnRegisterFragmentListener {
    void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId);
    void createStudentAccount(boolean student, String name, String rut, String lastName, String email, String password, String university, String career, String semester);
    void finish();
    void goToWall();
    void goToLogin();
}