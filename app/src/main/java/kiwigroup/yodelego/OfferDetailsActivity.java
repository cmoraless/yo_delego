package kiwigroup.yodelego;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import angtrim.com.fivestarslibrary.FiveStarsDialog;
import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

public class OfferDetailsActivity extends AppCompatActivity {

    private TextView publicationOwner;
    private Button accept_button;
    private Offer offer;
    private Application application;
    private TextView TextViewTitle;
    private TextView textViewDate;
    private TextView textViewResume;
    private TextView textViewAmount;
    private TextView textViewRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offer_details);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayShowTitleEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        publicationOwner = findViewById(R.id.publication_owner);
        TextViewTitle = findViewById(R.id.title);
        textViewDate = findViewById(R.id.date);
        textViewResume = findViewById(R.id.publication_resume);
        textViewAmount = findViewById(R.id.dailyWage);
        accept_button = findViewById(R.id.accept_button);
        textViewRating = findViewById(R.id.rating);

        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                builder.setTitle(getString(R.string.application));
                builder.setMessage(Html.fromHtml(String.format(getString(R.string.ask_application), offer.getTitle())));
                builder.setPositiveButton("Postular",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                acceptOffer();
                            }
                        });
                builder.setNegativeButton("Cancelar",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(getIntent().hasExtra("offer")){
            offer = (Offer) bundle.getSerializable("offer");
            setUpForOffer(offer);
        }
        if(getIntent().hasExtra("application")){
            application = (Application) bundle.getSerializable("application");
            setUpForApplication(application);
        }
    }

    private void setUpForOffer(final Offer offer){
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        publicationOwner.setText(Html.fromHtml(String.format("publicado por <b>%s</b>", offer.getPublisher())));
        TextViewTitle.setText(offer.getTitle());
        textViewDate.setText(DateUtils.getRelativeTimeSpanString(offer.getDate().getTime(), new Date().getTime(),0L, DateUtils.FORMAT_ABBREV_ALL));
        textViewResume.setText(offer.getSummary());
        textViewAmount.setText(String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(offer.getTotalWage())));
        if(offer.getStatus() != Offer.OfferStatus.ENTERED){
            accept_button.setText("cerrada");
            accept_button.setEnabled(false);
            accept_button.getBackground().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(),
                            R.color.colorLightGreyText),
                    PorterDuff.Mode.SRC);
            for (Drawable drawable : accept_button.getCompoundDrawablesRelative()) {
                if(drawable != null) {
                    drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorLightGreyText), PorterDuff.Mode.SRC);
                }
            }
        }
        if(offer.getRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(String.format("%.1f", offer.getRating()));
    }


    DialogInterface currenRatingDialog;

    private void setUpForApplication(final Application application){
        setUpForOffer(application.getOffer());
        if(application.getApplicationStatus() == Application.ApplicationStatus.ACCEPTED){
            accept_button.setText("adjudicada");
            accept_button.setEnabled(false);
            accept_button.getBackground().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(),
                            R.color.colorAdjudicated),
                    PorterDuff.Mode.SRC);
            for (Drawable drawable : accept_button.getCompoundDrawablesRelative()) {
                if(drawable != null) {
                    drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorAdjudicated), PorterDuff.Mode.SRC);
                }
            }
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.REJECTED) {
            accept_button.setText("no adjudicada");
            accept_button.setEnabled(false);
            accept_button.getBackground().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(),
                            R.color.colorLightGreyText),
                    PorterDuff.Mode.SRC);
            for (Drawable drawable : accept_button.getCompoundDrawablesRelative()) {
                if(drawable != null) {
                    drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorLightGreyText), PorterDuff.Mode.SRC);
                }
            }
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.REVISION) {
            accept_button.setText("en revisión");
            accept_button.setEnabled(false);
            accept_button.getBackground().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(),
                            R.color.colorAcademicShape),
                    PorterDuff.Mode.SRC);
            for (Drawable drawable : accept_button.getCompoundDrawablesRelative()) {
                if(drawable != null) {
                    drawable.setColorFilter(ContextCompat.getColor(getApplicationContext(),
                            R.color.colorAcademicShape), PorterDuff.Mode.SRC);
                }
            }
        }
        if(application.getRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(String.format("%.1f", application.getRating()));

        View ratingLayout = findViewById(R.id.rating_layout);
        ratingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(application.getRating() == -1.0f){
                    DialogInterface di = null;
                    final AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view = inflater.inflate(R.layout.dialog_rank2, null);
                    RatingBar ratingBar = view.findViewById(R.id.dialog_ratingbar);
                    ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                        @Override
                        public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                            currenRatingDialog.dismiss();
                            rateApplication(rating);
                        }
                    });
                    builder.setView(view);
                    builder.setTitle("Calificación de postulación");
                    builder.setMessage(Html.fromHtml("¿Qué calificación le das a <b>" + application.getOffer().getTitle()+ "</b>?"));
                    builder.setCancelable(false);
                    builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    currenRatingDialog = builder.show();
                }
            }
        });
    }

    protected void acceptOffer(){
        Log.d("OfferDetailsActivity", "--- accept offers process ---");
        ServerCommunication serverCommunication = new ServerCommunication.ServerCommunicationBuilder(
                this, String.format(Locale.US,"offers/%d/", offer.getId()))
                .POST()
                .tokenized(true)
                .objectReturnListener(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("OfferDetailsActivity", "onResponse: " + response.toString());
                        accept_button.setBackgroundColor(getResources().getColor(R.color.colorAcademicShape));
                        accept_button.setText(R.string.applicated);
                        accept_button.setEnabled(false);
                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        volleyError.printStackTrace();
                        if (volleyError instanceof NetworkError) {
                        } else if (volleyError instanceof ServerError) {
                            try {
                                JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                                Log.d("acceptOffer", "---> onErrorResponse " + responseObject.toString());
                                String genericError = "";
                                Iterator<String> iter = responseObject.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    String errors = "";
                                    JSONArray errorsArray = responseObject.getJSONArray(key);
                                    for(int i = 0; i < errorsArray.length(); i++){
                                        errors += " " + errorsArray.getString(i);
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (volleyError instanceof AuthFailureError) {
                        } else if (volleyError instanceof ParseError) {
                        } else if (volleyError instanceof TimeoutError) {
                        }
                    }
                })
                .build();
        serverCommunication.execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void rateApplication(final float rating){
        HashMap<String, Object> args = new HashMap<>();
        args.put("rating", rating);

        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this,
            String.format(Locale.US,"applications/%d/reviews/", application.getId()))
            .POST()
            .tokenized(true)
            .parameters(args)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    application.setRating(rating);
                    textViewRating.setText(String.format("%.1f", rating));
                }
            })
            .errorListener(new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    if (volleyError instanceof NetworkError) {
                    } else if (volleyError instanceof ServerError) {
                        try {
                            JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                            Log.d("rateApplication", "---> onErrorResponse " + responseObject.toString());
                            String genericError = "";
                            Iterator<String> iter = responseObject.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                String errors = "";
                                JSONArray errorsArray = responseObject.getJSONArray(key);
                                for(int i = 0; i < errorsArray.length(); i++){
                                    errors += " " + errorsArray.getString(i);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else if (volleyError instanceof AuthFailureError) {
                    } else if (volleyError instanceof ParseError) {
                    } else if (volleyError instanceof TimeoutError) {
                    }
                }
            })
            .build();
        sc.execute();
    }

}
