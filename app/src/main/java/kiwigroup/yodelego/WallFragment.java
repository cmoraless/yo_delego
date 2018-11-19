package kiwigroup.yodelego;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import kiwigroup.yodelego.adapter.WallAdapter;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.StatusNotification;
import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.model.WallItem;

public class WallFragment extends Fragment
        implements
            WallAdapter.AdapterListener,
            OnWallUpdateListener,
            SwipeRefreshLayout.OnRefreshListener {

    private User user;
    private static WallFragment fragment;
    private OnUserFragmentsListener mListener;
    private WallAdapter adapter;
    private RecyclerView wallRecyclerView;
    private EndlessRecyclerViewScrollListener listener;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    public static WallFragment newInstance(User user) {
        if(fragment == null) {
            fragment = new WallFragment();
            Bundle bundle = new Bundle();
            bundle.putSerializable("user", user);
            fragment.setArguments(bundle);
        }
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
        return inflater.inflate(R.layout.fragment_wall, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wallRecyclerView = view.findViewById(R.id.lvWall);
        mSwipeRefreshLayout = view.findViewById(R.id.swipe_refresh);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(
                R.color.colorPrimary,
                R.color.colorPrimaryDark,
                R.color.colorPrimary);
        adapter = new WallAdapter(this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        wallRecyclerView.setLayoutManager(linearLayoutManager);
        wallRecyclerView.setAdapter(adapter);
        listener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                onLoadMoreOffers(page);
            }
        };
        wallRecyclerView.addOnScrollListener(listener);
        Log.d("WallFragment", "-------> onViewCreated");
        mListener.getWallItems();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d("WallFragment", "-------> onAttach");
        if (context instanceof OnUserFragmentsListener) {
            mListener = (OnUserFragmentsListener) context;
            mListener.addWallUpdateListener(this);
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRegisterFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("WallFragment", "-------> onDetach");
        mListener.removeWallUpdateListener(this);
        mListener = null;
    }

    @Override
    public void onOfferSelected(Offer offer) {
        mListener.onWallOfferSelected(offer);
    }

    @Override
    public void onLoadMoreOffers(int page) {
        mListener.getMoreWallItems();
    }

    @Override
    public void closeNotification(StatusNotification notification) {
        mListener.closeNotification(notification);
    }

    @Override
    public void onNotificationSelected() {
        mListener.onNotificationSelected();
    }

    @Override
    public void cleanWall() {
        adapter.clear();
    }

    @Override
    public void onLoadingWallItems() {
        adapter.showLoading();
    }

    @Override
    public void onWallItemsResponse(List<WallItem> wallOffers) {
        Log.d("WallFragment", "-------> onWallItemsResponse");
        mSwipeRefreshLayout.setRefreshing(false);
        adapter.hideLoading();
        for(WallItem offer : wallOffers){
            adapter.append(offer);
        }
        listener.stopLoading();
    }

    @Override
    public void onNotificationResponse(List<StatusNotification> notificationResume) {
        adapter.updateStatusNotification(notificationResume);
        wallRecyclerView.smoothScrollToPosition(0);
    }

    @Override
    public void onApplicationsResponse(List<Offer> applications) {

    }

    @Override
    public void onRefresh() {
        mListener.refreshWall();
    }

    public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        private int visibleThreshold = 2;
        private int currentPage = 0;
        private int previousTotalItemCount = 0;
        private boolean loading = true;
        private int startingPageIndex = 0;

        RecyclerView.LayoutManager mLayoutManager;

        EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
            this.mLayoutManager = layoutManager;
        }

        public int getLastVisibleItem(int[] lastVisibleItemPositions) {
            int maxSize = 0;
            for (int i = 0; i < lastVisibleItemPositions.length; i++) {
                if (i == 0) {
                    maxSize = lastVisibleItemPositions[i];
                }
                else if (lastVisibleItemPositions[i] > maxSize) {
                    maxSize = lastVisibleItemPositions[i];
                }
            }
            return maxSize;
        }

        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int lastVisibleItemPosition = 0;
            int totalItemCount = mLayoutManager.getItemCount();

            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();

            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount, view);
                loading = true;
            }
        }

        void stopLoading(){
            int totalItemCount = mLayoutManager.getItemCount();
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }
        }

        public void resetState() {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = 0;
            this.loading = true;
        }

        public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

    }
}
