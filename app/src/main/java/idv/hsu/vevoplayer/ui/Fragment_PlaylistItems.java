package idv.hsu.vevoplayer.ui;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewPropertyAnimator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
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
import com.google.android.youtube.player.YouTubeApiServiceUtil;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import idv.hsu.vevoplayer.PlayActivity;
import idv.hsu.vevoplayer.R;
import idv.hsu.vevoplayer.conn.ConnControl;
import idv.hsu.vevoplayer.conn.RequestMaker;
import idv.hsu.vevoplayer.data.SubscriptionListResponseItems;

public class Fragment_PlaylistItems extends Fragment implements AbsListView.OnItemClickListener, YouTubePlayer.OnFullscreenListener {
    private static final String TAG = Fragment_PlaylistItems.class.getSimpleName();
    private static boolean D = true;

    private RequestQueue queue;
    private String playlistId;
    JsonFactory factory = new JsonFactory();
    ObjectMapper mapper = new ObjectMapper();
    private String nextPageToken = "INIT";
    private String prevPageToken = "INIT";
    private boolean isFullscreen;

    private IOnFragmentInteractionListener mListener;
    private AbsListView mListView;
    private Adapter_Channels mAdapter;
    private List<SubscriptionListResponseItems> listData;
    private SwipyRefreshLayout swipy;
    private View videoBox;
    private VideoFragment videoFragment;

    public static Fragment_PlaylistItems newInstance(String id) {
        Fragment_PlaylistItems fragment = new Fragment_PlaylistItems();
        Bundle args = new Bundle();
        args.putString(PlayActivity.PARAM_PLAYLISTID, id);
        fragment.setArguments(args);
        return fragment;
    }

    public Fragment_PlaylistItems() {
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
            playlistId = getArguments().getString(PlayActivity.PARAM_PLAYLISTID);
        }

        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play_list, container, false);

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

        videoBox = view.findViewById(R.id.video_box);
        videoFragment = (VideoFragment) getChildFragmentManager().findFragmentById(R.id.player);

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
        String url = new RequestMaker("playlistItems", "snippet")
                .playlistId(playlistId)
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
//        if (null != mListener) {
//            mListener.onFragmentInteraction(listData.get(position).getSnippet().getResourceId().getChannelId());
//            mListener.setSubTitle(listData.get(position).getSnippet().getResourceId().getChannelId());
//        }
        String videoID = listData.get(position).getSnippet().getResourceId().getVideoId();
        videoFragment.setVideoId(videoID);
    }

    @Override
    public void onFullscreen(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;

        layout();
    }

    /**
     * Sets up the layout programatically for the three different states. Portrait, landscape or
     * fullscreen+landscape. This has to be done programmatically because we handle the orientation
     * changes ourselves in order to get fluent fullscreen transitions, so the xml layout resources
     * do not get reloaded.
     */
    private void layout() {
        boolean isPortrait =
                getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        getView().setVisibility(isFullscreen ? View.GONE : View.VISIBLE);
        mListView.setVisibility(isPortrait ? View.VISIBLE : View.GONE);

        if (isFullscreen) {
            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.

            setLayoutSize(videoFragment.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, MATCH_PARENT, Gravity.TOP | Gravity.LEFT);
        } else if (isPortrait) {
            setLayoutSize(listFragment.getView(), MATCH_PARENT, MATCH_PARENT);
            setLayoutSize(videoFragment.getView(), MATCH_PARENT, WRAP_CONTENT);
            setLayoutSizeAndGravity(videoBox, MATCH_PARENT, WRAP_CONTENT, Gravity.BOTTOM);
        } else {
//            videoBox.setTranslationY(0); // Reset any translation that was applied in portrait.
//            int screenWidth = dpToPx(getResources().getConfiguration().screenWidthDp);
//            setLayoutSize(listFragment.getView(), screenWidth / 4, MATCH_PARENT);
//            int videoWidth = screenWidth - screenWidth / 4 - dpToPx(LANDSCAPE_VIDEO_PADDING_DP);
//            setLayoutSize(videoFragment.getView(), videoWidth, WRAP_CONTENT);
//            setLayoutSizeAndGravity(videoBox, videoWidth, WRAP_CONTENT,
//                    Gravity.RIGHT | Gravity.CENTER_VERTICAL);
        }
    }

    public void onClickClose(@SuppressWarnings("unused") View view) {
        mListView.clearChoices();
        mListView.requestLayout();
        videoFragment.pause();
        ViewPropertyAnimator animator = videoBox.animate()
                .translationYBy(videoBox.getHeight())
                .setDuration(300);
        runOnAnimationEnd(animator, new Runnable() {
            @Override
            public void run() {
                videoBox.setVisibility(View.INVISIBLE);
            }
        });
    }

    @TargetApi(16)
    private void runOnAnimationEnd(ViewPropertyAnimator animator, final Runnable runnable) {
        if (Build.VERSION.SDK_INT > 16) {
            animator.withEndAction(runnable);
        } else {
            animator.setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    runnable.run();
                }
            });
        }
    }

    private static void setLayoutSize(View view, int width, int height) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    private static void setLayoutSizeAndGravity(View view, int width, int height, int gravity) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        params.gravity = gravity;
        view.setLayoutParams(params);
    }
}
