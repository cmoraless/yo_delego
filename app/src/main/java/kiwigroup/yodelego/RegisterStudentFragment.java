package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class RegisterStudentFragment extends Fragment {

    private OnRegisterFragmentListener mListener;

    private View mProgressView;
    private LinearLayout formLayout;
    private SearchableSpinner universitySpinner;
    private SearchableSpinner categoriesSpinner;
    private TextInputEditText careerTextView;
    private TextInputEditText yearTextView;
    private Button signUpButton;
    private Button cancelButton;

    private String firstName;
    private String lastName;
    private String rut;
    private String phone_number;
    private String email;
    private String password;
    private int bank;
    private int accountType;
    private String account;

    private Map educationInstitutions;
    private Map careerCategories;

    public static RegisterStudentFragment newInstance(
            String firstName,
            String lastName,
            String rut,
            String phone,
            String email,
            String password,
            int bank,
            int accountType,
            String account) {
        RegisterStudentFragment fragment = new RegisterStudentFragment();

        Bundle bundle = new Bundle();
        bundle.putString("name", firstName);
        bundle.putString("lastName", lastName);
        bundle.putString("rut", rut);
        bundle.putString("phone_number", phone);
        bundle.putString("email", email);
        bundle.putString("password", password);

        bundle.putInt("bank", bank);
        bundle.putInt("accountType", accountType);
        bundle.putString("account", account);

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
            phone_number = getArguments().getString("phone_number");
            email = getArguments().getString("email");
            password = getArguments().getString("password");
            bank = getArguments().getInt("bank");
            accountType = getArguments().getInt("accountType");
            account = getArguments().getString("account");
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
        categoriesSpinner = view.findViewById(R.id.cat_spinner);
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

        universitySpinner.setTitle("Universidad o Instituto");
        universitySpinner.setPositiveButton("OK");

        categoriesSpinner.setTitle("Área de estudio");
        categoriesSpinner.setPositiveButton("OK");

        showProgress(true);
        mListener.getEducationalInstitutions(new RegisterActivity.OnEducationalInstitutionsListener() {
            @Override
            public void onEducationalInstitutionsResponse(LinkedHashMap<String, Integer> response) {
                educationInstitutions = response;
                List<String> values = new ArrayList<>(response.keySet());
                values.add(0, "");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, values);
                adapter.setDropDownViewResource(R.layout.spinner_layout);
                universitySpinner.setAdapter(adapter);

                if(educationInstitutions != null && careerCategories != null)
                    showProgress(false);
            }

            @Override
            public void onError(String error) {

            }
        });

        mListener.getCareerCategories(new RegisterActivity.OnCareerCategoriesListener() {
            @Override
            public void onCareerCategoriesResponse(LinkedHashMap<String, Integer> response) {
                careerCategories = response;
                List<String> values = new ArrayList<>(response.keySet());
                values.add(0, "");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, values);
                adapter.setDropDownViewResource(R.layout.spinner_layout);
                categoriesSpinner.setAdapter(adapter);

                if(educationInstitutions != null && careerCategories != null)
                    showProgress(false);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    public void updateErrors(String errorUniversity,
                             String errorCareer,
                             String errorYear){
        ((TextView)universitySpinner.getSelectedView()).setError(errorUniversity);
        careerTextView.setError(errorCareer);
        yearTextView.setError(errorYear);
    }

    private void attemptCreateAccount() {
        ((TextView)universitySpinner.getSelectedView()).setError(null);
        careerTextView.setError(null);
        yearTextView.setError(null);

        String university = universitySpinner.getSelectedItem().toString();
        int universityId;

        String category = categoriesSpinner.getSelectedItem().toString();
        int categoryId;

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

        if(careerCategories != null){
            try{
                categoryId = (Integer) careerCategories.get(category);
            } catch(Exception ex){
                ex.printStackTrace();
                categoryId = 0;
            }
        } else {
            categoryId = 0;
        }
        if(categoryId < 1){
            ((TextView)categoriesSpinner.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = categoriesSpinner.getSelectedView();
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
            mListener.createAccount(
                true,
                firstName,
                lastName,
                rut,
                email,
                phone_number,
                password,
                universityId,
                categoryId,
                career,
                year,
                bank,
                accountType,
                account);
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
