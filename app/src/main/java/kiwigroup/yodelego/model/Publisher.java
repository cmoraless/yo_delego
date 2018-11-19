package kiwigroup.yodelego.model;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;

public class Publisher implements Serializable {

    private String name;
    private String profilePictureUrl;
    private float rating;
    private String email;
    private String phone;

    public static Publisher parseFromJson(JSONObject object){
        Publisher publisher = new Publisher();
        try {

            //Log.d("Offer", " PUBLISHER ****** " + object.toString());

            if(!object.isNull("name"))
                publisher.setName(object.getString("name"));
            if(!object.isNull("profile_picture"))
                publisher.setProfilePictureUrl(object.getString("profile_picture"));
            if(!object.isNull("rating")){
                try {
                    publisher.setRating(BigDecimal.valueOf(object.getDouble("rating")).floatValue());
                } catch(Exception ex){
                    ex.printStackTrace();
                    publisher.setRating(-1.0f);
                }
            } else {
                publisher.setRating(-1.0f);
            }
            if(!object.isNull("email"))
                publisher.setEmail(object.getString("email"));
            if(!object.isNull("phone_number"))
                publisher.setPhone(object.getString("phone_number"));

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return publisher;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
