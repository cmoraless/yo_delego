package kiwigroup.yodelego;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import kiwigroup.yodelego.model.User;

public class ProfileEditFragment extends Fragment {
    private View mProgressView;
    private RelativeLayout studentFormLayout;
    private LinearLayout baseFormLayout;
    private Spinner universitySpinner;
    private TextInputEditText careerTextView;
    private TextInputEditText yearTextView;

    private TextView userType;
    private TextView name;
    private TextView rut;
    private TextView mail;
    private TextView academic_description;

    private User user;
    private OnUserFragmentsListener listener;

    public static ProfileEditFragment newInstance(User user) {
        ProfileEditFragment fragment = new ProfileEditFragment();
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
        return inflater.inflate(R.layout.fragment_edit_profile, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        studentFormLayout = view.findViewById(R.id.email_login_form);
        baseFormLayout = view.findViewById(R.id.summary_info);
        mProgressView = view.findViewById(R.id.login_progress);

        universitySpinner = view.findViewById(R.id.spinner);
        careerTextView = view.findViewById(R.id.career);
        yearTextView = view.findViewById(R.id.enrollment_year);

        showProgress(true);
        listener.getEducationalInstitutions(new RegisterActivity.onEducationalInstitutionsListener() {
            @Override
            public void onEducationalInstitutionsResponse(Map<String, Integer> response) {
                List<String> values = new ArrayList<>(response.keySet());
                values.add(0, "Universidad o Instituto");
                showProgress(false);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                        android.R.layout.simple_spinner_item, values){
                    @Override
                    public boolean isEnabled(int position){
                        return position != 0;
                    }
                    @Override
                    public View getDropDownView(int position, View convertView,
                                                ViewGroup parent) {
                        View view = super.getDropDownView(position, convertView, parent);
                        TextView tv = (TextView) view;
                        tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                        return view;
                    }
                };
                adapter.setDropDownViewResource(R.layout.spinner_layout);
                universitySpinner.setAdapter(adapter);
            }

            @Override
            public void onError(String error) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.error_network));
                builder.setMessage(getString(R.string.error_network_details));
                builder.setPositiveButton(getString(R.string.message_ok),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                builder.show();
                showProgress(false);
            }
        });
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                android.R.layout.simple_spinner_item, new ArrayList<String>()){
            @Override
            public boolean isEnabled(int position){
                return position != 0;
            }
            @Override
            public View getDropDownView(int position, View convertView,
                                        ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                TextView tv = (TextView) view;
                tv.setTextColor(position == 0 ? Color.GRAY : Color.BLACK);
                return view;
            }
        };
        adapter.add("Universidad o Instituto");
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        universitySpinner.setAdapter(adapter);
    }

    private void loadData(User user){

    }

    private void showProgress(final boolean show) {
        for (int i = 0; i < studentFormLayout.getChildCount(); i++) {
            View child = studentFormLayout.getChildAt(i);
            child.setVisibility(show ? View.GONE : View.VISIBLE);
            child.setEnabled(!show);
        }
        for (int i = 0; i < baseFormLayout.getChildCount(); i++) {
            View child = baseFormLayout.getChildAt(i);
            child.setVisibility(show ? View.GONE : View.VISIBLE);
            child.setEnabled(true);
        }

        int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressView.animate().setDuration(shortAnimTime).alpha(
                show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
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
