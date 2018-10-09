package kiwigroup.yodelego;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import kiwigroup.yodelego.server.ServerCommunication;

public class RegisterActivity extends AppCompatActivity implements OnRegisterFragmentListener{

    public static final int PICK_IMAGE = 555;

    private RegisterMainFragment mainFragment;
    private RegisterStudentFragment studentFragment;
    private RegisterEndFragment endFragment;
    private Fragment currentFragment;
    private Bitmap image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainFragment = RegisterMainFragment.newInstance();
        addFragmentToMainContent(mainFragment, true, getString(R.string.id_main_fragment));

    }

    @Override
    public boolean onSupportNavigateUp() {
        if(getSupportFragmentManager().getFragments().size() > 1)
            getSupportFragmentManager().popBackStack();
        else
            goToLogin();
        return true;
    }

    @Override
    public void onBackPressed() {
        if(getSupportFragmentManager().getFragments().size() > 1)
            super.onBackPressed();
        else
            goToLogin();
    }

    @Override
    public void addFragmentToMainContent(Fragment fragment, boolean addToBackStack, String fragmentId) {
        if (fragment != null) {
            currentFragment = fragment;
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.container, fragment, fragmentId);
            ft.addToBackStack(null);
            ft.commit();
        }
    }

    @Override
    public void getEducationalInstitutions(final OnEducationalInstitutionsListener listener) {
        Log.d("RegisterActivity", "-call: getEducationalInstitutions");
        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "/educational-institutions")
                .GET()
                .tokenized(false)
                .arrayReturnListener(new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response != null) {
                            LinkedHashMap<String, Integer> educational = new LinkedHashMap<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    Log.d("RegisterActivity", "-call: getEducationalInstitutions " + object.toString());
                                    int id = object.getInt("id");
                                    String name = object.getString("name");
                                    educational.put(name, id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onError(getString(R.string.error_json_exception));
                                    return;
                                }
                            }
                            listener.onEducationalInstitutionsResponse(educational);
                        }
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("getEducationalInstitut", "volleyError: " + volleyError.toString());
                        String message = "";
                        if (volleyError instanceof NetworkError) {
                            message = getString(R.string.error_network);
                        } else if (volleyError instanceof ServerError) {
                            if(volleyError.networkResponse != null){
                                try {
                                    JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                                    JSONObject errorsObject = responseObject.getJSONObject("error");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (volleyError instanceof AuthFailureError) {
                            message = getString(R.string.error_incorrect_password_or_login);
                        } else if (volleyError instanceof ParseError) {
                            message = getString(R.string.error_parser);
                        } else if (volleyError instanceof TimeoutError) {
                            message = getString(R.string.error_timeout);
                        }
                        listener.onError(message);
                    }
                })
                .build();
        sc.execute();
    }

    @Override
    public void getCareerCategories(final OnCareerCategoriesListener listener) {
        Log.d("RegisterActivity", "-call: getCareerCategories");
        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "/career-categories")
                .GET()
                .tokenized(false)
                .arrayReturnListener(new Response.Listener<JSONArray> (){
                    @Override
                    public void onResponse(JSONArray response) {
                        if(response != null) {
                            LinkedHashMap<String, Integer> categories = new LinkedHashMap<>();
                            for (int i = 0; i < response.length(); i++) {
                                try {
                                    JSONObject object = response.getJSONObject(i);
                                    Log.d("RegisterActivity", "-call: getCareerCategories " + object.toString());
                                    int id = object.getInt("id");
                                    String name = object.getString("name");
                                    categories.put(name, id);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    listener.onError(getString(R.string.error_json_exception));
                                    return;
                                }
                            }
                            listener.onCareerCategoriesResponse(categories);
                        }
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        Log.d("RegisterActivity", "volleyError: " + volleyError.toString());
                        String message = "";
                        if (volleyError instanceof NetworkError) {
                            message = getString(R.string.error_network);
                        } else if (volleyError instanceof ServerError) {
                            if(volleyError.networkResponse != null){
                                try {
                                    JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                                    JSONObject errorsObject = responseObject.getJSONObject("error");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        } else if (volleyError instanceof AuthFailureError) {
                            message = getString(R.string.error_incorrect_password_or_login);
                        } else if (volleyError instanceof ParseError) {
                            message = getString(R.string.error_parser);
                        } else if (volleyError instanceof TimeoutError) {
                            message = getString(R.string.error_timeout);
                        }
                        listener.onError(message);
                    }
                })
                .build();
        sc.execute();
    }

    @Override
    public void createAccount(boolean student,
                              String name,
                              String lastName,
                              String rut,
                              final String email,
                              final String phone,
                              final String password,
                              int university,
                              int career_area,
                              String career,
                              String year,
                              int bank_id,
                              int bank_account_type,
                              String bank_account_number,
                             Bitmap profile ){
        final HashMap<String, Object> args = new HashMap<>();

        args.put("first_name", name);
        args.put("last_name", lastName);
        args.put("rut", rut);
        args.put("email", email);
        args.put("phone_number", phone);
        args.put("password", password);
        if (university >= 0)
            args.put("educational_institution_id", university);
        if (career_area >= 0)
            args.put("career_category_id", career_area);
        if (year != null)
            args.put("enrollment_year", year);
        if (career != null)
            args.put("career", career);

        if (bank_id >= 1 && bank_account_type >= 1 && bank_account_number != null && !bank_account_number.isEmpty()) {
            args.put("bank_id", bank_id);
            args.put("bank_account_kind", bank_account_type);
            args.put("bank_account_number", bank_account_number);
            callServer(args, email, password);
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
            builder.setTitle("Datos de pago");
            builder.setMessage("Recuerda que debes completar tus datos de pago para postular a ofertas de trabajo!");
            builder.setPositiveButton("OK",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            callServer(args, email, password);
                        }
                    });
            builder.show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if(resultCode == Activity.RESULT_OK) {
                Uri selectedImage = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    bitmap = rotate(bitmap, getCameraPhotoOrientation(selectedImage));
                    if(bitmap.getWidth() > 256){
                        int width = 256;
                        int heightofBitMap = bitmap.getHeight();
                        int widthofBitMap = bitmap.getWidth();
                        heightofBitMap = width * heightofBitMap / widthofBitMap;
                        widthofBitMap = width;
                        bitmap = Bitmap.createScaledBitmap(bitmap, widthofBitMap, heightofBitMap, true);
                    }
                        onGalleryImageListener.onImageSelected(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public int getCameraPhotoOrientation(Uri imageUri) {
        int rotate = 0;
        try {

            android.support.media.ExifInterface exif;

            InputStream input = getContentResolver().openInputStream(imageUri);
            if (Build.VERSION.SDK_INT > 23)
                exif = new android.support.media.ExifInterface(input);
            else
                exif = new android.support.media.ExifInterface(imageUri.getPath());

            String exifOrientation = exif
                    .getAttribute(android.support.media.ExifInterface.TAG_ORIENTATION);
            Log.d("exifOrientation", exifOrientation);
            int orientation = exif.getAttributeInt(
                    android.support.media.ExifInterface.TAG_ORIENTATION,
                    android.support.media.ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case android.support.media.ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case android.support.media.ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case android.support.media.ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return rotate;
    }

    public static Bitmap rotate(Bitmap bitmap, float degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }

    private MainActivity.OnGalleryImageListener onGalleryImageListener;

    @Override
    public void getImageFromGallery(String message, MainActivity.OnGalleryImageListener listener) {
        this.onGalleryImageListener = listener;
        /*Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, message), PICK_IMAGE);*/

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, message), PICK_IMAGE);
    }

    private void callServer(HashMap<String, Object> args, final String email, final String password){
        for (Iterator i = args.keySet().iterator(); i.hasNext(); ) {
            String key = (String) i.next();
            if(args.get(key) instanceof String){
                String value = (String) args.get(key);
                Log.d("Register", key + " = " + value);
            }
            if(args.get(key) instanceof Integer){
                Integer value = (Integer) args.get(key);
                Log.d("Register", key + " = " + value);
            }
        }

        Response.ErrorListener errorListener = new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

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
                            String profilePictureError = checkForError(responseObject, "profile_picture");

                            RegisterMainFragment mainFragment = (RegisterMainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.id_main_fragment));
                            RegisterStudentFragment studentFragment = (RegisterStudentFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.id_student_fragment));

                            if( firstNameError != null ||
                                    lastNameError != null ||
                                    rutError != null ||
                                    emailError != null ||
                                    passwordError != null ||
                                    bankNameError != null ||
                                    bankAccountNumberError != null ||
                                    bankAccountType != null ||
                                    profilePictureError != null) {

                                Log.d("createAccount", "---> errors on first form");

                                if(studentFragment != null){
                                    Log.d("createAccount", "---> doing pop");
                                    getSupportFragmentManager().popBackStack();
                                }

                                mainFragment.updateErrors(
                                        firstNameError,
                                        lastNameError,
                                        emailError,
                                        passwordError,
                                        rutError,
                                        bankNameError,
                                        bankAccountType,
                                        bankAccountNumberError,
                                        profilePictureError);
                            } else {
                                String firstEducational = checkForError(responseObject, "educational_institution");
                                String yearError = checkForError(responseObject, "enrollment_year");
                                String careerError = checkForError(responseObject, "career");

                                if( firstEducational != null ||
                                        yearError != null ||
                                        careerError != null){
                                    Log.d("createAccount", "---> errors on second form");
                                    studentFragment.updateErrors(firstEducational, yearError, careerError);
                                }
                            }
                            message = getString(R.string.error_form);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                } else if (volleyError instanceof AuthFailureError) {
                    message = getString(R.string.error_incorrect_password_or_login);
                } else if (volleyError instanceof ParseError) {
                    message = getString(R.string.error_parser);
                } else if (volleyError instanceof TimeoutError) {
                    message = getString(R.string.error_timeout);
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
        };

        Log.d("createAccount", "********* image == null " + (image == null));

        /*if(image == null){
            ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "users/")
                .POST()
                .tokenized(false)
                .parameters(args)
                .objectReturnListener(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", email);
                        editor.putString("password", password);
                        editor.apply();

                        endFragment = RegisterEndFragment.newInstance();
                        addFragmentToMainContent(endFragment, false, getString(R.string.id_end_fragment));
                    }
                })
                .errorListener(errorListener)
                .build();
            sc.execute();
        } else {*/
            ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "users/")
                .POST()
                .tokenized(false)
                .parameters(args)
                .multipartListener(new Response.Listener<NetworkResponse>() {
                    @Override
                    public void onResponse(NetworkResponse response) {
                        SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", email);
                        editor.putString("password", password);
                        editor.apply();

                        endFragment = RegisterEndFragment.newInstance();
                        addFragmentToMainContent(endFragment, false, getString(R.string.id_end_fragment));
                    }
                }, image)
                .errorListener(errorListener)
                .build();
            sc.execute();
        //}
        /*ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this, "users/")
                .POST()
                .tokenized(false)
                .parameters(args)
                .objectReturnListener(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        SharedPreferences sharedPref = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("username", email);
                        editor.putString("password", password);
                        editor.apply();

                        endFragment = RegisterEndFragment.newInstance();
                        addFragmentToMainContent(endFragment, false, getString(R.string.id_end_fragment));
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {

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

                                    if( firstNameError != null ||
                                            lastNameError != null ||
                                            rutError != null ||
                                            emailError != null ||
                                            passwordError != null ||
                                            bankNameError != null ||
                                            bankAccountNumberError != null ||
                                            bankAccountType != null) {

                                        Log.d("createAccount", "-> showing RegisterMainFragment");

                                        RegisterMainFragment mainFragment = (RegisterMainFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.id_main_fragment));
                                        getSupportFragmentManager().popBackStack();

                                        mainFragment.updateErrors(
                                                firstNameError,
                                                lastNameError,
                                                emailError,
                                                passwordError,
                                                rutError,
                                                bankNameError,
                                                bankAccountType,
                                                bankAccountNumberError);
                                        return;
                                    }

                                    String firstEducational = checkForError(responseObject, "educational_institution");
                                    String yearError = checkForError(responseObject, "enrollment_year");
                                    String careerError = checkForError(responseObject, "career");

                                    if( firstEducational == null ||
                                            yearError == null ||
                                            careerError == null){
                                        Log.d("createAccount", "-> showing RegisterStudentFragment");
                                        RegisterStudentFragment studentFragment = (RegisterStudentFragment) getSupportFragmentManager().findFragmentByTag(getString(R.string.id_student_fragment));
                                        studentFragment.updateErrors(firstEducational, yearError, careerError);
                                        return;
                                    }

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

                        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
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
        sc.execute();*/
    }

    @Override
    public void goToWall(){
        Intent mainIntent = new Intent().setClass(RegisterActivity.this, SplashActivity.class);
        startActivity(mainIntent);
        finish();
    }

    @Override
    public void goToLogin(){
        Intent mainIntent = new Intent().setClass(RegisterActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
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

    public interface OnEducationalInstitutionsListener {
        void onEducationalInstitutionsResponse(LinkedHashMap<String, Integer> response);
        void onError(String error);
    }

    public interface OnCareerCategoriesListener {
        void onCareerCategoriesResponse(LinkedHashMap<String, Integer> response);
        void onError(String error);
    }
}
