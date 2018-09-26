package kiwigroup.yodelego;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import kiwigroup.yodelego.model.User;

public class ApplicationsFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private PagerAdapter adapter;
    private OnUserFragmentsListener mListener;

    public static ApplicationsFragment newInstance(User user) {
        ApplicationsFragment fragment = new ApplicationsFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("user", user);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_application, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TabLayout tabLayout = view.findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.adjudicated_applications_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.reviewing_applications_title)));
        tabLayout.addTab(tabLayout.newTab().setText(getResources().getString(R.string.complete_applications_title)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = view.findViewById(R.id.pager);
        viewPager.setOffscreenPageLimit(1);

        adapter = new PagerAdapter(((MainActivity)getContext()).getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
        });

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserFragmentsListener) {
            mListener = (OnUserFragmentsListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnRegisterFragmentListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        AdjudicatedApplicationsFragment mAdjudicatedApplicationsFragment;
        ReviewingApplicationsFragment mReviewingApplicationsFragment;
        CompleteApplicationsFragment mCompleteApplicationsFragment;

        PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    if(mAdjudicatedApplicationsFragment == null)
                        mAdjudicatedApplicationsFragment = new AdjudicatedApplicationsFragment();
                    mAdjudicatedApplicationsFragment.updateData();
                    return mAdjudicatedApplicationsFragment;
                case 1:
                    if(mReviewingApplicationsFragment == null)
                        mReviewingApplicationsFragment = new ReviewingApplicationsFragment();
                    mReviewingApplicationsFragment.updateData();
                    return mReviewingApplicationsFragment;
                case 2:
                    if(mCompleteApplicationsFragment == null)
                        mCompleteApplicationsFragment = new CompleteApplicationsFragment();
                    mCompleteApplicationsFragment.updateData();
                    return mCompleteApplicationsFragment;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
    }

    public void updateFragmentsData(){
        if(adapter.mAdjudicatedApplicationsFragment != null){
            adapter.mAdjudicatedApplicationsFragment.cleanWall();
            adapter.mAdjudicatedApplicationsFragment.updateData();
        }
        if(adapter.mReviewingApplicationsFragment != null){
            adapter.mReviewingApplicationsFragment.cleanWall();
            adapter.mReviewingApplicationsFragment.updateData();
        }
        if(adapter.mCompleteApplicationsFragment != null){
            adapter.mCompleteApplicationsFragment.cleanWall();
            adapter.mCompleteApplicationsFragment.updateData();
        }
    }

}