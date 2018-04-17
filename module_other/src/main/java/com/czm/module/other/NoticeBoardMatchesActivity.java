package com.czm.module.other;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.czm.module.common.base.BaseActivity;

public class NoticeBoardMatchesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_matches);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            creatNotifcationChanner("chart","聊天消息",NotificationManager.IMPORTANCE_HIGH);
            creatNotifcationChanner("subscrble","订阅消息",NotificationManager.IMPORTANCE_DEFAULT);
        }
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void creatNotifcationChanner(String channelId, String channelName, int importance)
    {
        NotificationChannel channel=new NotificationChannel(channelId,channelName,importance);
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
