package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class RegisterStudentFragment extends Fragment {

    private OnRegisterFragmentListener mListener;

    private View mProgressView;
    private RelativeLayout formLayout;
    //private TextInputEditText universityTextView;
    private Spinner universitySpinner;
    private TextInputEditText careerTextView;
    private TextInputEditText semesterTextView;
    private Button signUpButton;
    private Button cancelButton;

    private String firstName;
    private String lastName;
    private String rut;
    private String email;
    private String password;

    public static RegisterStudentFragment newInstance(String firstName, String lastName, String rut, String email, String password) {
        RegisterStudentFragment fragment = new RegisterStudentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("rut", rut);
        bundle.putString("email", email);
        bundle.putString("password", password);
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
        semesterTextView = view.findViewById(R.id.semester);

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
            public void onEducationalInstitutionsResponse(List<String> response) {
                response.add(0, "Universidad o Instituto");
                showProgress(false);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, response){
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

    private void attemptCreateAccount() {
        //universitySpinner.setError(null);
        careerTextView.setError(null);
        semesterTextView.setError(null);

        String university = universitySpinner.getSelectedItem().toString();
        String career = careerTextView.getText().toString();
        String semester = semesterTextView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        /*if(TextUtils.isEmpty(university)){
            universityTextView.setError(getString(R.string.error_field_required));
            focusView = universityTextView;
            cancel = true;
        }*/
        if(TextUtils.isEmpty(career)){
            careerTextView.setError(getString(R.string.error_field_required));
            focusView = careerTextView;
            cancel = true;
        }
        if(TextUtils.isEmpty(semester)){
            semesterTextView.setError(getString(R.string.error_field_required));
            focusView = semesterTextView;
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
            mListener.createAccount(true, firstName, lastName, rut, email, password, university, career, semester);
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
