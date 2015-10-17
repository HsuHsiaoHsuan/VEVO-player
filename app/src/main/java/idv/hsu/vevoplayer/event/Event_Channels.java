package idv.hsu.vevoplayer.event;

import idv.hsu.vevoplayer.data.Response;

public class Event_Channels implements IEvent {
    private Response response;

    @Override
    public void setData(Response response) {
        this.response = response;
    }

    @Override
    public Response getData() {
        return response;
    }
}
