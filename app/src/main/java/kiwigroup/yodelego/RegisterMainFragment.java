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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

    private Spinner bankSpinner;
    private Spinner accountSpinner;
    private TextInputEditText TextViewAccount;
    private ArrayAdapter<String> bankAdapter;
    private ArrayAdapter<String> accountAdapter;

    private CheckBox checkBoxAgree;

    private Button signUpButton;
    private Button cancelButton;

    private String firstNameError;
    private String lastNameError;
    private String mailError;
    private String passwordError;
    private String rutError;
    private String bankError;
    private String accountTypeError;
    private String accountError;

    private boolean student;
    private boolean noStudent;

    public static RegisterMainFragment newInstance() {
        RegisterMainFragment fragment = new RegisterMainFragment();
        return fragment;
    }

    public static RegisterMainFragment newInstance(String firstNameError,
                                                   String lastNameError,
                                                   String mailError,
                                                   String passwordError,
                                                   String rutError,
                                                   String bankError,
                                                   String accountTypeError,
                                                   String accountError) {
        RegisterMainFragment fragment = new RegisterMainFragment();

        Bundle bundle = new Bundle();
        bundle.putString("firstNameError", firstNameError);
        bundle.putString("lastNameError", lastNameError);
        bundle.putString("mailError", mailError);
        bundle.putString("passwordError", passwordError);
        bundle.putString("rutError", rutError);
        bundle.putString("bankError", bankError);
        bundle.putString("accountTypeError", accountTypeError);
        bundle.putString("accountError", accountError);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            firstNameError = getArguments().getString("firstNameError");
            lastNameError = getArguments().getString("lastNameError");
            mailError = getArguments().getString("mailError");
            passwordError = getArguments().getString("passwordError");
            rutError = getArguments().getString("rutError");
            bankError = getArguments().getString("bankError");
            accountTypeError = getArguments().getString("accountTypeError");
            accountError = getArguments().getString("accountError");
        }
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
                    student = true;
                    textViewFirstName.setEnabled(true);
                    textViewLastName.setEnabled(true);
                    textViewRut.setEnabled(true);
                    textViewMail.setEnabled(true);
                    textViewPassword.setEnabled(true);
                    textViewConfirmPassword.setEnabled(true);
                    bankSpinner.setClickable(true);
                    accountSpinner.setClickable(true);
                    TextViewAccount.setEnabled(true);
                    checkBoxAgree.setEnabled(true);
                    signUpButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    signUpButton.setText("CONTINUAR");
                } else {
                    student = false;
                    textViewFirstName.setEnabled(false);
                    textViewLastName.setEnabled(false);
                    textViewRut.setEnabled(false);
                    textViewMail.setEnabled(false);
                    textViewPassword.setEnabled(false);
                    textViewConfirmPassword.setEnabled(false);
                    bankSpinner.setClickable(false);
                    accountSpinner.setClickable(false);
                    TextViewAccount.setEnabled(false);
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
                    noStudent = true;
                    textViewFirstName.setEnabled(true);
                    textViewLastName.setEnabled(true);
                    textViewRut.setEnabled(true);
                    textViewMail.setEnabled(true);
                    textViewPassword.setEnabled(true);
                    textViewConfirmPassword.setEnabled(true);
                    checkBoxAgree.setEnabled(true);
                    bankSpinner.setClickable(true);
                    accountSpinner.setClickable(true);
                    TextViewAccount.setEnabled(true);
                    signUpButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    signUpButton.setText("FINALIZAR");
                } else {
                    noStudent = false;
                    textViewFirstName.setEnabled(false);
                    textViewLastName.setEnabled(false);
                    textViewRut.setEnabled(false);
                    textViewMail.setEnabled(false);
                    textViewPassword.setEnabled(false);
                    textViewConfirmPassword.setEnabled(false);
                    checkBoxAgree.setEnabled(false);
                    bankSpinner.setClickable(false);
                    accountSpinner.setClickable(false);
                    TextViewAccount.setEnabled(false);
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
                    if(!textViewRut.getText().toString().isEmpty() && !isValidRut(textViewRut.getText().toString())){
                        textViewRut.setError("el Rut no es válido");
                    }
                }
            }
        });
        textViewMail = view.findViewById(R.id.mail);
        textViewPassword = view.findViewById(R.id.password);
        textViewConfirmPassword = view.findViewById(R.id.confirm_password);

        bankSpinner = view.findViewById(R.id.bank_spinner);
        accountSpinner = view.findViewById(R.id.account_spinner);
        TextViewAccount = view.findViewById(R.id.account_number);
        bankAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.banks_array)){
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
        bankAdapter.setDropDownViewResource(R.layout.spinner_layout);
        bankSpinner.setAdapter(bankAdapter);

        accountAdapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.account_array)){
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
        accountAdapter.setDropDownViewResource(R.layout.spinner_layout);
        accountSpinner.setAdapter(accountAdapter);

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

        if(student || noStudent){
            textViewFirstName.setEnabled(true);
            textViewLastName.setEnabled(true);
            textViewRut.setEnabled(true);
            textViewMail.setEnabled(true);
            textViewPassword.setEnabled(true);
            textViewConfirmPassword.setEnabled(true);
            bankSpinner.setEnabled(true);
            accountSpinner.setEnabled(true);
            bankSpinner.setClickable(true);
            accountSpinner.setClickable(true);
            TextViewAccount.setEnabled(true);
            checkBoxAgree.setEnabled(true);
            signUpButton.setEnabled(true);
            cancelButton.setEnabled(true);

            if(firstNameError != null)
                textViewFirstName.setError(firstNameError);
            if(lastNameError != null)
                textViewLastName.setError(lastNameError);
            if(mailError != null)
                textViewMail.setError(mailError);
            if(passwordError != null)
                textViewPassword.setError(passwordError);
            if(rutError != null)
                textViewRut.setError(rutError);

            if(bankError != null)
                ((TextView)bankSpinner.getSelectedView()).setError(bankError);
            if(accountTypeError != null)
                ((TextView)accountSpinner.getSelectedView()).setError(accountTypeError);
            if(accountError != null)
                TextViewAccount.setError(accountError);
        }
    }

    public void updateErrors(String firstNameError,
                             String lastNameError,
                             String mailError,
                             String passwordError,
                             String rutError,
                             String bankError,
                             String accountTypeError,
                             String accountError){

        this.firstNameError = firstNameError;
        this.lastNameError = lastNameError;
        this.mailError = mailError;
        this.passwordError = passwordError;
        this.rutError = rutError;
        this.bankError = bankError;
        this.accountTypeError = accountTypeError;
        this.accountError = accountError;

        signUpButton.setEnabled(true);
        signUpButton.setAlpha(1f);
        cancelButton.setEnabled(true);
        cancelButton.setAlpha(1f);
        showProgress(false);

        if(firstNameError != null)
            textViewFirstName.setError(firstNameError);
        if(lastNameError != null)
            textViewLastName.setError(lastNameError);
        if(mailError != null)
            textViewMail.setError(mailError);
        if(passwordError != null)
            textViewPassword.setError(passwordError);
        if(rutError != null)
            textViewRut.setError(rutError);

        if(bankError != null)
            ((TextView)bankSpinner.getSelectedView()).setError(bankError);
        if(accountTypeError != null)
            ((TextView)accountSpinner.getSelectedView()).setError(accountTypeError);
        if(accountError != null)
            TextViewAccount.setError(accountError);
    }

    private void showProgress(final boolean show) {
        for (int i = 0; i < studentFormLayout.getChildCount(); i++) {
            View child = studentFormLayout.getChildAt(i);
            child.setEnabled(!show);
        }
        if(baseFormLayout != null)
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
        ((TextView)bankSpinner.getSelectedView()).setError(null);
        ((TextView)accountSpinner.getSelectedView()).setError(null);
        TextViewAccount.setError(null);

        checkBoxAgree.setError(null);

        String firstName = textViewFirstName.getText().toString();
        String lastName = textViewLastName.getText().toString();
        String rut = textViewRut.getText().toString();
        String email = textViewMail.getText().toString();
        String password = textViewPassword.getText().toString();
        String passwordConfirm = textViewConfirmPassword.getText().toString();
        int bank = bankSpinner.getSelectedItemPosition();
        int accountType = accountSpinner.getSelectedItemPosition();
        String account = TextViewAccount.getText().toString();

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
        /*if(bank < 1){
            ((TextView)bankSpinner.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = bankSpinner.getSelectedView();
            cancel = true;
        }
        if(accountType < 1){
            ((TextView)accountSpinner.getSelectedView()).setError(getString(R.string.error_field_required));
            focusView = accountSpinner.getSelectedView();
            cancel = true;
        }
        if(TextUtils.isEmpty(account)){
            TextViewAccount.setError(getString(R.string.error_field_required));
            focusView = TextViewAccount;
            cancel = true;
        }*/

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
                mListener.createAccount(false,
                    firstName,
                    lastName,
                    rut,
                    email,
                    password,
                    -1,
                    null,
                    null,
                    bank,
                    accountType,
                    account.replaceAll("-", ""));
            } else if(studentButton.isChecked()){
                RegisterStudentFragment studentFragment = RegisterStudentFragment.newInstance(
                        firstName, lastName, rut, email, password, bank, accountType, account.replaceAll("-", ""));
                mListener.addFragmentToMainContent(studentFragment, true, getString(R.string.id_student_fragment));
            }
        }
    }

    private static boolean isValidRut(String rut) {

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

    private static boolean checkForError(JSONObject object, String tag, TextView textView){
        JSONObject errorsObject = null;
        try {
            errorsObject = object.getJSONObject("errors");
            if(errorsObject.has(tag)){
                JSONArray errorsArray = null;
                errorsArray = errorsObject.getJSONArray(tag);
                String errors = "";
                for(int i=0; i<errorsArray.length(); i++){
                    errors += errorsArray.getString(i);
                }
                textView.setError(errors);
                textView.requestFocus();
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
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
