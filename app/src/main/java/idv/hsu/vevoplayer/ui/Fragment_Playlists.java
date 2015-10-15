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

import idv.hsu.vevoplayer.R;
import idv.hsu.vevoplayer.SubActivity;
import idv.hsu.vevoplayer.conn.ConnControl;
import idv.hsu.vevoplayer.conn.RequestMaker;
import idv.hsu.vevoplayer.data.SubscriptionListResponseItems;

public class Fragment_Playlists extends Fragment implements AbsListView.OnItemClickListener {
    private static final String TAG = Fragment_Playlists.class.getSimpleName();
    private static boolean D = true;

    private RequestQueue queue;
    private static final String PARAM_PLAYLISTID = "PLAYLISTID";
    private String channelId;
    JsonFactory factory = new JsonFactory();
    ObjectMapper mapper = new ObjectMapper();
    private String nextPageToken = "INIT";
    private String prevPageToken = "INIT";

    private IOnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private Adapter_Channels mAdapter;
    private List<SubscriptionListResponseItems> listData;
    private SwipyRefreshLayout swipy;

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

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
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

        swipy = ((SwipyRefreshLayout) view.findViewById(R.id.swipyrefreshlayout));
        swipy.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (nextPageToken.equals("END")) {
                    Toast.makeText(getContext(), R.string.alarm_last_page, Toast.LENGTH_LONG).show();
                    swipy.setRefreshing(false);
                } else {
                    getMoreData(false);
                }
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (D) {
            Log.d(TAG, "onResume channelId = " + channelId);
        }

        if (listData.size() == 0) {
            nextPageToken = "INIT";
            getMoreData(true);
        }
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
    private void getMoreData(final boolean init) {
        final String key = getActivity().getString(R.string.key);
        String url = new RequestMaker("playlists", "snippet")
                .channelId(channelId)
                .maxResults(50)
                .pageToken(nextPageToken)
                .build(key);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JsonParser parser = null;
                        try {
                            if (response.has("nextPageToken")) {
                                nextPageToken = response.getString("nextPageToken");
                            } else {
                                nextPageToken = "END";
                                Toast.makeText(getContext(), R.string.alarm_last_page, Toast.LENGTH_LONG).show();
                            }

                            if (response.has("prevPageToken")) {
                                prevPageToken = response.getString("prevPageToken");
                            } else {
//                                Toast.makeText(getContext(), "First Page!", Toast.LENGTH_LONG).show();
                            }

                            parser = factory.createParser(response.getJSONArray("items").toString());
                            SubscriptionListResponseItems[] items = mapper.readValue(parser, SubscriptionListResponseItems[].class);
                            if (init) { listData.clear(); }
                            listData.addAll(Arrays.asList(items));
                            mAdapter.notifyDataSetChanged();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            if (swipy.isRefreshing()) { swipy.setRefreshing(false); }
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (swipy.isRefreshing()) { swipy.setRefreshing(false); }
            }
        });
        queue.add(request);
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
