package idv.hsu.vevoplayer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import idv.hsu.vevoplayer.R;

public class Fragment_Sub extends Fragment {
    private static final String TAG = Fragment_Sub.class.getSimpleName();
    private static boolean D = true;

    private static final String PARAM_CHANNELID = "CHANNELID";
    private static final String PARAM_CHANNEL_TITLE = "CHANNELTITLE";
    private String channelId;
    private String channelTitle;
    private IOnFragmentInteractionListener mListener;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


    public static Fragment_Sub newInstance(String id, String title) {
        Fragment_Sub fragment = new Fragment_Sub();
        Bundle args = new Bundle();
        args.putString(PARAM_CHANNELID, id);
        args.putString(PARAM_CHANNEL_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Sub() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (IOnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement IOnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            channelId = getArguments().getString(PARAM_CHANNELID);
            channelTitle = getArguments().getString(PARAM_CHANNEL_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager());

        mViewPager = (ViewPager) view.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mListener.setViewPager(mViewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (D) {
            Log.d(TAG, "onResume");
        }
        mListener.setSubTitle(channelTitle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int[] titles = {
                R.string.section_playlist,
                R.string.section_channels
        };

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return Fragment_Playlists.newInstance(channelId);
                case 1:
                    return Fragment_Channels.newInstance(channelId);
            }
            return null;
        }

        @Override
        public int getCount() {
            return titles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return getString(titles[position]);
        }
    }
}
