package kiwigroup.yodelego;

import android.content.DialogInterface;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
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
import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

public class OfferDetailsActivity extends AppCompatActivity {

    private TextView publicationOwner;
    private Offer offer;

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

        Bundle bundle = getIntent().getExtras();
        offer = (Offer) bundle.getSerializable("offer");

        Log.d("********", "instance of Application" + (offer instanceof Application));

        publicationOwner = findViewById(R.id.publication_owner);
        publicationOwner.setText(Html.fromHtml(String.format("publicado por <b>%s</b>", offer.getPublisher())));

        TextView TextViewTitle = findViewById(R.id.title);
        TextViewTitle.setText(offer.getTitle());

        TextView textViewDate = findViewById(R.id.date);
        textViewDate.setText(DateUtils.getRelativeTimeSpanString(offer.getDate().getTime(), new Date().getTime(),0L, DateUtils.FORMAT_ABBREV_ALL));

        TextView textViewResume = findViewById(R.id.publication_resume);
        textViewResume.setText(offer.getPublicationResume());

        TextView textViewAmount = findViewById(R.id.dailyWage);
        DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
        otherSymbols.setDecimalSeparator(',');
        otherSymbols.setGroupingSeparator('.');
        textViewAmount.setText(String.format("$%s", new DecimalFormat("#,###", otherSymbols).format(offer.getTotalWage())));


        Button accept_button = findViewById(R.id.accept_button);



        accept_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(OfferDetailsActivity.this);
                builder.setTitle(getString(R.string.account_created_title));
                builder.setMessage(getString(R.string.account_created));
                builder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                acceptOffer();
                            }
                        });
                builder.show();
            }
        });
    }

    protected void acceptOffer(){
        Log.d("startLoginProcess", "--- startLoginProcess ---");
        ServerCommunication serverCommunication = new ServerCommunication.ServerCommunicationBuilder(
                this, String.format(Locale.US,"offers/%d/", offer.getId()))
                .POST()
                .tokenized(true)
                .objectReturnListener(new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                    }
                })
                .errorListener(new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {


                        /*volleyError.printStackTrace();
                        if (volleyError instanceof NetworkError) {
                            onLoginError(getString(R.string.error_network));
                        } else if (volleyError instanceof ServerError) {
                            try {
                                JSONObject responseObject = new JSONObject(new String(volleyError.networkResponse.data));
                                Log.d("startLoginProcess", "---> onErrorResponse " + responseObject.toString());
                                String genericError = "";
                                Iterator<String> iter = responseObject.keys();
                                while (iter.hasNext()) {
                                    String key = iter.next();
                                    String errors = "";
                                    JSONArray errorsArray = responseObject.getJSONArray(key);
                                    for(int i = 0; i < errorsArray.length(); i++){
                                        errors += " " + errorsArray.getString(i);
                                    }
                                    onLoginError(errors);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                                onLoginError(getString(R.string.error_json_exception));
                            }
                        } else if (volleyError instanceof AuthFailureError) {
                            onLoginFieldError("username", getString(R.string.error_incorrect_password_or_login));
                        } else if (volleyError instanceof ParseError) {
                            onLoginError(getString(R.string.error_parser));
                        } else if (volleyError instanceof TimeoutError) {
                            onLoginError(getString(R.string.error_timeout));
                        }*/
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

}
