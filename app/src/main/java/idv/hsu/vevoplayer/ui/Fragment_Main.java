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
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;
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
import idv.hsu.vevoplayer.conn.ConnControl;
import idv.hsu.vevoplayer.data.SubscriptionListResponseItems;
import idv.hsu.vevoplayer.ui.dummy.DummyContent;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class Fragment_Main extends Fragment implements AbsListView.OnItemClickListener {
    private static final String TAG = Fragment_Main.class.getSimpleName();
    private static boolean D = true;

    private RequestQueue queue;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    JsonFactory factory = new JsonFactory();
    ObjectMapper mapper = new ObjectMapper();
    private static final String VEVO_CHANNEL_ID = "UC2pmfLm7iq6Ov1UwYrWYkZA";
    private String nextPageToken = "";
    private String prevPageToken = "";
    private boolean isLastPage = false;

    private OnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private Adapter_Main mAdapter;
    private List<SubscriptionListResponseItems> listData;
    private SwipyRefreshLayout swipy;

    // TODO: Rename and change types of parameters
    public static Fragment_Main newInstance(String param1, String param2) {
        Fragment_Main fragment = new Fragment_Main();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
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
        queue = ConnControl.getInstance(getContext()).getRequestQueue();

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main_list, container, false);

        listData = new ArrayList<>();
        mAdapter = new Adapter_Main(inflater, listData);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        swipy = ((SwipyRefreshLayout) view.findViewById(R.id.swipyrefreshlayout));
        swipy.setOnRefreshListener(new SwipyRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(SwipyRefreshLayoutDirection direction) {
                if (isLastPage) {
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
            Log.d(TAG, "onResume");
        }

        getMoreData(true);
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
        final String url =
                "https://www.googleapis.com/youtube/v3/subscriptions?" +
                "part=Snippet&"  +
                "channelId=" + VEVO_CHANNEL_ID + "&" +
                "maxResults=50&" +
                (!init ? "pageToken=" + nextPageToken + "&" : "") +
                "key=" + key;
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JsonParser parser = null;
                        try {
                            if (response.has("nextPageToken")) {
                                nextPageToken = response.getString("nextPageToken");
                                isLastPage = false;
                            } else {
                                isLastPage = true;
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
//            mListener.onFragmentInteraction(DummyContent.ITEMS.get(position).id);
        }
    }

    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }

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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }
}
