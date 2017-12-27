package kiwigroup.yodelego;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import kiwigroup.yodelego.model.User;

public class ApplicationsFragment extends Fragment {


    private View mProgressView;
    private RelativeLayout formLayout;
    private TextInputEditText universityTextView;
    private TextInputEditText careerTextView;
    private TextInputEditText semesterTextView;
    private Button signUpButton;
    private Button cancelButton;

    private User user;

    public static ApplicationsFragment newInstance(User user) {
        ApplicationsFragment fragment = new ApplicationsFragment();
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
        return inflater.inflate(R.layout.fragment_application, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*formLayout = view.findViewById(R.id.email_login_form);
        mProgressView = view.findViewById(R.id.login_progress);

        universityTextView = view.findViewById(R.id.university);
        careerTextView = view.findViewById(R.id.career);
        semesterTextView = view.findViewById(R.id.semester);

        signUpButton = view.findViewById(R.id.create_account_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptCreateAccount();
            }
        });
        cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.finish();
            }
        });*/
    }



    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnRegisterFragmentListener) {
            mListener = (OnRegisterFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRegisterFragmentListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }
}
