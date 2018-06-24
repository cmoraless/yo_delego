package kiwigroup.yodelego;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.server.ServerCommunication;

import static android.view.View.GONE;

public class OfferDetailsActivity extends AppCompatActivity {

    private TextView publicationOwner;
    private LinearLayout accept_button;
    private LinearLayout bottom_message;
    private TextView bottom_message_text;
    private TextView textViewButtonText;
    private ImageView imageViewButton;
    private Offer offer;
    private TextView textViewTitle;
    private TextView textViewCreationDate;
    private RelativeLayout taskDatesLayout;
    private TextView textViewDates;
    private TextView textViewResume;
    private TextView textViewAmount;
    private TextView textViewRating;
    private TextView textViewAddress;

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
        textViewTitle = findViewById(R.id.title);
        textViewCreationDate = findViewById(R.id.creationDate);
        taskDatesLayout = findViewById(R.id.task_dates_layout);
        textViewDates = findViewById(R.id.dates);
        textViewResume = findViewById(R.id.publication_resume);
        textViewAmount = findViewById(R.id.dailyWage);
        accept_button = findViewById(R.id.accept_button);
        bottom_message = findViewById(R.id.bottom_message_bar);
        bottom_message_text = findViewById(R.id.bottom_message_text);
        textViewButtonText = findViewById(R.id.button_text);
        imageViewButton = findViewById(R.id.button_icon);

        textViewRating = findViewById(R.id.rating);
        textViewAddress = findViewById(R.id.address);

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
            if(offer != null)
                if(offer.isApplied()) {
                    setUpForOffer(offer);
                    setUpForApplication(offer.getApplication());
                } else {
                    setUpForOffer(offer);
                }
        }
    }

    private void setUpForOffer(final Offer offer){
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        publicationOwner.setText(Html.fromHtml(String.format("publicado por <b>%s</b>", offer.getPublisher())));
        textViewTitle.setText(offer.getTitle());
        textViewCreationDate.setText(DateUtils.getRelativeTimeSpanString(offer.getCreationDate().getTime(), new Date().getTime(),0L, DateUtils.FORMAT_ABBREV_ALL));
        textViewResume.setText(offer.getSummary());
        textViewAddress.setText(offer.getCommune());
        if(offer.getStartDate() != null && offer.getEndDate() != null && !offer.getStartDate().equals(offer.getEndDate())){
            textViewDates.setText(
                    String.format(Locale.US, "%s a %s",
                            DateUtils.getRelativeTimeSpanString(offer.getStartDate().getTime(), new Date().getTime(), 0L, DateUtils.FORMAT_ABBREV_ALL),
                            DateUtils.getRelativeTimeSpanString(offer.getEndDate().getTime(), new Date().getTime(), 0L, DateUtils.FORMAT_ABBREV_ALL)));
        } else if((offer.getStartDate() != null)){
            textViewDates.setText(
                    String.format(Locale.US, "%s",
                            DateUtils.getRelativeTimeSpanString(offer.getStartDate().getTime(), new Date().getTime(), 0L, DateUtils.FORMAT_ABBREV_ALL)));
        } else {
            taskDatesLayout.setVisibility(GONE);
        }
        accept_button.setVisibility(View.VISIBLE);

        textViewAmount.setText(String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(offer.getTotalWage())));
        if(offer.getStatus() != Offer.OfferStatus.ENTERED){
            /*textViewButtonText.setText("Cerrado");
            accept_button.setEnabled(false);
            accept_button.getBackground().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(),
                            R.color.colorLightGreyText),
                    PorterDuff.Mode.SRC);
            imageViewButton.setVisibility(GONE);*/
            accept_button.setVisibility(GONE);
            bottom_message.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Lamentablemente esta oferta ha sido cerrada.");
        }
        if(offer.getRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(String.format(Locale.US, "%.1f", offer.getRating()));
    }

    DialogInterface currenRatingDialog;

    private void setUpForApplication(final Application application){
        if(application.getApplicationStatus() == Application.ApplicationStatus.ACCEPTED){
            accept_button.setEnabled(false);
            textViewButtonText.setText("adjudicada");
            accept_button.getBackground().setColorFilter(
                    ContextCompat.getColor(getApplicationContext(),
                            R.color.colorAdjudicated),
                    PorterDuff.Mode.SRC);
            imageViewButton.setVisibility(View.VISIBLE);
            imageViewButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_accepted_offer));
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.REJECTED) {
            accept_button.setVisibility(GONE);
            bottom_message.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Lamentablemente no has sido seleccionado para esta tarea.");
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.REVISION) {
            accept_button.setVisibility(GONE);
            bottom_message.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Has postulado a esta tarea, te avisaremos cuando sea adjudicada.");
        }
        if(application.getRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(String.format(Locale.US, "%.1f", application.getRating()));

        View ratingLayout = findViewById(R.id.rating_layout);
        ratingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if(application.getRating() == -1.0f){
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
                    builder.setMessage(Html.fromHtml("¿Qué calificación le das a <b>" + offer.getTitle()+ "</b>?"));
                    builder.setCancelable(false);
                    builder.setPositiveButton("Cancelar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int whichButton) {
                            dialog.dismiss();
                        }
                    });
                    currenRatingDialog = builder.show();
                }*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent returnIntent = new Intent();
        setResult(accepted_offer ? Activity.RESULT_OK : Activity.RESULT_CANCELED, returnIntent);
        super.onBackPressed();
    }

    boolean accepted_offer;

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
                        accept_button.setVisibility(GONE);
                        bottom_message.setVisibility(View.VISIBLE);
                        accepted_offer = true;
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
            Intent returnIntent = new Intent();
            setResult(accepted_offer ? Activity.RESULT_OK : Activity.RESULT_CANCELED, returnIntent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void rateApplication(final float rating){
        HashMap<String, Object> args = new HashMap<>();
        args.put("rating", rating);

        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this,
            String.format(Locale.US,"applications/%d/reviews/", offer.getId()))
            .POST()
            .tokenized(true)
            .parameters(args)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    offer.setRating(rating);
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
