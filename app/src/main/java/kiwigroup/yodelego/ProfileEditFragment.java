package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

import static android.view.View.GONE;

public class ProfileEditFragment extends Fragment {
    private View mProgressView;

    private LinearLayout baseFormLayout;
    private TextInputEditText name;
    private TextInputEditText lastName;
    private TextInputEditText rut;
    private TextInputEditText phone;
    private TextInputEditText mail;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;
    private Button editButton;
    private RelativeLayout formLayout;

    private LinearLayout studentFormLayout;
    private SearchableSpinner universitySpinner;
    private TextInputEditText careerTextView;
    private TextInputEditText yearTextView;
    private TextInputLayout yearLayout;
    private ArrayAdapter<String> universityAdapter;

    private LinearLayout imageLayout;

    private CircleImageView image;
    private TextInputLayout rutLayout;
    private TextInputLayout mailLayout;

    private SearchableSpinner bankSpinner;
    private SearchableSpinner accountSpinner;
    private SearchableSpinner categorySpinner;
    private TextInputEditText account;
    private ArrayAdapter<String> bankAdapter;
    private ArrayAdapter<String> accountAdapter;

    private User user;
    private OnUserFragmentsListener listener;

    private Map educationInstitutions;
    private Map careerCategories;

    private Bitmap imageBitmap;

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

        imageLayout = view.findViewById(R.id.imageLayout);
        imageLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.getImageFromGallery("Selecciona tu imagen de perfil", new MainActivity.OnGalleryImageListener() {
                    @Override
                    public void onImageSelected(Bitmap bitmap) {
                        imageBitmap = bitmap;
                        image.setImageBitmap(bitmap);
                    }
                });
            }
        });
        image = view.findViewById(R.id.profile_image);

        name = view.findViewById(R.id.first_name);
        lastName = view.findViewById(R.id.last_name);
        rut = view.findViewById(R.id.rut);
        phone = view.findViewById(R.id.phone);
        mail = view.findViewById(R.id.mail);
        password = view.findViewById(R.id.password);
        confirmPassword = view.findViewById(R.id.confirm_password);

        rutLayout = view.findViewById(R.id.id_layout);
        rutLayout.setVisibility(View.GONE);
        mailLayout = view.findViewById(R.id.mail_layout);
        mailLayout.setVisibility(GONE);

        studentFormLayout = view.findViewById(R.id.academic_info);
        studentFormLayout.setVisibility(GONE);

        universitySpinner = view.findViewById(R.id.spinner);
        categorySpinner = view.findViewById(R.id.cat_spinner);
        careerTextView = view.findViewById(R.id.career);
        yearTextView = view.findViewById(R.id.enrollment_year);
        yearLayout = view.findViewById(R.id.year_layout);

        bankSpinner = view.findViewById(R.id.bank_spinner);
        accountSpinner = view.findViewById(R.id.account_spinner);
        account = view.findViewById(R.id.account_number);

        editButton = view.findViewById(R.id.edit_btn);

        universitySpinner.setTitle("Universidad o Instituto");
        universitySpinner.setPositiveButton("OK");

        categorySpinner.setTitle("Área de estudio");
        categorySpinner.setPositiveButton("OK");

        if(user.getEducationalInstitution() != null && !user.getEducationalInstitution().isEmpty()) {
            showProgress(true);
            studentFormLayout.setVisibility(View.VISIBLE);
            careerTextView.setText(user.getCareer());
            yearTextView.setText(String.valueOf(user.getEnrollmentYear()));
            yearTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final NumberPicker numPicker = new NumberPicker(getActivity());
                    numPicker.setMaxValue(Calendar.getInstance().get(Calendar.YEAR));
                    numPicker.setValue(Calendar.getInstance().get(Calendar.YEAR));
                    numPicker.setMinValue(1920);
                    numPicker.setWrapSelectorWheel(false);

                    android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                    builder.setView(numPicker);
                    builder.setTitle("Año de ingreso");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            yearTextView.setText(String.valueOf(numPicker.getValue()));
                        }
                    });
                    builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            yearTextView.setText(String.valueOf(user.getEnrollmentYear()));
                        }
                    });
                    builder.create();
                    builder.show();
                }
            });

            listener.getEducationalInstitutions(new RegisterActivity.OnEducationalInstitutionsListener() {
                @Override
                public void onEducationalInstitutionsResponse(LinkedHashMap<String, Integer> response) {
                    educationInstitutions = response;
                    List<String> values = new ArrayList<>(response.keySet());

                    universityAdapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, values);
                    universitySpinner.setAdapter(universityAdapter);
                    for(int i = 0; i < universitySpinner.getCount(); i++){
                        if(universitySpinner.getAdapter().getItem(i).equals(user.getEducationalInstitution()))
                            universitySpinner.setSelection(i);
                    }

                    careerTextView.setText(user.getCareer());
                    yearTextView.setText(String.valueOf(user.getEnrollmentYear()));

                    if (educationInstitutions != null && careerCategories != null){
                        showProgress(false);
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
                    if (educationInstitutions != null && careerCategories != null)
                        showProgress(false);
                }
            });

            listener.getCareerCategories(new RegisterActivity.OnCareerCategoriesListener() {
                @Override
                public void onCareerCategoriesResponse(LinkedHashMap<String, Integer> response) {
                    careerCategories = response;
                    List<String> values = new ArrayList<>(response.keySet());
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, values);
                    categorySpinner.setAdapter(adapter);

                    for(int i = 0; i < categorySpinner.getCount(); i++){
                        if(categorySpinner.getAdapter().getItem(i).equals(user.getCareerCategory()))
                            categorySpinner.setSelection(i);
                    }

                    if (educationInstitutions != null && careerCategories != null){
                        showProgress(false);
                    }
                }

                @Override
                public void onError(String error) {

                }
            });
        }
        bankAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.banks_array));
        bankAdapter.setDropDownViewResource(R.layout.spinner_layout);
        bankSpinner.setAdapter(bankAdapter);

        accountAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.account_array));
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
        rut.setText(user.getRut());
        rut.setEnabled(true);
        phone.setText(user.getPhone());
        phone.setEnabled(true);
        password.setEnabled(true);
        confirmPassword.setEnabled(true);
        password.setVisibility(GONE);
        confirmPassword.setVisibility(GONE);

        if(user.getProfileImage() != null && !user.getProfileImage().isEmpty()){
            Picasso.get().load(user.getProfileImage()).placeholder(R.drawable.ic_profile).into(image);
        }

        bankSpinner.setClickable(true);
        if(user.getBank() != null && !user.getBank().isEmpty()){
            for(int i = 0; i < bankSpinner.getCount(); i++){
                if(bankSpinner.getAdapter().getItem(i).equals(user.getBank().toUpperCase()))
                    bankSpinner.setSelection(i);
            }
        }
        accountSpinner.setClickable(true);
        if(user.getAccountType() != -1){
            accountSpinner.setSelection(user.getAccountType());
        }
        account.setEnabled(true);
        if(user.getAccountNumber() != null && !user.getAccountNumber().isEmpty()){
            account.setText(user.getAccountNumber());
        }
    }

    private void showProgress(final boolean show) {
        for (int i = 0; i < formLayout.getChildCount(); i++) {
            View child = formLayout.getChildAt(i);
            child.setVisibility(show ? GONE : View.VISIBLE);
            child.setEnabled(!show);
        }

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : GONE);
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
        phone.setError(null);

        ((TextView)bankSpinner.getSelectedView()).setError(null);
        ((TextView)accountSpinner.getSelectedView()).setError(null);

        if(studentFormLayout.getVisibility() == View.VISIBLE){
            ((TextView)categorySpinner.getSelectedView()).setError(null);
            account.setError(null);
            ((TextView)universitySpinner.getSelectedView()).setError(null);
            careerTextView.setError(null);
            yearTextView.setError(null);
        }

        String firstName = name.getText().toString();
        String lastN = lastName.getText().toString();
        String phoneText = phone.getText().toString();

        int bank = bankSpinner.getSelectedItemPosition();
        int accountType = accountSpinner.getSelectedItemPosition();
        String acc = account.getText().toString();

        /*if(studentFormLayout.getVisibility() == View.VISIBLE){
            university = universitySpinner.getSelectedItem().toString();
            career = careerTextView.getText().toString();
            year = yearTextView.getText().toString();
            category = categorySpinner.getSelectedItemPosition();
        }*/

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
        if(TextUtils.isEmpty(phoneText)){
            phone.setError(getString(R.string.error_field_required));
            focusView = phone;
            cancel = true;
        } else if(!isValidPhone(phoneText)){
            phone.setError(getString(R.string.error_phone_not_valid));
            focusView = phone;
            cancel = true;
        }

        String university = "";
        int universityId = -1;
        String category = "";
        int categoryId = -1;
        String career = "";
        String year = "";

        if(studentFormLayout.getVisibility() == View.VISIBLE){
            university = universitySpinner.getSelectedItem().toString();
            category = categorySpinner.getSelectedItem().toString();
            career = careerTextView.getText().toString();
            year = yearTextView.getText().toString();
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
                ((TextView)categorySpinner.getSelectedView()).setError(getString(R.string.error_field_required));
                focusView = categorySpinner.getSelectedView();
                cancel = true;
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
                    phoneText,
                    universityId,
                    categoryId,
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
                              final String phone,
                              int university,
                              int career_area,
                              String career,
                              String year,
                              int bank_id,
                              int bank_account_type,
                              String bank_account_number){
        final HashMap<String, Object> args = new HashMap<>();
        args.put("first_name", name);
        args.put("last_name", lastName);
        args.put("phone_number", phone);
        if (university >= 0)
            args.put("educational_institution_id", university);
        if (year != null && !year.isEmpty())
            args.put("enrollment_year", year);
        if (career != null && !career.isEmpty())
            args.put("career", career);
        if (career_area >= 0)
            args.put("career_category_id", career_area);

        if (bank_id >= 1 && bank_account_type >= 1 && bank_account_number != null && !bank_account_number.isEmpty()) {
            Log.d("EditFragment", "**** bank_id: " + bank_id);
            Log.d("EditFragment", "**** bank_account_kind: " + bank_account_type);
            Log.d("EditFragment", "**** bank_account_number: " + bank_account_number);
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
                /*.multipartListener(new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
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
                }, imageBitmap)*/
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

    private static boolean isValidRut(String rut) {

        boolean validate = false;
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
                validate = true;
            }

        } catch (Exception e) {
            return false;
        }
        return validate;
    }

    private boolean isValidPhone(String phone){
        return phone.length() <= 12 && android.util.Patterns.PHONE.matcher(phone).matches();
    }
}
