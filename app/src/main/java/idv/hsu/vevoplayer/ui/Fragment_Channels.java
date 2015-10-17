package idv.hsu.vevoplayer.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;

import com.android.volley.RequestQueue;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import idv.hsu.vevoplayer.R;
import idv.hsu.vevoplayer.SubActivity;
import idv.hsu.vevoplayer.conn.ConnControl;
import idv.hsu.vevoplayer.conn.RequestMaker;
import idv.hsu.vevoplayer.data.Items;
import idv.hsu.vevoplayer.event.Event_Channels;

public class Fragment_Channels extends Fragment implements AbsListView.OnItemClickListener {
    private static final String TAG = Fragment_Channels.class.getSimpleName();
    private static boolean D = true;

    private RequestQueue queue;
    private static final String PARAM_CHANNELID = "CHANNELID";
    private String channelId;
    private String nextPageToken = null;

    private IOnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private Adapter_Channels mAdapter;
    private List<Items> listData;

    public static Fragment_Channels newInstance(String id) {
        Fragment_Channels fragment = new Fragment_Channels();
        Bundle args = new Bundle();
        args.putString(PARAM_CHANNELID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Channels() {
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
        queue = ConnControl.getInstance(getContext()).getRequestQueue();

        if (getArguments() != null) {
            channelId = getArguments().getString(PARAM_CHANNELID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        listData = new ArrayList<>();
        mAdapter = new Adapter_Channels(inflater, listData);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
        if (D) {
            Log.d(TAG, "onResume channelId = " + channelId);
        }
        if (listData.size() == 0) {
            nextPageToken = null;
            getMoreData();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    /*
           FIXME
           use your own key, create a keys.xml in values folder,
           and add a named "key" string resource.
           i.e.
           <?xml version="1.0" encoding="utf-8"?>
               <resources>
                <string name="key">(your api key)</string>
           </resources>
         */
    private void getMoreData() {
        final String key = getActivity().getString(R.string.key);
        new RequestMaker("subscriptions", "snippet")
                .channelId(channelId)
                .maxResults(50)
                .pageToken(nextPageToken)
                .build(key)
                .requestResult(new Event_Channels(), queue);
    }

    public void onEventMainThread(Event_Channels event) {
        if (D) {
            Log.d(TAG, "onEventMainThread getEvent!");
        }
        idv.hsu.vevoplayer.data.Response response = event.getData();
        nextPageToken = response.getNextPageToken();

        listData.addAll(response.getItems());
        mAdapter.notifyDataSetChanged();

        if (nextPageToken != null) {
            getMoreData();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            mListener.onFragmentInteraction(
                    SubActivity.INTERACTION_TYPE_CHANNELS,
                    listData.get(position).getSnippet().getResourceId().getChannelId(),
                    listData.get(position).getSnippet().getTitle());
        }
    }
}
