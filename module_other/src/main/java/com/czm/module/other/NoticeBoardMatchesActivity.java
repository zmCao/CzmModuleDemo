package com.czm.module.other;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.czm.module.common.base.BaseActivity;

public class NoticeBoardMatchesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_matches);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            creatNotifcationChanner("chat","聊天消息",NotificationManager.IMPORTANCE_HIGH);

            creatNotifcationChanner("subscribe","订阅消息",NotificationManager.IMPORTANCE_DEFAULT);
        }
        findViewById(R.id.btnSendChartMsg).setOnClickListener(v -> {
            NotificationManager manager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = manager.getNotificationChannel("chat");
                if (channel.getImportance() == NotificationManager.IMPORTANCE_NONE) {
                    Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
                    intent.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
                    startActivity(intent);
                    Toast.makeText(this, "请手动将通知打开", Toast.LENGTH_SHORT).show();
                }
            }
            Notification notification = new NotificationCompat.Builder(this, "chat")
                    .setContentTitle("收到一条聊天消息")
                    .setContentText("今天中午吃什么？")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setNumber(3)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .build();
            manager.notify(1, notification);

        });
        findViewById(R.id.btnSendSubscribeMsg).setOnClickListener(v -> {
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            Notification notification = new NotificationCompat.Builder(this, "subscribe")
                    .setContentTitle("收到一条订阅消息")
                    .setContentText("地铁沿线30万商铺抢购中！")
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                    .setAutoCancel(true)
                    .build();
            manager.notify(2, notification);
        });
    }
    @TargetApi(Build.VERSION_CODES.O)
    private void creatNotifcationChanner(String channelId, String channelName, int importance)
    {
        NotificationChannel channel=new NotificationChannel(channelId,channelName,importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager= (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }
}
