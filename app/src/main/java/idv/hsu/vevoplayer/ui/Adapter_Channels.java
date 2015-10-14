package idv.hsu.vevoplayer.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import idv.hsu.vevoplayer.R;
import idv.hsu.vevoplayer.conn.ConnControl;
import idv.hsu.vevoplayer.data.SubscriptionListResponseItems;

public class Adapter_Channels extends BaseAdapter{
    private static final String TAG = Adapter_Channels.class.getSimpleName();
    private static final boolean D = true;

    private LayoutInflater mInflater;
    private List<SubscriptionListResponseItems> listData;
    private ImageLoader mImageLoader;

    public Adapter_Channels(LayoutInflater inflater, List<SubscriptionListResponseItems> data) {
        mInflater = inflater;
        listData = data;
        mImageLoader = ConnControl.getInstance(inflater.getContext()).getmImageLoader();
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        private NetworkImageView icon;
        private TextView title;
        private ViewHolder(View view) {
            icon = (NetworkImageView) view.findViewById(android.R.id.icon);
            title = (TextView) view.findViewById(android.R.id.title);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.fragment_main_list_cell, null);
            ViewHolder holder = new ViewHolder(rowView);
            rowView.setTag(holder);
        }
        final ViewHolder holder = (ViewHolder) rowView.getTag();
        String url = listData.get(position).getSnippet().getThumbnails().getHigh().getUrl();
        if (url == null) {
            url = listData.get(position).getSnippet().getThumbnails().getDef().getUrl();
        }
        holder.icon.setImageUrl(url, mImageLoader);
        holder.title.setText(listData.get(position).getSnippet().getTitle());

        return rowView;
    }
}
