package kiwigroup.yodelego;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class RegisterEndFragment extends Fragment {

    private OnRegisterFragmentListener mListener;
    private Button goToWallButton;

    public static RegisterEndFragment newInstance() {
        RegisterEndFragment fragment = new RegisterEndFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final Context contextThemeWrapper = new ContextThemeWrapper(getActivity(), R.style.YoDelegoDarkTheme);
        LayoutInflater localInflater = inflater.cloneInContext(contextThemeWrapper);
        return localInflater.inflate(R.layout.fragment_end_register, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRegisterFragmentListener) {
            mListener = (OnRegisterFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRegisterFragmentListener");
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView text = view.findViewById(R.id.text2);
        text.setText(Html.fromHtml("Tu cuenta se ha creado de manera correcta, entra a tu muro para disfrutar de las funciones de <b>YoDelego</b>."));
        goToWallButton = view.findViewById(R.id.go_to_wall);
        goToWallButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.goToWall();
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
