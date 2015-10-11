package idv.hsu.vevoplayer.conn;

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
        if (!pageToken.equals("INIT") && !pageToken.equals("END")) {
            url.append("&pageToken=" + pageToken);
        }
        return this;
    }

    public RequestMaker playlistId(String playlistId) {
        url.append("&playlistId=" + playlistId);
        return this;
    }

    public String build(String key) {
        url.append("&key=" + key);
        return url.toString();
    }
}