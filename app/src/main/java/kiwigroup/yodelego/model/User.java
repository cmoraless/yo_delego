package kiwigroup.yodelego.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.util.Calendar.DATE;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;

/**
 * Created by cristian on 7/21/17.
 */

public class User implements Serializable {
    private long id;
    private String name;
    private String lastName;
    private String email;
    private String phone;
    private String educationalInstitution;
    private String careerCategory;
    private String career;
    private int enrollmentYear;
    private int semesters;
    private String rut;
    private String bank;
    private int accountType;
    private String accountNumber;
    private float applicantRating;
    private float publisherRating;
    private String profileImage;

    public static User parseFromJson(JSONObject response){
        User user = new User();
        try {

            user = new User();
            user.setId(response.getLong("id"));
            user.setName(response.getString("first_name"));
            user.setLastName(response.getString("last_name"));
            user.setEmail(response.getString("email"));

            if(response.has("rut") && !response.isNull("rut") && !response.getString("rut").isEmpty())
                user.setRut(response.getString("rut"));

            if(response.has("phone_number") && !response.isNull("phone_number") && !response.getString("phone_number").isEmpty())
                user.setPhone(response.getString("phone_number"));

            if(response.has("educational_institution") && !response.isNull("educational_institution") && !response.getString("educational_institution").isEmpty())
                user.setEducationalInstitution(response.getString("educational_institution"));

            if(response.has("career_category") && !response.isNull("career_category") && !response.getString("career_category").isEmpty())
                user.setCareerCategory(response.getString("career_category"));

            if(response.has("career") && !response.isNull("career") && !response.getString("career").isEmpty())
                user.setCareer(response.getString("career"));

            if(response.has("enrollment_year") && !response.isNull("enrollment_year") && !response.getString("enrollment_year").isEmpty())
                user.setEnrollmentYear(Integer.parseInt(response.getString("enrollment_year")));

            if(response.has("bank") && !response.isNull("bank") && !response.getString("bank").isEmpty())
                user.setBank(response.getString("bank"));

            if(response.has("bank_account_kind") && !response.isNull("bank_account_kind"))
                user.setAccountType(response.getInt("bank_account_kind"));

            if(response.has("bank_account_number") && !response.isNull("bank_account_number") && !response.getString("bank_account_number").isEmpty())
                user.setAccountNumber(response.getString("bank_account_number"));

            if(response.has("profile_picture") && !response.isNull("profile_picture") && !response.getString("profile_picture").isEmpty())
                user.setProfileImage(response.getString("profile_picture"));

            if(response.has("publisher_rating") && !response.isNull("publisher_rating") && !response.getString("publisher_rating").isEmpty())
                user.setPublisherRating(Float.parseFloat(response.getString("publisher_rating")));

            Log.e("LoginActivity", "SCUser getName " + user.getName());
            Log.e("LoginActivity", "SCUser getLastName " + user.getLastName());
            Log.e("LoginActivity", "SCUser getSemesters " + user.getSemesters());
            Log.e("LoginActivity", "SCUser getCareer " + user.getCareer());
            Log.e("LoginActivity", "SCUser getEducationalInstitution " + user.getEducationalInstitution());
            Log.e("LoginActivity", "SCUser getEmail " + user.getEmail());
            Log.e("LoginActivity", "SCUser getRut " + user.getRut());
            Log.e("LoginActivity", "SCUser getEnrollmentYear " + user.getEnrollmentYear());
            Log.e("LoginActivity", "SCUser setProfileImage " + user.getProfileImage());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEducationalInstitution() {
        return educationalInstitution;
    }

    public void setEducationalInstitution(String educationalInstitution) {
        this.educationalInstitution = educationalInstitution;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public int getEnrollmentYear() {
        return enrollmentYear;
    }

    public void setEnrollmentYear(int enrollmentYear) {
        this.enrollmentYear = enrollmentYear;

        Date currentTime = Calendar.getInstance().getTime();
        Date startTime = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
        try {
            startTime = formatter.parse(String.valueOf(enrollmentYear));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int diff = getDiffYears(startTime, currentTime);
        semesters = diff*2;
        if(currentTime.getMonth() > 7){
            semesters ++;
        }
    }

    public static Calendar getCalendar(Date date) {
        Calendar cal = Calendar.getInstance(new Locale("es", "ES"));
        cal.setTime(date);
        return cal;
    }

    public static int getDiffYears(Date first, Date last) {
        Calendar a = getCalendar(first);
        Calendar b = getCalendar(last);
        int diff = b.get(YEAR) - a.get(YEAR);
        if (a.get(MONTH) > b.get(MONTH) ||
                (a.get(MONTH) == b.get(MONTH) && a.get(DATE) > b.get(DATE))) {
            diff--;
        }
        return diff;
    }

    public String getRut() {
        return rut;
    }

    public void setRut(String rut) {
        this.rut = rut;
    }

    public int getSemesters() {
        return semesters;
    }

    public void setSemesters(int semesters) {
        this.semesters = semesters;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public int getAccountType() {
        return accountType;
    }

    public void setAccountType(int accountType) {
        this.accountType = accountType;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public float getApplicantRating() {
        return applicantRating;
    }

    public void setApplicantRating(float applicantRating) {
        this.applicantRating = applicantRating;
    }

    public float getPublisherRating() {
        return publisherRating;
    }

    public void setPublisherRating(float publisherRating) {
        this.publisherRating = publisherRating;
    }

    public String getCareerCategory() {
        return careerCategory;
    }

    public void setCareerCategory(String careerCategory) {
        this.careerCategory = careerCategory;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getProfileImage() {
        return profileImage;
    }

    public void setProfileImage(String profileImage) {
        this.profileImage = profileImage;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
