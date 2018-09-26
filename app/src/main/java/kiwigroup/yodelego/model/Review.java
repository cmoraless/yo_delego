package kiwigroup.yodelego.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class Review implements Serializable {

    private int kind;
    private float rating;
    private String text;

    public static Review parseFromJson(JSONObject object){
        Review review = new Review();
        try {
            Log.d("Review", " REVIEW ****** " + object.toString());
            if(!object.isNull("kind"))
                review.setKind(object.getInt("kind"));
            if(!object.isNull("rating")){
                try {
                    review.setRating(BigDecimal.valueOf(object.getDouble("rating")).floatValue());
                } catch(Exception ex){
                    ex.printStackTrace();
                    review.setRating(-1.0f);
                }
            } else {
                review.setRating(-1.0f);
            }
            if(!object.isNull("text"))
                review.setText(object.getString("text"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return review;
    }


    public void setKind(int kind) {
        this.kind = kind;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ReviewKind getKind() {
        return ReviewKind.fromInteger(kind);
    }

    public enum ReviewKind {
        OFFER,
        APPLICATION;

        public static ReviewKind fromInteger(int number) {
            switch(number) {
                case 0:
                    return OFFER;
                case 1:
                    return APPLICATION;
            }
            return null;
        }
    }
}
