package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import kiwigroup.yodelego.model.User;

public class ProfileFragment extends Fragment {
    private TextView userType;
    private TextView name;
    private TextView rut;

    private TextView assigned_offers;
    private TextView cancelled_offers;

    private TextView mail;

    private TextView academic_description;
    private TextView edit;
    private LinearLayout academic_layout;

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

        userType = view.findViewById(R.id.user_type);
        edit = view.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment editFragment = ProfileEditFragment.newInstance(user);
                listener.addFragmentToMainContent(editFragment, true, getString(R.string.id_profile_edit));
            }
        });
        name = view.findViewById(R.id.name);
        rut = view.findViewById(R.id.rut);
        mail = view.findViewById(R.id.mail);
        view.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.closeSession();
            }
        });
        academic_layout = view.findViewById(R.id.academic_info);
        assigned_offers = view.findViewById(R.id.assigned_offers);
        cancelled_offers = view.findViewById(R.id.cancelled_offers);
        loadData(user);
    }

    private void loadData(User user){
        name.setText(user.getName() + " " + user.getLastName());
        rut.setText(user.getRut());
        mail.setText(user.getEmail());
        userType.setText(user.getEducationalInstitution() == null ? "Trabajador" : "Estudiante");

        if(user.getEducationalInstitution() == null || user.getEducationalInstitution().isEmpty()){
            academic_layout.setVisibility(LinearLayout.GONE);
        } else {
            academic_layout.setVisibility(LinearLayout.VISIBLE);
            academic_description.setText(Html.fromHtml("Estudiante de <b>" + user.getCareer() + ", en la " + user.getEducationalInstitution() + ", cursa " + user.getSemesters() + " semestre</b>"));
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
}
