package com.czm.module_audio_video;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.czm.module.common.utils.Utils;
import com.czm.module.module_audio_video.R;
import com.hikvision.sdk.net.bean.SubResourceNodeBean;

import java.util.ArrayList;
import java.util.List;

public class HIKVisionAdapter extends BaseAdapter {
    private Context mContext;
    private List<SubResourceNodeBean> subResourceNodeBeanArrayList = new ArrayList<>();

    private String imgUrl = "";

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
        notifyDataSetChanged();
    }

    public HIKVisionAdapter(Context mContext, List<SubResourceNodeBean> monitoringInfoList) {
        super();
        this.mContext = mContext;
        this.subResourceNodeBeanArrayList = monitoringInfoList;
    }

    @Override
    public int getCount() {
        return subResourceNodeBeanArrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return subResourceNodeBeanArrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_video, null);
            holder.img_snapshot = (ImageView) convertView.findViewById(R.id.img_snapshot);
            holder.txt_video_name = (TextView) convertView.findViewById(R.id.txt_video_name);
            holder.img_play = (ImageView) convertView.findViewById(R.id.img_play);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        Glide.with(Utils.getContext())
                .load(imgUrl)
                .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.img_snapshot);
        holder.txt_video_name.setText(subResourceNodeBeanArrayList.get(position).getName());
        return convertView;

    }

    class ViewHolder {
        private ImageView img_snapshot;
        private ImageView img_play;
        private TextView txt_video_name;
    }
}