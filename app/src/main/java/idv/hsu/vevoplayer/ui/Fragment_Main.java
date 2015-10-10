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

public class Fragment_Main extends Fragment {
    private static final String TAG = Fragment_Main.class.getSimpleName();
    private static boolean D = true;

    private static final String PARAM_CHANNELID = "CHANNELID";
    private String channelId;
    private IOnFragmentInteractionListener mListener;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


    public static Fragment_Main newInstance(String id) {
        Fragment_Main fragment = new Fragment_Main();
        Bundle args = new Bundle();
        args.putString(PARAM_CHANNELID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Main() {
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

        mListener.setSubTitle(null);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (D) {
            Log.d(TAG, "onResume");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int[] titles = {
                R.string.section_main_list
        };

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment_Channels.newInstance(channelId);
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
