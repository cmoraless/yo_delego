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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

public class RegisterMainFragment extends Fragment {

    private OnRegisterFragmentListener mListener;
    private View mProgressView;
    private LinearLayout baseFormLayout;
    private RelativeLayout studentFormLayout;

    private ToggleButton studentButton;
    private ToggleButton noStudentButton;
    private TextInputEditText textViewFirstName;
    private TextInputEditText textViewLastName;
    private TextInputEditText textViewRut;
    private TextView textViewMail;
    private TextView textViewPassword;
    private TextView textViewConfirmPassword;
    private CheckBox checkBoxAgree;

    private Button signUpButton;
    private Button cancelButton;

    public static RegisterMainFragment newInstance() {
        RegisterMainFragment fragment = new RegisterMainFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_register, container, false);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        studentFormLayout = view.findViewById(R.id.email_login_form);
        baseFormLayout = view.findViewById(R.id.base_form);

        mProgressView = view.findViewById(R.id.login_progress);
        studentButton = view.findViewById(R.id.student);
        studentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                noStudentButton.setChecked(false);
                if(studentButton.isChecked()){
                    textViewFirstName.setEnabled(true);
                    textViewLastName.setEnabled(true);
                    textViewRut.setEnabled(true);
                    textViewMail.setEnabled(true);
                    textViewPassword.setEnabled(true);
                    textViewConfirmPassword.setEnabled(true);
                    checkBoxAgree.setEnabled(true);
                    signUpButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    signUpButton.setText("CONTINUAR");
                } else {
                    textViewFirstName.setEnabled(false);
                    textViewLastName.setEnabled(false);
                    textViewRut.setEnabled(false);
                    textViewMail.setEnabled(false);
                    textViewPassword.setEnabled(false);
                    textViewConfirmPassword.setEnabled(false);
                    checkBoxAgree.setEnabled(false);
                    signUpButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    signUpButton.setText("FINALIZAR");
                }
            }
        });
        noStudentButton = view.findViewById(R.id.no_student);
        noStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                studentButton.setChecked(false);
                signUpButton.setText("FINALIZAR");
                if(noStudentButton.isChecked()){
                    textViewFirstName.setEnabled(true);
                    textViewLastName.setEnabled(true);
                    textViewRut.setEnabled(true);
                    textViewMail.setEnabled(true);
                    textViewPassword.setEnabled(true);
                    textViewConfirmPassword.setEnabled(true);
                    checkBoxAgree.setEnabled(true);
                    signUpButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    signUpButton.setText("FINALIZAR");
                } else {
                    textViewFirstName.setEnabled(false);
                    textViewLastName.setEnabled(false);
                    textViewRut.setEnabled(false);
                    textViewMail.setEnabled(false);
                    textViewPassword.setEnabled(false);
                    textViewConfirmPassword.setEnabled(false);
                    checkBoxAgree.setEnabled(false);
                    signUpButton.setEnabled(false);
                    cancelButton.setEnabled(false);
                    signUpButton.setText("FINALIZAR");
                }
            }
        });
        textViewFirstName = view.findViewById(R.id.first_name);
        textViewLastName = view.findViewById(R.id.last_name);
        textViewRut = view.findViewById(R.id.rut);
        textViewRut.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(!hasFocus){
                    textViewRut.setHint(getString(R.string.rut_hint));
                    if(!textViewRut.getText().toString().isEmpty() && !isValidRut(textViewRut.getText().toString())){
                        textViewRut.setError("el Rut no es válido");
                    }
                } else {
                    textViewRut.setHint(getString(R.string.rut));
                }
            }
        });
        textViewMail = view.findViewById(R.id.mail);
        textViewPassword = view.findViewById(R.id.pin);
        textViewConfirmPassword = view.findViewById(R.id.confirm_password);
        checkBoxAgree = view.findViewById(R.id.terms);

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
    }

    private void showProgress(final boolean show) {
        for (int i = 0; i < studentFormLayout.getChildCount(); i++) {
            View child = studentFormLayout.getChildAt(i);
            child.setEnabled(!show);
        }
        for (int i = 0; i < baseFormLayout.getChildCount(); i++) {
            View child = baseFormLayout.getChildAt(i);
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

    private void attemptCreateAccount() {
        textViewFirstName.setError(null);
        textViewLastName.setError(null);
        textViewMail.setError(null);
        textViewPassword.setError(null);
        textViewConfirmPassword.setError(null);
        textViewRut.setError(null);
        checkBoxAgree.setError(null);

        String firstName = textViewFirstName.getText().toString();
        String lastName = textViewLastName.getText().toString();
        String rut = textViewRut.getText().toString();
        String email = textViewMail.getText().toString();
        String password = textViewPassword.getText().toString();
        String passwordConfirm = textViewConfirmPassword.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(firstName)){
            textViewFirstName.setError(getString(R.string.error_field_required));
            focusView = textViewFirstName;
            cancel = true;
        }
        if(TextUtils.isEmpty(lastName)){
            textViewLastName.setError(getString(R.string.error_field_required));
            focusView = textViewLastName;
            cancel = true;
        }
        if(TextUtils.isEmpty(rut)){
            textViewRut.setError(getString(R.string.error_field_required));
            focusView = textViewRut;
            cancel = true;
        } else if(!isValidRut(rut)){
            textViewRut.setError("el Rut no es válido");
            focusView = textViewRut;
            cancel = true;
        }
        if(TextUtils.isEmpty(email)){
            textViewMail.setError(getString(R.string.error_field_required));
            focusView = textViewMail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            textViewMail.setError(getString(R.string.error_invalid_email));
            focusView = textViewMail;
            cancel = true;
        }
        if(TextUtils.isEmpty(password)){
            textViewPassword.setError(getString(R.string.error_field_required));
            focusView = textViewPassword;
            cancel = true;
        }
        if(TextUtils.isEmpty(passwordConfirm)){
            textViewConfirmPassword.setError(getString(R.string.error_field_required));
            focusView = textViewConfirmPassword;
            cancel = true;
        }

        if(!checkBoxAgree.isChecked()){
            checkBoxAgree.setError(getString(R.string.error_field_must_be_accepted));
            focusView = checkBoxAgree;
            cancel = true;
        }

        if(!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            textViewPassword.setError(getString(R.string.error_invalid_password));
            focusView = textViewPassword;
            cancel = true;
        } else if(!TextUtils.isEmpty(passwordConfirm)) {
            if(!passwordConfirm.equals(password)){
                textViewConfirmPassword.setError(getString(R.string.error_password_must_be_same));
                focusView = textViewConfirmPassword;
                cancel = true;
            }
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            signUpButton.setEnabled(false);
            signUpButton.setAlpha(.5f);
            cancelButton.setEnabled(false);
            cancelButton.setAlpha(.5f);

            if(noStudentButton.isChecked()) {
                mListener.createAccount(false, firstName, lastName, rut, email, password, null, null, null);
            } else if(studentButton.isChecked()){
                RegisterStudentFragment studentFragment = RegisterStudentFragment.newInstance(firstName, lastName, rut, email, password);
                mListener.addFragmentToMainContent(studentFragment, true, getString(R.string.id_student_fragment));
            }
        }
    }

    public static boolean isValidRut(String rut) {

        boolean validacion = false;
        try {
            rut =  rut.toUpperCase();
            rut = rut.replace(".", "");
            rut = rut.replace("-", "");
            int rutAux = Integer.parseInt(rut.substring(0, rut.length() - 1));

            char dv = rut.charAt(rut.length() - 1);

            int m = 0, s = 1;
            for (; rutAux != 0; rutAux /= 10) {
                s = (s + rutAux % 10 * (9 - m++ % 6)) % 11;
            }
            if (dv == (char) (s != 0 ? s + 47 : 75)) {
                validacion = true;
            }

        } catch (java.lang.NumberFormatException e) {
        } catch (Exception e) {
        }
        return validacion;
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
