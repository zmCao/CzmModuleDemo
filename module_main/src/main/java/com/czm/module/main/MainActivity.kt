package com.czm.module.main

import android.os.Bundle
import android.widget.Button

import com.alibaba.android.arouter.launcher.ARouter
import com.czm.module.common.base.BaseActivity

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val btnCustomControlDemo = findViewById<Button>(R.id.btnCustomControlDemo)
        btnCustomControlDemo.setOnClickListener { v -> ARouter.getInstance().build("/customcontrol/CustomControlLearnActivity").navigation() }
        val btnGreenDaoDemo = findViewById<Button>(R.id.btnGreenDaoDemo)
        btnGreenDaoDemo.setOnClickListener { v -> ARouter.getInstance().build("/GreenDao/GreenDaoLearnActivity").navigation() }
        val btnNetty = findViewById<Button>(R.id.btnNetty)
        btnNetty.setOnClickListener { v -> ARouter.getInstance().build("/netty/NettyLearnActivity").navigation() }
        val btnOther = findViewById<Button>(R.id.btnOther)
        btnOther.setOnClickListener { v -> ARouter.getInstance().build("/other/OtherLearnActivity").navigation() }
        val btnSerialPort = findViewById<Button>(R.id.btnSerialPort)
        btnSerialPort.setOnClickListener { v -> ARouter.getInstance().build("/port/SerialPortLearnActivity").navigation() }
        val btnAudioVideo = findViewById<Button>(R.id.btnAudioVideo)
        btnAudioVideo.setOnClickListener { v -> ARouter.getInstance().build("/module_audio_video/VideoLearnActivity").navigation() }
    }
}
