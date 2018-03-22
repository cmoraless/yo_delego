package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

public class ProfileEditFragment extends Fragment {
    private View mProgressView;

    private LinearLayout baseFormLayout;
    private TextInputEditText name;
    private TextInputEditText lastName;
    private TextInputEditText rut;
    private TextInputEditText mail;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;
    private Button editButton;
    private RelativeLayout formLayout;

    private LinearLayout studentFormLayout;
    private Spinner universitySpinner;
    private TextInputEditText careerTextView;
    private TextInputEditText yearTextView;
    private ArrayAdapter<String> universityAdapter;

    private TextInputLayout rutLayout;
    private TextInputLayout mailLayout;

    private Spinner bankSpinner;
    private Spinner accountSpinner;
    private TextInputEditText account;
    private ArrayAdapter<String> bankAdapter;
    private ArrayAdapter<String> accountAdapter;

    private User user;
    private OnUserFragmentsListener listener;

    private Map educationInstitutions;

    public static ProfileEditFragment newInstance(User user) {
        ProfileEditFragment fragment = new ProfileEditFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        baseFormLayout = view.findViewById(R.id.summary_info);

        mProgressView = view.findViewById(R.id.login_progress);
        formLayout = view.findViewById(R.id.email_login_form);

        name = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        rut = view.findViewById(R.id.rut);
        mail = view.findViewById(R.id.mail);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirm_password);

        rutLayout = view.findViewById(R.id.id_layout);
        rutLayout.setVisibility(View.GONE);
        mailLayout = view.findViewById(R.id.mail_layout);
        mailLayout.setVisibility(View.GONE);

        studentFormLayout = view.findViewById(R.id.academic_info);
        universitySpinner = view.findViewById(R.id.spinner);
        careerTextView = view.findViewById(R.id.career);
        yearTextView = view.findViewById(R.id.enrollment_year);

        bankSpinner = view.findViewById(R.id.bank_spinner);
        accountSpinner = view.findViewById(R.id.account_spinner);
        account = view.findViewById(R.id.account_number);

        editButton = view.findViewById(R.id.edit_btn);

        showProgress(true);
        listener.getEducationalInstitutions(new RegisterActivity.onEducationalInstitutionsListener() {
            @Override
            public void onEducationalInstitutionsResponse(Map<String, Integer> response) {
                educationInstitutions = response;
                List<String> values = new ArrayList<>(response.keySet());
                values.add(0, "Universidad o Instituto");
                showProgress(false);
                universityAdapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, values){
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
                universityAdapter.setDropDownViewResource(R.layout.spinner_layout);
                universitySpinner.setAdapter(universityAdapter);

                if(user.getEducationalInstitution() == null || user.getEducationalInstitution().isEmpty()){
                    studentFormLayout.setVisibility(View.GONE);
                } else {
                    studentFormLayout.setVisibility(View.VISIBLE);
                    int spinnerPosition = universityAdapter.getPosition(user.getEducationalInstitution());
                    universitySpinner.setSelection(spinnerPosition);
                    careerTextView.setText(user.getCareer());
                    yearTextView.setText(String.valueOf(user.getEnrollmentYear()))  ;
                }
            }

            @Override
            public void onError(String error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.error_network));
                builder.setMessage(getString(R.string.error_network_details));
                builder.setPositiveButton(getString(R.string.message_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
                showProgress(false);
            }
        });

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

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptUpdateAccount();
            }
        });

        loadData(user);
    }

    private void loadData(User user){
        name.setText(user.getName());
        name.setEnabled(true);
        lastName.setText(user.getLastName());
        lastName.setEnabled(true);
        password.setVisibility(View.GONE);
        confirmPassword.setVisibility(View.GONE);

        int spinnerBankPosition = bankAdapter.getPosition(user.getBank().toUpperCase());
        bankSpinner.setClickable(true);
        bankSpinner.setSelection(spinnerBankPosition);

        accountSpinner.setSelection(user.getAccountType());
        accountSpinner.setClickable(true);

        account.setEnabled(true);
        account.setText(user.getAccountNumber());
    }

    private void showProgress(final boolean show) {
        for (int i = 0; i < formLayout.getChildCount(); i++) {
            View child = formLayout.getChildAt(i);
            child.setVisibility(show ? View.GONE : View.VISIBLE);
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
        if (context instanceof OnUserFragmentsListener) {
            listener = (OnUserFragmentsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRegisterFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    private void attemptUpdateAccount() {
        name.setError(null);
        lastName.setError(null);
        mail.setError(null);
        password.setError(null);
        confirmPassword.setError(null);
        rut.setError(null);
        ((TextView)bankSpinner.getSelectedView()).setError(null);
        ((TextView)accountSpinner.getSelectedView()).setError(null);
        account.setError(null);
        if(studentFormLayout.getVisibility() == View.VISIBLE){
            ((TextView)universitySpinner.getSelectedView()).setError(null);
            careerTextView.setError(null);
            yearTextView.setError(null);
        }

        String firstName = name.getText().toString();
        String lastN = lastName.getText().toString();
        int bank = bankSpinner.getSelectedItemPosition();
        int accountType = accountSpinner.getSelectedItemPosition();
        String acc = account.getText().toString();

        String university = "";
        int universityId = -1;
        String career = "";
        String year = "";

        if(studentFormLayout.getVisibility() == View.VISIBLE){
            university = universitySpinner.getSelectedItem().toString();
            career = careerTextView.getText().toString();
            year = yearTextView.getText().toString();
        }

        boolean cancel = false;
        View focusView = null;

        if(TextUtils.isEmpty(firstName)){
            name.setError(getString(R.string.error_field_required));
            focusView = name;
            cancel = true;
        }
        if(TextUtils.isEmpty(lastN)){
            lastName.setError(getString(R.string.error_field_required));
            focusView = lastName;
            cancel = true;
        }
        if(studentFormLayout.getVisibility() == View.VISIBLE){
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
        }


        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            editButton.setEnabled(false);
            editButton.setAlpha(.5f);

            updateAccount(
                    firstName,
                    lastN,
                    universityId,
                    career,
                    year,
                    bank,
                    accountType,
                    acc.replaceAll("-", ""));
        }
    }

    private void updateAccount(
                              String name,
                              String lastName,
                              int university,
                              String career,
                              String year,
                              int bank_id,
                              int bank_account_type,
                              String bank_account_number){
        final HashMap<String, Object> args = new HashMap<>();
        args.put("first_name", name);
        args.put("last_name", lastName);
        if (university >= 0)
            args.put("educational_institution_id", university);
        if (year != null && !year.isEmpty())
            args.put("enrollment_year", year);
        if (career != null && !career.isEmpty())
            args.put("career", career);

        if (bank_id >= 1 && bank_account_type >= 1 && bank_account_number != null && !bank_account_number.isEmpty()) {
            args.put("bank_id", bank_id);
            args.put("bank_account_kind", bank_account_type);
            args.put("bank_account_number", bank_account_number);
            callServer(args);
        } else {
            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Datos de pago");
            builder.setMessage("Recuerda que debes completar todos tus datos de pago antes de postular a ofertas de trabajo!");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callServer(args);
                        }
                    });
            builder.show();
        }
    }

    private void callServer( HashMap<String, Object> args){

        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(getActivity(), "profile/")
                .PATCH()
                .tokenized(true)
                .parameters(args)
                .objectReturnListener(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        builder.setTitle("Actualización de perfil");
                        builder.setMessage("Tu perfil se ha actualizado con éxito!");
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        getActivity().getSupportFragmentManager().popBackStack();

                                        listener.updateUser();
                                    }
                                });
                        builder.show();
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                    /*signUpButton.setEnabled(true);
                    signUpButton.setAlpha(1f);
                    cancelButton.setEnabled(true);
                    cancelButton.setAlpha(1f);
                    showProgress(false);*/
                        Log.d("createAccount", "volleyError: " + volleyError.toString());

                        String message = "";
                        if (volleyError instanceof NetworkError) {
                            message = getString(R.string.error_network);
                        } else if (volleyError instanceof ServerError) {
                            if(volleyError.networkResponse != null){
                                Log.d("createAccount", "serverError: " + new String(volleyError.networkResponse.data));
                                try {
                                    JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));

                                    String firstNameError = checkForError(responseObject, "first_name");
                                    String lastNameError = checkForError(responseObject, "last_name");
                                    String rutError = checkForError(responseObject, "rut");
                                    String emailError = checkForError(responseObject, "email");
                                    String passwordError = checkForError(responseObject, "password");
                                    String bankNameError = checkForError(responseObject, "bank_id");
                                    String bankAccountNumberError = checkForError(responseObject, "bank_account_number");
                                    String bankAccountType = checkForError(responseObject, "bank_account_kind");

                                    String firstEducational = checkForError(responseObject, "educational_institution");
                                    String yearError = checkForError(responseObject, "enrollment_year");
                                    String careerError = checkForError(responseObject, "career");


                                    if(firstNameError != null)
                                        name.setError(firstNameError);
                                    if(lastNameError != null)
                                        lastName.setError(lastNameError);
                                    if(emailError != null)
                                        mail.setError(emailError);
                                    if(passwordError != null)
                                        password.setError(passwordError);
                                    if(rutError != null)
                                        rut.setError(rutError);

                                    if(bankNameError != null)
                                        ((TextView)bankSpinner.getSelectedView()).setError(bankNameError);
                                    if(bankAccountType != null)
                                        ((TextView)accountSpinner.getSelectedView()).setError(bankAccountType);
                                    if(bankAccountNumberError != null)
                                        account.setError(bankAccountNumberError);

                                    if(firstEducational != null)
                                        ((TextView)universitySpinner.getSelectedView()).setError(firstEducational);
                                    if(careerError != null)
                                        careerTextView.setError(careerError);
                                    if(yearError != null)
                                        yearTextView.setError(yearError);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            return;
                        } else if (volleyError instanceof AuthFailureError) {
                            message = getString(R.string.error_incorrect_password_or_login);
                        } else if (volleyError instanceof ParseError) {
                            message = getString(R.string.error_parser);
                        } else if (volleyError instanceof TimeoutError) {
                            message = getString(R.string.error_timeout);
                        }

                        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                        builder.setTitle(getString(R.string.error_creating_account));
                        builder.setMessage(message);
                        builder.setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        builder.show();
                    }
                })
                .build();
        sc.execute();
    }

    private static String checkForError(JSONObject object, String tag){
        try {
            if(object.has(tag)){
                JSONArray errorsArray = null;
                errorsArray = object.getJSONArray(tag);
                String errors = "";
                for(int i=0; i<errorsArray.length(); i++){
                    errors += errorsArray.getString(i);
                }
                return errors;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
