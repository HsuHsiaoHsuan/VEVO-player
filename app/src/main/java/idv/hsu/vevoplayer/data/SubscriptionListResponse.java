package idv.hsu.vevoplayer.data;

import java.util.List;

public class SubscriptionListResponse {
    public SubscriptionListResponse() {
    }

    private String kind;
    private String etag;
    private String nextPageToken;
    private PageInfo pageInfo;
    private List<SubscriptionListResponseItems> items;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getEtag() {
        return etag;
    }

    public void setEtag(String etag) {
        this.etag = etag;
    }

    public String getNextPageToken() {
        return nextPageToken;
    }

    public void setNextPageToken(String nextPageToken) {
        this.nextPageToken = nextPageToken;
    }

    public PageInfo getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(PageInfo pageInfo) {
        this.pageInfo = pageInfo;
    }

    public List<SubscriptionListResponseItems> getItems() {
        return items;
    }

    public void setItems(List<SubscriptionListResponseItems> items) {
        this.items = items;
    }
}
