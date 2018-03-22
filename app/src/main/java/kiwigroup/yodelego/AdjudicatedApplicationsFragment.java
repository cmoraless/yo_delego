package kiwigroup.yodelego;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import kiwigroup.yodelego.adapter.ApplicationAdapter;
import kiwigroup.yodelego.model.Application;

public class AdjudicatedApplicationsFragment extends Fragment implements OnApplicationUpdateListener {

    private OnUserFragmentsListener mListener;
    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;

    public AdjudicatedApplicationsFragment() {
    }

    public static AdjudicatedApplicationsFragment newInstance(int columnCount) {
        AdjudicatedApplicationsFragment fragment = new AdjudicatedApplicationsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_list, container, false);

        recyclerView = view.findViewById(R.id.list);
        adapter = new ApplicationAdapter(mListener, Application.ApplicationStatus.ACCEPTED);
        recyclerView.setAdapter(adapter);

        updatingData();
        mListener.getMyApplications(this, false);
        return view;
    }


    private void updatingData(){
        adapter.showLoading();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserFragmentsListener) {
            mListener = (OnUserFragmentsListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnApplicationsListListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onApplicationsResponse(List<Application> applications) {
        adapter.hideLoading();
        adapter.update(applications);
    }

    @Override
    public void onApplicationError(String error) {

    }
}
