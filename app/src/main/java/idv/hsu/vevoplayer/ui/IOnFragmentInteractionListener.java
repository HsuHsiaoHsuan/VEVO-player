package idv.hsu.vevoplayer.ui;

import android.support.v4.view.ViewPager;

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */
public interface IOnFragmentInteractionListener {
    public void onFragmentInteraction(String id, String title);

    public void setViewPager(ViewPager viewPager);

    public void setSubTitle(String subTitle);
}
