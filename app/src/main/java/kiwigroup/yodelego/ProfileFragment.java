package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
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
    private TextView mail;
    private TextView academic_description;

    private User user;
    private OnUserFragmentsListener listener;

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);
        return fragment;
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
        userType.setText(user.getEducationalInstitution().isEmpty() ? "Trabajador" : "Estudiante");
        name = view.findViewById(R.id.name);
        name.setText(user.getName() + " " + user.getLastName());
        rut = view.findViewById(R.id.rut);
        if(user.getRut() != null && !user.getRut().isEmpty())
            rut.setText(user.getRut());
        else
            rut.setVisibility(LinearLayout.GONE);

        mail = view.findViewById(R.id.mail);
        mail.setText(user.getEmail());
        LinearLayout academic_layout = view.findViewById(R.id.academic_info);
        if(user.getEducationalInstitution().isEmpty()){
            academic_layout.setVisibility(LinearLayout.GONE);
        } else {
            academic_description = view.findViewById(R.id.academic_description);
            academic_layout.setVisibility(LinearLayout.VISIBLE);
            academic_description.setText("Estudiante de " + user.getCareer() + ", en la " + user.getEducationalInstitution() + ", cursa " + user.getSemesters() + " semestre");
        }

        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.closeSession();
            }
        });
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
