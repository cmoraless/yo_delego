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

public class CloseApplicationsFragment extends Fragment implements OnApplicationUpdateListener {

    private OnUserFragmentsListener mListener;

    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;

    public CloseApplicationsFragment() {
    }

    public static CloseApplicationsFragment newInstance(int columnCount) {
        CloseApplicationsFragment fragment = new CloseApplicationsFragment();
        return fragment;
    }

    private void updatingData(){
        if(adapter != null) {
            adapter.showLoading();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        adapter = new ApplicationAdapter(mListener, Application.ApplicationStatus.CANCELED);
        recyclerView.setAdapter(adapter);

        updatingData();
        mListener.getMyApplications(this, false);

        return view;
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
        if(adapter != null){
            adapter.hideLoading();
            adapter.update(applications);
        }
    }

    @Override
    public void onApplicationError(String error) {

    }
}
