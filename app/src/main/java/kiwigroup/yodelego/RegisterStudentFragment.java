package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class RegisterStudentFragment extends Fragment {

    private OnRegisterFragmentListener mListener;

    private View mProgressView;
    private RelativeLayout formLayout;
    //private TextInputEditText universityTextView;
    private Spinner universitySpinner;
    private TextInputEditText careerTextView;
    private TextInputEditText yearTextView;
    private TextInputLayout yearLayout;
    private Button signUpButton;
    private Button cancelButton;

    private String firstName;
    private String lastName;
    private String rut;
    private String email;
    private String password;
    private Map educationInstitutions;
    private String errorUniversity;
    private String errorCareer;
    private String errorYear;

    public static RegisterStudentFragment newInstance(String firstName,
                                                      String lastName,
                                                      String rut,
                                                      String email,
                                                      String password
                                                      /*String career,
                                                      String year,
                                                      String university,
                                                      String errorUniversity,
                                                      String errorCareer,
                                                      String errorYear*/) {
        RegisterStudentFragment fragment = new RegisterStudentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("rut", rut);
        bundle.putString("email", email);
        bundle.putString("password", password);
        /*bundle.putString("career", career);
        bundle.putString("year", year);
        bundle.putString("university", university);
        bundle.putString("errorUniversity", errorUniversity);
        bundle.putString("errorCareer", errorCareer);
        bundle.putString("errorYear", errorYear);*/

        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            firstName = getArguments().getString("name");
            lastName = getArguments().getString("lastName");
            rut = getArguments().getString("rut");
            email = getArguments().getString("email");
            password = getArguments().getString("password");

            /*errorUniversity = getArguments().getString("errorUniversity");
            errorCareer = getArguments().getString("errorCareer");
            errorYear = getArguments().getString("errorYear");*/
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_student_register, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        formLayout = view.findViewById(R.id.email_login_form);
        mProgressView = view.findViewById(R.id.login_progress);

        universitySpinner = view.findViewById(R.id.spinner);
        careerTextView = view.findViewById(R.id.career);
        yearTextView = view.findViewById(R.id.enrollment_year);

        signUpButton = view.findViewById(R.id.create_account_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateAccount();
            }
        });
        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToLogin();
            }
        });

        showProgress(true);
        mListener.getEducationalInstitutions(new RegisterActivity.onEducationalInstitutionsListener() {
            @Override
            public void onEducationalInstitutionsResponse(Map<String, Integer> response) {
                educationInstitutions = response;
                List<String> values = new ArrayList<>(response.keySet());
                values.add(0, "Universidad o Instituto");
                showProgress(false);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, values){
                    @Override
                    public boolean isEnabled(int position){
                        return position != 0;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView, ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                        return view;
                    }
                };
                adapter.setDropDownViewResource(R.layout.spinner_layout);
                universitySpinner.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {

            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, new ArrayList<String>()){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        adapter.add("Universidad o Instituto");
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        universitySpinner.setAdapter(adapter);
    }

    public void updateErrors(String errorUniversity,
                             String errorCareer,
                             String errorYear){
        ((TextView)universitySpinner.getSelectedView()).setError(errorUniversity);
        careerTextView.setError(errorCareer);
        yearTextView.setError(errorYear);
    }

    private void attemptCreateAccount() {
        ((TextView)universitySpinner.getSelectedView()).setError(errorUniversity);
        careerTextView.setError(null);
        yearTextView.setError(null);

        String university = universitySpinner.getSelectedItem().toString();
        int universityId;
        String career = careerTextView.getText().toString();
        String year = yearTextView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(educationInstitutions != null){
            try{
                universityId = (Integer) educationInstitutions.get(university);
            } catch(Exception ex){
                ex.printStackTrace();
                universityId = 0;
            }
        } else {
            universityId = 0;
        }
        if(universityId < 1){
            ((TextView)universitySpinner.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = universitySpinner.getSelectedView();
            cancel = true;
        }
        if(TextUtils.isEmpty(career)){
            careerTextView.setError(getString(R.string.error_field_required));
            focusView = careerTextView;
            cancel = true;
        }
        if(TextUtils.isEmpty(year)){
            yearTextView.setError(getString(R.string.error_field_required));
            focusView = yearTextView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            signUpButton.setEnabled(false);
            signUpButton.setAlpha(.5f);
            cancelButton.setEnabled(false);
            cancelButton.setAlpha(.5f);
            mListener.createAccount(true, firstName, lastName, rut, email, password, universityId, career, year);
        }
    }

    private void showProgress(final boolean show) {
        for (int i = 0; i < formLayout.getChildCount(); i++) {
            View child = formLayout.getChildAt(i);
            child.setEnabled(!show);
        }

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentListener) {
            mListener = (OnRegisterFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRegisterFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
