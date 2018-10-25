package kiwigroup.yodelego;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.KeyEvent;
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
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
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
    private LinearLayout bottom_message_bar;
    private TextView bottom_message_text;
    private LinearLayout bottom_message_bar_button;
    private TextView cancel_text;
    private TextView textViewButtonText;
    private ImageView imageViewButton;
    private Offer offer;
    private TextView textViewTitle;
    private TextView textViewCreationDate;
    private TextView textViewInitDate;
    private TextView textViewEndDate;

    private TextView textViewResume;
    private TextView textViewAmount;
    private TextView textViewRating;
    private TextView textViewAddress;

    private TextView textViewContactTitle;
    private RelativeLayout textViewPhoneLayout;
    private TextView textViewPhone;
    private RelativeLayout textViewEmailLayout;
    private TextView textViewEmail;

    private TextView textViewStartHour;
    private TextView textViewJobHour;

    private RelativeLayout attachLayout;
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
        textViewInitDate = findViewById(R.id.init_date);
        textViewEndDate = findViewById(R.id.end_date);

        textViewResume = findViewById(R.id.publication_resume);
        textViewAmount = findViewById(R.id.dailyWage);
        accept_button = findViewById(R.id.accept_button);
        bottom_message_bar = findViewById(R.id.bottom_message_bar);
        bottom_message_text = findViewById(R.id.bottom_message_text);
        bottom_message_bar_button = findViewById(R.id.cancel_button);
        cancel_text = findViewById(R.id.cancel_text);
        textViewButtonText = findViewById(R.id.button_text);
        imageViewButton = findViewById(R.id.button_icon);
        textViewRating = findViewById(R.id.publisherRating);
        textViewAddress = findViewById(R.id.address);

        textViewContactTitle = findViewById(R.id.contact_title);
        textViewPhoneLayout = findViewById(R.id.phone_layout);
        textViewEmailLayout = findViewById(R.id.email_layout);
        textViewPhone = findViewById(R.id.phone);
        textViewEmail = findViewById(R.id.email);
        attachLayout = findViewById(R.id.attach);

        textViewStartHour = findViewById(R.id.start_hour);
        textViewJobHour = findViewById(R.id.job_hours);

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

        bottom_message_bar_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offer != null)
                    if(offer.isAppliedByMe()) {
                        if((offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.REVISION ||
                                offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.ACCEPTED ) && !complete){
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
                            text.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void afterTextChanged(Editable s) {}
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {
                                    ((AlertDialog) currentRatingDialog).
                                            getButton(AlertDialog.BUTTON_POSITIVE).
                                            setEnabled(!text.getText().toString().isEmpty() &&
                                                    ratingBar.getRating() > 0);
                                }
                            });

                            text.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                                @Override
                                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                    text.setError(null);
                                    return false;
                                }
                            });
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
                                    ((AlertDialog) currentRatingDialog).
                                            getButton(AlertDialog.BUTTON_POSITIVE).
                                            setEnabled(!text.getText().toString().isEmpty() &&
                                                    ratingBar.getRating() > 0);
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
                                    if(ratingBar.getRating() > 0 && !text.getText().toString().isEmpty()){
                                        rateApplication(
                                            offer.getApplication().getId(),
                                            ratingBar.getRating(),
                                            text.getText().toString());
                                        dialog.dismiss();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.dismiss();
                                }
                            });
                            currentRatingDialog = builder.show();
                            ((AlertDialog) currentRatingDialog).getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                        }
                    }
            }
        });

        Bundle bundle = getIntent().getExtras();
        if(getIntent().hasExtra("offer")){
            offer = (Offer) bundle.getSerializable("offer");
            Log.e("Offer", " OFFER ****** location: " + offer.getLocation());
            if(offer != null) {
                setUpForOffer(offer);
                if (offer.isAppliedByMe()) {
                    setUpForApplication(offer.getApplication());
                }
            }
        }
    }

    private void setUpForOffer(final Offer offer){
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(new Locale("es", "ES"));
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        publicationOwner.setText(Html.fromHtml(String.format("publicado por <b>%s</b>", offer.getPublisher().getName())));
        if(offer.getImages() != null && offer.getImages().size() > 0){
            imageViewHeader.setVisibility(View.VISIBLE);
            Picasso.get().load(offer.getImages().get(0)).into(imageViewHeader);
        } else {
            imageViewHeader.setVisibility(View.GONE);
        }
        textViewTitle.setText(offer.getTitle());
        textViewCreationDate.setText(DateUtils.getRelativeTimeSpanString(offer.getCreationDate().getTime(), new Date().getTime(),0L, DateUtils.FORMAT_ABBREV_ALL));
        textViewResume.setText(offer.getSummary());
        if(offer.getLocation() != null && !offer.getLocation().isEmpty()){
            textViewAddress.setText(String.format(new Locale("es", "ES"), "%s, %s", offer.getLocation(), offer.getCommune()));
        } else {
            textViewAddress.setText(offer.getCommune());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", new Locale("es", "ES"));

        if(offer.getStartDate() != null){
            textViewInitDate.setText(String.format("inicio: %s", dateFormat.format(offer.getStartDate().getTime())));
        }
        if(offer.getEndDate() != null){
            textViewEndDate.setText(String.format("término: %s", dateFormat.format(offer.getEndDate().getTime())));

        }

        bottom_message_bar.setVisibility(View.GONE);
        accept_button.setVisibility(View.VISIBLE);

        textViewAmount.setText(String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(offer.getWage())));

        if(!offer.isAppliedByMe()){
            if(offer.getStatus() == Offer.OfferStatus.REVISION && !offer.hasStarted()){
                /*accept_button.setVisibility(GONE);
                bottom_message_bar.setVisibility(View.VISIBLE);
                bottom_message_text.setText("Esta oferta se encuentra en revisión");*/
            } else if(offer.getStatus() == Offer.OfferStatus.CANCELED){
                accept_button.setVisibility(GONE);
                bottom_message_bar.setVisibility(View.VISIBLE);
                bottom_message_text.setText("Esta oferta ha sido cancelada");
                bottom_message_bar_button.setVisibility(GONE);
            } else if(offer.getStatus() == Offer.OfferStatus.ACCEPTED_APPLICATION){
                /*if(offer.isPaid()){
                    accept_button.setVisibility(GONE);
                    bottom_message_bar.setVisibility(View.VISIBLE);
                    bottom_message_text.setText("Esta oferta ha sido adjudicada por otro usuario");
                    bottom_message_bar_button.setVisibility(GONE);
                }*/
            } else if(offer.getStatus() == Offer.OfferStatus.FILLED){
                accept_button.setVisibility(GONE);
                bottom_message_bar.setVisibility(View.VISIBLE);
                bottom_message_text.setText("Esta oferta esta sin vacantes");
                bottom_message_bar_button.setVisibility(GONE);
            } else if(offer.getStatus() == Offer.OfferStatus.PAUSED){
                accept_button.setVisibility(GONE);
                bottom_message_bar.setVisibility(View.VISIBLE);
                bottom_message_text.setText("Esta oferta ha sido pausada");
                bottom_message_bar_button.setVisibility(GONE);
            } else if(offer.getStatus() == Offer.OfferStatus.CLOSED){
                accept_button.setVisibility(GONE);
                bottom_message_bar.setVisibility(View.VISIBLE);
                bottom_message_text.setText("Esta oferta ha sido cerrada");
                bottom_message_bar_button.setVisibility(GONE);
            }  else if(offer.getStatus() == Offer.OfferStatus.DEACTIVATED){
                accept_button.setVisibility(GONE);
                bottom_message_bar.setVisibility(View.VISIBLE);
                bottom_message_text.setText("Esta oferta ha sido desactivada");
                bottom_message_bar_button.setVisibility(GONE);
            }

            if(offer.hasStarted()){
                accept_button.setVisibility(GONE);
                bottom_message_bar.setVisibility(View.VISIBLE);
                bottom_message_text.setText("Esta oferta está cerrada");
                bottom_message_bar_button.setVisibility(GONE);
            }
        }

        if(offer.getPublisher().getRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(
                    String.format(
                            new Locale("es", "ES"),
                            "%.1f",
                            offer.getPublisher().getRating()));

        DateFormat df3 = new SimpleDateFormat(
                "HH:mm",
                new Locale("es", "ES"));

        if(offer.getStartTime() != null)
            textViewStartHour.setText(
                    String.format(
                            new Locale("es", "ES"),
                            "inicio: %s hrs.",
                            df3.format(offer.getStartTime())));
        else
            textViewStartHour.setVisibility(GONE);

        textViewJobHour.setText(
                String.format(new Locale("es", "ES"),
                        "duración de la tarea: %d hrs.",
                        offer.getTotalHours()));

        textViewContactTitle.setVisibility(GONE);
        textViewPhoneLayout.setVisibility(GONE);
        textViewEmailLayout.setVisibility(GONE);

        if(offer.getAttaches() != null && !offer.getAttaches().isEmpty()){
            attachLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(offer.getAttaches().get(0)));
                    startActivity(browserIntent);
                }
            });
        } else {
            attachLayout.setVisibility(GONE);
        }
    }

    DialogInterface currentRatingDialog;

    private void setUpForApplication(final Application application){
        if(application.getApplicationStatus() == Application.ApplicationStatus.ACCEPTED){
            if(offer.isPaid()){
                if(offer.hasFinished()){
                    if(!offer.getApplication().isClosed()){
                        if(offer.getApplication().isQualifiable()) {
                            complete = true;
                            accept_button.setVisibility(GONE);
                            bottom_message_bar.setVisibility(View.VISIBLE);
                            bottom_message_text.setText("Puedes calificar esta oferta");
                            bottom_message_bar_button.setVisibility(View.VISIBLE);
                            cancel_text.setText("Calificar");
                        } else {
                            if(application.wasReviewedByApplicant()){
                                accept_button.setVisibility(GONE);
                                bottom_message_bar.setVisibility(View.VISIBLE);
                                bottom_message_text.setText("Esta tarea ya ha sido calificada");
                                bottom_message_bar_button.setVisibility(GONE);
                            } else {
                                accept_button.setVisibility(GONE);
                                bottom_message_bar.setVisibility(View.VISIBLE);
                                bottom_message_text.setText("Esta oferta está cerrada");
                                bottom_message_bar_button.setVisibility(GONE);
                            }
                        }
                    } else {
                        if(application.wasReviewedByApplicant()){
                            accept_button.setVisibility(GONE);
                            bottom_message_bar.setVisibility(View.VISIBLE);
                            bottom_message_text.setText("Esta tarea ya ha sido calificada");
                            bottom_message_bar_button.setVisibility(GONE);
                        } else {
                            accept_button.setVisibility(GONE);
                            bottom_message_bar.setVisibility(View.VISIBLE);
                            bottom_message_text.setText("Esta oferta está cerrada");
                            bottom_message_bar_button.setVisibility(GONE);
                        }
                    }
                } else {
                    accept_button.setEnabled(false);
                    textViewButtonText.setText("adjudicada");
                    accept_button.getBackground().setColorFilter(
                            ContextCompat.getColor(getApplicationContext(),
                                    R.color.colorAdjudicated),
                            PorterDuff.Mode.SRC);
                    imageViewButton.setVisibility(View.VISIBLE);
                    imageViewButton.setImageDrawable(
                            ContextCompat.getDrawable(this, R.drawable.ic_accepted_offer));
                }
            } else {
                if(offer.getApplication().isClosed()) {
                    accept_button.setVisibility(GONE);
                    bottom_message_bar.setVisibility(View.VISIBLE);
                    bottom_message_text.setText("Esta oferta está cerrada");
                    bottom_message_bar_button.setVisibility(GONE);
                } else {
                    accept_button.setVisibility(GONE);
                    bottom_message_bar.setVisibility(View.VISIBLE);
                    bottom_message_text.setText("Has postulado a esta tarea, te avisaremos cuando sea adjudicada");
                    bottom_message_bar_button.setVisibility(View.VISIBLE);
                }

            }
            /*if(offer.isPaid()){
                accept_button.setEnabled(false);
                textViewButtonText.setText("adjudicada");
                accept_button.getBackground().setColorFilter(
                        ContextCompat.getColor(getApplicationContext(),
                                R.color.colorAdjudicated),
                        PorterDuff.Mode.SRC);
                imageViewButton.setVisibility(View.VISIBLE);
                imageViewButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_accepted_offer));
            } else {
                accept_button.setVisibility(GONE);
                bottom_message_bar.setVisibility(View.VISIBLE);
                bottom_message_text.setText("Has postulado a esta tarea, te avisaremos cuando sea adjudicada");
            }*/
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.REJECTED) {
            accept_button.setVisibility(GONE);
            bottom_message_bar.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Lamentablemente no has sido seleccionado para esta tarea");
            bottom_message_bar_button.setVisibility(GONE);
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.REVISION) {
            accept_button.setVisibility(GONE);
            bottom_message_bar.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Has postulado a esta tarea, te avisaremos cuando sea adjudicada");
            bottom_message_bar_button.setVisibility(View.VISIBLE);
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.CANCELED_BY_APPLICANT) {
            accept_button.setVisibility(GONE);
            bottom_message_bar.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Has cancelado tu postulación");
            bottom_message_bar_button.setVisibility(GONE);
        } else if(application.getApplicationStatus() == Application.ApplicationStatus.CANCELED_BY_PUBLISHER) {
            accept_button.setVisibility(GONE);
            bottom_message_bar.setVisibility(View.VISIBLE);
            bottom_message_text.setText("La oferta ha sido cancelada");
            bottom_message_bar_button.setVisibility(GONE);
        }

        /*if(offer.getApplication().isClosed()){
            accept_button.setVisibility(GONE);
            bottom_message_bar.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Esta oferta está cerrada");
            bottom_message_bar_button.setVisibility(GONE);
        } else if(offer.getApplication().isQualifiable()){
            complete = true;
            accept_button.setVisibility(GONE);
            bottom_message_bar.setVisibility(View.VISIBLE);
            bottom_message_text.setText("Puedes calificar esta oferta");
            bottom_message_bar_button.setVisibility(View.VISIBLE);
            cancel_text.setText("Calificar");
        }*/

        if(offer.getPublisher().getRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(String.format(new Locale("es", "ES"), "%.1f", offer.getPublisher().getRating()));

        if(offer.getPublisher().getEmail() != null && !offer.getPublisher().getEmail().isEmpty()
                && offer.getPublisher().getPhone() != null && !offer.getPublisher().getPhone().isEmpty()){
            textViewContactTitle.setVisibility(View.VISIBLE);
            textViewPhoneLayout.setVisibility(View.VISIBLE);
            textViewEmailLayout.setVisibility(View.VISIBLE);
            textViewPhone.setText(offer.getPublisher().getPhone());
            textViewEmail.setText(offer.getPublisher().getEmail());
        } else {
            textViewContactTitle.setVisibility(GONE);
            textViewPhoneLayout.setVisibility(GONE);
            textViewEmailLayout.setVisibility(GONE);
        }
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
                this, String.format(new Locale("es", "ES"),"offers/%d/", offer.getId()))
                .POST()
                .tokenized(true)
                .objectReturnListener(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("OfferDetailsActivity", "onResponse: " + response.toString());
                        accept_button.setVisibility(GONE);
                        bottom_message_bar.setVisibility(View.VISIBLE);
                        bottom_message_text.setText("Has postulado a esta tarea, te avisaremos cuando sea adjudicada");
                        accepted_offer = true;

                        Intent returnIntent = new Intent();
                        setResult(Activity.RESULT_OK, returnIntent);
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
                        AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                        builder.setTitle(getString(R.string.application));
                        builder.setMessage("Ha ocurrido un problema postulando a la oferta");
                        builder.setNegativeButton("Ok",
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
            String.format(new Locale("es", "ES"),"applications/%d/reviews/", application_id))
            .POST()
            .tokenized(true)
            .parameters(args)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                    builder.setTitle(getString(R.string.application));
                    builder.setMessage("Tu calificación ha sido registrada con éxito");
                    builder.setNegativeButton("Ok",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    builder.show();

                    Intent returnIntent = new Intent();
                    setResult(Activity.RESULT_OK, returnIntent);

                    bottom_message_bar.setVisibility(GONE);
                    accept_button.setVisibility(View.VISIBLE);
                    accept_button.setEnabled(false);
                    textViewButtonText.setText("Cerrado");
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                    builder.setTitle(getString(R.string.application));
                    builder.setMessage("Ha ocurrido un problema registrando tu calificación");
                    builder.setNegativeButton("Ok",
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

    private void cancelApplication(Application application){
        HashMap<String, Object> args = new HashMap<>();
        args.put("status", Application.ApplicationStatus.toInt(Application.ApplicationStatus.CANCELED_BY_APPLICANT));
        Log.d("****cancelApplication", " -----> application new status: " + Application.ApplicationStatus.toInt(Application.ApplicationStatus.CANCELED_BY_APPLICANT));
        ServerCommunication sc = new ServerCommunication.ServerCommunicationBuilder(this,
            String.format(new Locale("es", "ES"),"applications/%d/", application.getId()))
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
                        Log.e("DetailsActivity", "response" + new String(volleyError.networkResponse.data));
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

                    AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                    builder.setTitle(getString(R.string.application));
                    builder.setMessage("Ha ocurrido un problema cancelando tu postulación");
                    builder.setNegativeButton("Ok",
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
}