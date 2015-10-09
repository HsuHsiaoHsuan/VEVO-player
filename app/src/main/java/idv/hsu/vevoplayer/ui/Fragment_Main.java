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

    private static final String VEVO_CHANNEL_ID = "UC2pmfLm7iq6Ov1UwYrWYkZA";
    private OnFragmentInteractionListener mListener;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


    public static Fragment_Main newInstance() {
        Fragment_Main fragment = new Fragment_Main();
        return fragment;
    }

    public Fragment_Main() {
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private int[] titles = {
                R.string.section_main_list,
                R.string.section_favor
        };

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return Fragment_Channels.newInstance("VIEW", String.valueOf(position), VEVO_CHANNEL_ID);
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
