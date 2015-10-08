//package idv.hsu.vevoplayer.conn;
//
//import android.content.ContentValues;
//
//import com.android.volley.Request;
//import com.android.volley.Response;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonArrayRequest;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.android.volley.toolbox.StringRequest;
//import com.android.volley.toolbox.Volley;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Iterator;
//import java.util.Map;
//
//public class RequestMaker {
//    private static final String TAG = RequestMaker.class.getSimpleName();
//    private static final boolean D = true;
//
//    private String url;
//    private ContentValues args;
//
//    public RequestMaker(String api_url) {
//        url = api_url;
//    }
//
//    public RequestMaker args(ContentValues pairs) {
//        args = pairs;
//        return this;
//    }
//
//    protected String requestResult() {
//        url += "?";
//        if (args != null) {
//            Iterator iterator = args.keySet().iterator();
//            while (iterator.hasNext()) {
//                String idx = iterator.next().toString();
//                url += idx + "=" + args.getAsString(idx) + "&";
//            }
//        }
//        url += "key=????";
//
//        return "TEST";
//    }
//
//    public JSONObject getJSONObjectResult() {
//        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url,
//                new Response.Listener<JSONObject>() {
//                    @Override
//                    public void onResponse(JSONObject response) {
//
//                    }
//                }, new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                    }
//                });
//        return null;
//    }
//
//    public JSONArray getJSONArrayResult() {
//        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//
//            }
//        });
//        return null;
//    }
//}