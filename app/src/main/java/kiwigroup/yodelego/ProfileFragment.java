package kiwigroup.yodelego;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;
import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.User;

public class ProfileFragment extends Fragment {
    private LinearLayout progressLayout;
    private LinearLayout formLayout;

    private CircleImageView image;

    private TextView userType;
    private TextView name;
    private TextView rut;

    private TextView accepted_offers;
    private TextView complete_offers;

    private TextView mail;

    private TextView academic_description;
    private TextView edit;
    private LinearLayout academic_layout;
    private TextView textViewRating;

    private TextView version_text;

    private User user;
    private OnUserFragmentsListener listener;


    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);



        return fragment;
    }

    public void updateUser(User user){
        this.user = user;
        loadData(user);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = (User) getArguments().getSerializable("user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        progressLayout = view.findViewById(R.id.progress_layout);
        formLayout = view.findViewById(R.id.form_layout);

        listener.updateUser();

        image = view.findViewById(R.id.profile_image);
        userType = view.findViewById(R.id.user_type);
        edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment editFragment = ProfileEditFragment.newInstance(user);
                listener.askAddFragmentToMainContent(editFragment, true, getString(R.string.id_profile_edit));
            }
        });
        name = view.findViewById(R.id.name);
        rut = view.findViewById(R.id.rut);
        mail = view.findViewById(R.id.mail);
        academic_description = view.findViewById(R.id.academic_description);
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.closeSession();
            }
        });
        academic_layout = view.findViewById(R.id.academic_info);

        accepted_offers = view.findViewById(R.id.assigned_offers_amounts);
        complete_offers = view.findViewById(R.id.complete_offers_amount);

        textViewRating = view.findViewById(R.id.publisherRating);

        //loadData(user);

        version_text = view.findViewById(R.id.version_text);
        version_text.setText(String.format("version %s", BuildConfig.VERSION_NAME));

        listener.getMyApplications(new OnApplicationUpdateListener() {
            @Override
            public void onApplicationsResponse(List<Offer> offers) {
                int assigned_offers_amount = 0;
                int complete_offers_amount = 0;
                for(Offer offer : offers){

                    if(offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.ACCEPTED
                            && offer.getApplication().isPaid()
                            && !offer.hasStarted()) {
                        assigned_offers_amount++;
                    }

                    if(offer.hasStarted() &&
                            offer.getApplication().getApplicationStatus() == Application.ApplicationStatus.ACCEPTED &&
                            offer.isPaid() ) {
                        complete_offers_amount ++;
                    }
                }
                accepted_offers.setText(String.valueOf(assigned_offers_amount));
                complete_offers.setText(String.valueOf(complete_offers_amount));
            }

            @Override
            public void onApplicationError(String error) {

            }
        }, false);
    }

    private void showProgress(final boolean show) {
        formLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        progressLayout.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    private void loadData(User user){
        showProgress(false);

        name.setText(String.format(new Locale("es", "ES"), "%s %s", user.getName(), user.getLastName()));
        rut.setText(RUTformat(user.getRut()));
        mail.setText(user.getEmail());
        userType.setText(user.getEducationalInstitution() == null ? "Trabajador" : "Estudiante");

        if(user.getEducationalInstitution() == null || user.getEducationalInstitution().isEmpty()){
            academic_layout.setVisibility(LinearLayout.GONE);
        } else {
            academic_layout.setVisibility(LinearLayout.VISIBLE);
            academic_description.setText(Html.fromHtml(String.format(new Locale("es", "ES"), "Estudiante de <b>%s, en la %s, cursa %d semestre</b>", user.getCareer(), user.getEducationalInstitution(), user.getSemesters())));
        }
        if(user.getApplicantRating() == -1.0f)
            textViewRating.setText("");
        else
            textViewRating.setText(String.format(new Locale("es", "ES"), "%.1f", user.getApplicantRating()));

        if(user.getProfileImage() != null && !user.getProfileImage().isEmpty()){
            Picasso.get()
                .load(user.getProfileImage())
                .placeholder(R.drawable.ic_profile)
                .into(image);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserFragmentsListener) {
            listener = (OnUserFragmentsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRegisterFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public String RUTformat(String rut) {
        int cont = 0;
        String format;
        rut = rut.replace(".", "");
        rut = rut.replace("-", "");
        format = "-" + rut.substring(rut.length() - 1);
        for (int i = rut.length() - 2; i >= 0; i--) {
            format = rut.substring(i, i + 1) + format;
            cont++;
            if (cont == 3 && i != 0) {
                format = "." + format;
                cont = 0;
            }
        }
        return format;
    }
}
