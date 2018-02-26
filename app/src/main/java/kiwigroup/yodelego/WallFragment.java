package kiwigroup.yodelego;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import kiwigroup.yodelego.adapter.WallAdapter;
import kiwigroup.yodelego.model.Offer;
import kiwigroup.yodelego.model.User;
import kiwigroup.yodelego.server.ServerCommunication;

public class WallFragment extends Fragment implements WallAdapter.AdapterListener {
    private RecyclerView wallRecyclerView;
    private User user;
    private WallAdapter adapter;
    private LinearLayoutManager linearLayoutManager;
    private EndlessRecyclerViewScrollListener listener;

    public static WallFragment newInstance(User user) {
        WallFragment fragment = new WallFragment();
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
        return inflater.inflate(R.layout.fragment_wall, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        wallRecyclerView = view.findViewById(R.id.lvWall);
        adapter = new WallAdapter(this);
        linearLayoutManager = new LinearLayoutManager(getContext());
        wallRecyclerView.setLayoutManager(linearLayoutManager);
        wallRecyclerView.setAdapter(adapter);
        listener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                onLoadMoreOffers(page);
            }
        };
        wallRecyclerView.addOnScrollListener(listener);
        onLoadMoreOffers(0);
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

    @Override
    public void onOfferSelected(Offer offer) {
        Intent mainIntent = new Intent().setClass(getContext(), OfferDetailsActivity.class);
        mainIntent.putExtra("user", user);
        startActivity(mainIntent);
    }

    @Override
    public void onLoadMoreOffers(int page) {
        Log.d("WallFragment", "-> onLoadMoreOffers()");
        adapter.showLoading();
        /*ServerCommunication userSC = new ServerCommunication.ServerCommunicationBuilder(getActivity(), "offers/")
            .GET()
            .tokenized(true)
            .objectReturnListener(new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(response != null) {
                        try {
                            loadingMore = false;
                            adapter.notifyDataSetChanged();
                        } catch (Exception e) {
                            e.printStackTrace();
                            loadingMore = false;
                            adapter.notifyDataSetChanged();
                        }
                    } else {
                        loadingMore = false;
                        adapter.notifyDataSetChanged();
                    }
                }
            })
            .errorListener(new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    volleyError.printStackTrace();
                    loadingMore = false;
                    adapter.notifyDataSetChanged();
                }
            })
            .build();
        userSC.execute();*/

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
                Date date1 = null;
                Date date2 = null;
                try {
                    date1 = df.parse("28/01/2018");
                    date2 = df.parse("04/01/2018");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                adapter.hideLoading();
                Offer offer = new Offer(
                        "Daniela Villalobos",
                        date1,
                        "Daniela Villalobos publicó un nuevo trabajo",
                        true,
                        "Toma de inventarios",
                        15000,
                        "Se necesita personal para toma de inventarios, son 3 noches consecutivas en el Mall Marina Arauco ubicado en 15 norte, Viña del Mar.",
                        "15 norte 123234, Viña del Mar",
                        "28/01/2018",
                        Offer.OfferStatus.OPEN
                );

                Offer offer3 = new Offer(
                        "Lily Anguita",
                        date2,
                        "Lily Anguita publicó un nuevo trabajo",
                        true,
                        "Toma de inventarios",
                        15000,
                        "Se necesita personal para toma de inventarios, son 3 noches consecutivas en el Mall Marina Arauco ubicado en 15 norte, Viña del Mar.",
                        "15 norte 123234, Viña del Mar",
                        "28/01/2018",
                        Offer.OfferStatus.OPEN
                );
                adapter.append(offer);
                adapter.append(offer3);
                listener.stopLoading();
            }
        }, 3000);
    }

    public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 2;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        RecyclerView.LayoutManager mLayoutManager;

        public EndlessRecyclerViewScrollListener(LinearLayoutManager layoutManager) {
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

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScrolled(RecyclerView view, int dx, int dy) {
            int lastVisibleItemPosition = 0;
            int totalItemCount = mLayoutManager.getItemCount();

            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();

            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) {
                    this.loading = true;
                }
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            // threshold should reflect how many total columns there are too
            if (!loading && (lastVisibleItemPosition + visibleThreshold) > totalItemCount) {
                currentPage++;
                onLoadMore(currentPage, totalItemCount, view);
                loading = true;
            }
        }

        public void stopLoading(){
            int totalItemCount = mLayoutManager.getItemCount();
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }
        }

        // Call this method whenever performing new searches
        public void resetState() {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = 0;
            this.loading = true;
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore(int page, int totalItemsCount, RecyclerView view);

    }
}
