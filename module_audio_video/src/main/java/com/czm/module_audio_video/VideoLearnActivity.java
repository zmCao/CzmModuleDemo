package com.czm.module_audio_video;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.alibaba.android.arouter.facade.annotation.Route;
import com.czm.module.common.base.BaseActivity;
import com.czm.module.module_audio_video.R;

/**
 * 视频demo
 *
 * @author czm
 */
@Route(path = "/module_audio_video/VideoLearnActivity")
public class VideoLearnActivity extends BaseActivity implements View.OnClickListener {
    private Button btn_video_streaming;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_learn);
        btn_video_streaming = findViewById(R.id.btn_video_streaming);
        btn_video_streaming.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_video_streaming) {
            gotoActivity(VideoStreamingActivity.class);
        }
    }
}
