package kiwigroup.yodelego;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import kiwigroup.yodelego.adapter.ApplicationAdapter;
import kiwigroup.yodelego.model.Application;
import kiwigroup.yodelego.model.NotificationResume;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.WallItem;

public class CompleteApplicationsFragment extends Fragment implements OnWallUpdateListener, SwipeRefreshLayout.OnRefreshListener {
    private OnUserFragmentsListener mListener;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ApplicationAdapter adapter;

    public CompleteApplicationsFragment() { }

    public static CompleteApplicationsFragment newInstance() {
        CompleteApplicationsFragment fragment = new CompleteApplicationsFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_application_list, container, false);
        recyclerView = view.findViewById(R.id.list);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorPrimary);
        adapter = new ApplicationAdapter(mListener, true);
        recyclerView.setAdapter(adapter);
        updateData();
        return view;
    }

    public void updateData() {
        if(adapter != null && mListener != null){
            updatingData();
            mListener.getWallItems(this);
        }
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
    public void cleanWall() {

    }

    @Override
    public void onLoadingWallItems() {

    }

    @Override
    public void onWallItemsResponse(List<WallItem> wallOffers) {

    }

    @Override
    public void onWallItemsError(String error) {

    }

    @Override
    public void onApplicationsResponse(List<Offer> applications) {
        adapter.hideLoading();
        adapter.update(applications);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onApplicationError(String error) {
        adapter.hideLoading();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onNotificationResponse(NotificationResume notificationResume) {

    }

    @Override
    public void onRefresh() {
        mListener.refreshWall(this);
    }
}
