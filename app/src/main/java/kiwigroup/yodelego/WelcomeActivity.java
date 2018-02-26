package kiwigroup.yodelego;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        TextView text = findViewById(R.id.text2);
        text.setText(Html.fromHtml("<b>YoDelego</b> te da la bienvenida a la aplicación donde podrás encontrar trabajos esporádicos de manera rápida y sencilla. Tan solo entra a la app, regístrate y ya serás parte de nuestra comunidad!"));

        Button understoodButton = findViewById(R.id.understood);
        understoodButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });
    }

    private void goToLogin(){
        Intent mainIntent = new Intent().setClass(WelcomeActivity.this, LoginActivity.class);
        startActivity(mainIntent);
        finish();
    }
}
