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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.greenrobot.event.EventBus;
import idv.hsu.vevoplayer.R;
import idv.hsu.vevoplayer.SubActivity;
import idv.hsu.vevoplayer.conn.ConnControl;
import idv.hsu.vevoplayer.conn.RequestMaker;
import idv.hsu.vevoplayer.data.Items;
import idv.hsu.vevoplayer.event.Event_Channels;
import idv.hsu.vevoplayer.event.Event_Playlist;

public class Fragment_Playlists extends Fragment implements AbsListView.OnItemClickListener {
    private static final String TAG = Fragment_Playlists.class.getSimpleName();
    private static boolean D = true;

    private RequestQueue queue;
    private static final String PARAM_PLAYLISTID = "PLAYLISTID";
    private String channelId;
    private String nextPageToken = null;

    private IOnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private Adapter_Channels mAdapter;
    private List<Items> listData;

    public static Fragment_Playlists newInstance(String id) {
        Fragment_Playlists fragment = new Fragment_Playlists();
        Bundle args = new Bundle();
        args.putString(PARAM_PLAYLISTID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_Playlists() {
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
            channelId = getArguments().getString(PARAM_PLAYLISTID);
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
        new RequestMaker("playlists", "snippet")
                .channelId(channelId)
                .maxResults(50)
                .pageToken(nextPageToken)
                .build(key)
                .requestResult(new Event_Playlist(), queue);
    }

    public void onEventMainThread(Event_Playlist event) {
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
                    SubActivity.INTERACTION_TYPE_PLAYLIST,
                    listData.get(position).getId(),
                    listData.get(position).getSnippet().getTitle());
        }
    }
}
