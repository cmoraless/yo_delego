package kiwigroup.yodelego;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import kiwigroup.yodelego.adapter.ApplicationAdapter;
import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.Offer;

public class ReviewingApplicationsFragment extends Fragment implements OnApplicationUpdateListener {

    private OnUserFragmentsListener mListener;

    private RecyclerView recyclerView;
    private ApplicationAdapter adapter;

    public ReviewingApplicationsFragment() {
    }

    @SuppressWarnings("unused")
    public static ReviewingApplicationsFragment newInstance(int columnCount) {
        ReviewingApplicationsFragment fragment = new ReviewingApplicationsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_postulation_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        adapter = new ApplicationAdapter(mListener, Application.ApplicationStatus.REVISION);
        recyclerView.setAdapter(adapter);

        updatingData();
        mListener.getMyApplications(this, false);

        return view;
    }

    private void updatingData(){
        if(adapter != null) {
            adapter.showLoading();
        }
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
