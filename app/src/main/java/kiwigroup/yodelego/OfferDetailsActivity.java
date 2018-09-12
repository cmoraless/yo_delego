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
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.server.ServerCommunication;

import static android.view.View.GONE;

public class OfferDetailsActivity extends AppCompatActivity {
    private ImageView imageViewHeader;
    private TextView publicationOwner;
    private LinearLayout accept_button;
    private LinearLayout bottom_message;
    private TextView bottom_message_text;
    private LinearLayout action_button;
    private TextView cancel_text;
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
    private boolean complete;

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

        imageViewHeader = findViewById(R.id.header);
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
        action_button = findViewById(R.id.cancel_button);
        cancel_text = findViewById(R.id.cancel_text);
        textViewButtonText = findViewById(R.id.button_text);
        imageViewButton = findViewById(R.id.button_icon);

        textViewRating = findViewById(R.id.publisherRating);
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

        action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offer != null)
                    if(offer.isApplied()) {
                        if(offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.REVISION && !complete){
                            AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                            builder.setTitle(getString(R.string.application));
                            builder.setMessage(Html.fromHtml(String.format(getString(R.string.cancel_application), offer.getTitle())));
                            builder.setPositiveButton("Sí",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        cancelApplication(offer.getApplication());
                                        dialog.dismiss();
                                    }
                                });
                            builder.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                            builder.show();
                        } else if(complete){
                            final AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            final View view = inflater.inflate(R.layout.dialog_rank2, null);
                            final RatingBar ratingBar = view.findViewById(R.id.dialog_ratingbar);
                            final TextView text = view.findViewById(R.id.text);
                            text.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if (!hasFocus) {
                                        InputMethodManager inputMethodManager =
                                                (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                }
                            });
                            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                                @Override
                                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                                    ((AlertDialog) currenRatingDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(rating > 0);
                                    InputMethodManager inputMethodManager =
                                            (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            });
                            ratingBar.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    InputMethodManager inputMethodManager =
                                            (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                                    inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                }
                            });
                            ratingBar.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                                @Override
                                public void onFocusChange(View v, boolean hasFocus) {
                                    if(hasFocus){
                                        InputMethodManager inputMethodManager =
                                                (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
                                        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                                    }
                                }
                            });
                            builder.setView(view);
                            builder.setTitle("Calificación de postulación");
                            builder.setMessage(Html.fromHtml("¿Qué calificación le das a <b>" + offer.getTitle()+ "</b>?"));
                            builder.setCancelable(true);
                            builder.setPositiveButton("Calificar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    if(ratingBar.getRating() > 0){
                                        //rateApplication(offer.getApplication().getId() , ratingBar.getPublisherRating() , text.getText().toString());
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                            currenRatingDialog = builder.show();
                            ((AlertDialog) currenRatingDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
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
        if(offer.getImages() != null && offer.getImages().size() > 0){
            imageViewHeader.setVisibility(View.VISIBLE);
            Picasso.get().load(offer.getImages().get(0)).into(imageViewHeader);
        } else {
            imageViewHeader.setVisibility(View.GONE);
        }
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
            bottom_message_text.setText("Lamentablemente esta oferta ha sido cerrada");
        }

        if(!offer.isApplied()) {
            bottom_message.setVisibility(View.GONE);
        }
        if(offer.getPublisherRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(String.format(Locale.US, "%.1f", offer.getPublisherRating()));
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
            bottom_message_text.setText("Lamentablemente no has sido seleccionado para esta tarea");
            action_button.setVisibility(GONE);
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.REVISION) {
            accept_button.setVisibility(GONE);
            bottom_message.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Has postulado a esta tarea, te avisaremos cuando sea adjudicada");
            action_button.setVisibility(View.VISIBLE);
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.CANCELED_BY_APPLICANT) {
            accept_button.setVisibility(GONE);
            bottom_message.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Has cancelado tu postulación");
            action_button.setVisibility(GONE);
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.CANCELED_BY_PUBLISHER) {
            accept_button.setVisibility(GONE);
            bottom_message.setVisibility(View.VISIBLE);
            bottom_message_text.setText("La oferta ha sido cancelada");
            action_button.setVisibility(GONE);
        }
        if(application.getRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(String.format(Locale.US, "%.1f", application.getRating()));

        Date currentTime = Calendar.getInstance().getTime();

        /*if((offer.getEndDate() == null || currentTime.after(offer.getEndDate())) &&
                offer.getStatus() != Offer.OfferStatus.CANCELED &&
                offer.getStatus() != Offer.OfferStatus.DEACTIVATED &&
                offer.getStatus() != Offer.OfferStatus.PAUSED &&
                offer.getStatus() != Offer.OfferStatus.CLOSED &&
                application.getApplicationStatus() == Application.ApplicationStatus.ACCEPTED){
            checkReviews(offer.getApplication());
        }*/

        /*complete = true;
        accept_button.setVisibility(GONE);
        bottom_message.setVisibility(View.VISIBLE);
        bottom_message_text.setText("Puedes calificar esta oferta");
        action_button.setVisibility(View.VISIBLE);
        cancel_text.setText("Calificar");*/
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
                        bottom_message_text.setText("Has postulado a esta tarea, te avisaremos cuando sea adjudicada");
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

    private void rateApplication(final long application_id, final float rating, final String text){
        HashMap<String, Object> args = new HashMap<>();
        args.put("rating", Math.round(rating));
        args.put("text", text);

        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this,
            String.format(Locale.US,"applications/%d/reviews/", application_id))
            .POST()
            .tokenized(true)
            .parameters(args)
            /*.nullableListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                    builder.setTitle(getString(R.string.application));
                    builder.setMessage("Hemos calificado exitosamente la oferta");
                    builder.setNegativeButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    builder.show();
                }
            })*/
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                    builder.setTitle(getString(R.string.application));
                    builder.setMessage("Hemos calificado exitosamente la oferta");
                    builder.setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    builder.show();

                    bottom_message.setVisibility(GONE);
                    accept_button.setVisibility(View.VISIBLE);
                    accept_button.setEnabled(false);
                    textViewButtonText.setText("Completado");
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
                            Log.d("OfferDetail", "----------------->" + new String(volleyError.networkResponse.data));
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

    private void cancelApplication(Application application){
        HashMap<String, Object> args = new HashMap<>();
        args.put("status", Application.ApplicationStatus.toInt(Application.ApplicationStatus.CANCELED_BY_APPLICANT));
        Log.d("****cancelApplication", " -----> application new status: " + Application.ApplicationStatus.toInt(Application.ApplicationStatus.CANCELED_BY_APPLICANT));
        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this,
            String.format(Locale.US,"applications/%d/", application.getId()))
            .PATCH()
            .tokenized(true)
            .parameters(args)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);
                    finish();
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


    private void checkReviews(Application application){
        ServerCommunication MyApplicationsWS = new ServerCommunication.ServerCommunicationBuilder(this,
            String.format(Locale.US,"applications/%d/reviews/", application.getId()))
            .GET()
            .tokenized(true)
            .objectReturnListener(new Response.Listener<JSONObject> () {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        Log.d("MainActivity", "**** reviews response: " + response.toString());
                        bottom_message.setVisibility(GONE);
                        accept_button.setEnabled(false);
                        textViewButtonText.setText("Completado");
                    }
                }
            })
            .errorListener(new Response.ErrorListener() {
               @Override
               public void onErrorResponse(VolleyError error) {
                   error.printStackTrace();

                   NetworkResponse networkResponse = error.networkResponse;
                   if (networkResponse != null) {
                       Log.e("Status code", String.valueOf(networkResponse.statusCode));

                       complete = true;
                       accept_button.setVisibility(GONE);
                       bottom_message.setVisibility(View.VISIBLE);
                       bottom_message_text.setText("Puedes calificar esta oferta");
                       action_button.setVisibility(View.VISIBLE);
                       cancel_text.setText("Calificar");
                   }

               }
           }).build();
        MyApplicationsWS.execute();
    }


}
