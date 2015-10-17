package idv.hsu.vevoplayer.event;

import org.json.JSONObject;

import idv.hsu.vevoplayer.data.Response;

public interface IEvent {
    public void setData(Response response);
    public Response getData();
}
