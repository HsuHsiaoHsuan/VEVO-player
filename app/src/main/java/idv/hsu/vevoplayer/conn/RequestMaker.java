package idv.hsu.vevoplayer.conn;

import android.util.Log;

import com.android.volley.DefaultRetryPolicy;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;

import de.greenrobot.event.EventBus;
import idv.hsu.vevoplayer.data.Items;
import idv.hsu.vevoplayer.event.IEvent;

public class RequestMaker {
    private static final String TAG = RequestMaker.class.getSimpleName();
    private static final boolean D = true;

    private StringBuffer url = new StringBuffer("https://www.googleapis.com/youtube/v3/");

    public RequestMaker(String api, String part) {
        url.append(api + "?part=" + part);
    }

    public RequestMaker channelId(String channelId) {
        url.append("&channelId=" + channelId);
        return this;
    }

    public RequestMaker maxResults(int maxResults) {
        url.append("&maxResults=" + maxResults);
        return this;
    }

    public RequestMaker pageToken(String pageToken) {
        if (pageToken != null) {
            url.append("&pageToken=" + pageToken);
        }
        return this;
    }

    public RequestMaker playlistId(String playlistId) {
        url.append("&playlistId=" + playlistId);
        return this;
    }

    public RequestMaker build(String key) {
        url.append("&key=" + key);
        return this;
    }

    public void requestResult(final IEvent event, RequestQueue queue) {
        final JsonFactory factory = new JsonFactory();
        final ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url.toString(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (D) {
                            Log.d(TAG, "onResponse: " + response);
                        }
                        JsonParser parser = null;
                        try {
                            parser = factory.createParser(response.toString());
                            idv.hsu.vevoplayer.data.Response data =
                                    mapper.readValue(parser, idv.hsu.vevoplayer.data.Response.class);
                            event.setData(data);
                            EventBus.getDefault().post(event);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
        });
        request.setRetryPolicy(
                new DefaultRetryPolicy(5000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                                       DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(request);
    }
}