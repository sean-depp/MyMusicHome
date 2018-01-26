package com.so.mymusichome.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.so.mymusichome.MainActivity;

/**
 * Created by so on 2018/1/26.
 */

public class MusicService extends Service {

    private final static String TAG = "sorrower";

    private final boolean keepTrue = true;
    private MediaPlayer mediaPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        //3. 返回自定义类MyBinder对象
        return new MyBinder();
    }

    @Override
    public void onCreate() {
        mediaPlayer = new MediaPlayer();
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        super.onDestroy();
    }

    /**
     * 类MyBinder继承Binder实现接口IService
     */
    private class MyBinder extends Binder implements IService {
        //2. 定义类MyBinder继承Binder实现接口IService中的函数
        @Override
        public void callPlayMusic(String path) {
            playMusic(path);
        }

        @Override
        public void callPauseMusic() {
            pauseMusic();
        }

        @Override
        public void callConMusic() {
            conMusic();
        }

        @Override
        public void callSeekToPos(int pos) {
            seekToPos(pos);
        }
    }

    /**
     * 播放音乐
     *
     * @param path 播放文件的路径
     */
    public void playMusic(String path) {
        Log.i(TAG, "playMusic");
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.setLooping(true);
            mediaPlayer.prepare();
            mediaPlayer.start();

            updateSeekBar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新SeekBar
     */
    private void updateSeekBar() {
        //获取总时长
        final int duration = mediaPlayer.getDuration();

        //用计时器定时发送数据
//        Timer timer = new Timer();
//        TimerTask timerTask = new TimerTask() {
//            @Override
//            public void run() {
//                int currentPosition = mediaPlayer.getCurrentPosition();
//
//                Message message = Message.obtain();
//                Bundle bundle = new Bundle();
//                bundle.putInt("duration", duration);
//                bundle.putInt("currentPosition", currentPosition);
//                message.setData(bundle);
//
//                MainActivity.handler.sendMessage(message);
//            }
//        };
//        timer.schedule(timerTask, 0, 1000);

        //开启线程发送数据
        new Thread() {
            @Override
            public void run() {
                while (keepTrue) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    int currentPosition = mediaPlayer.getCurrentPosition();

                    //发送数据给activity
                    Message message = Message.obtain();
                    Bundle bundle = new Bundle();
                    bundle.putInt("duration", duration);
                    bundle.putInt("currentPosition", currentPosition);
                    message.setData(bundle);

                    MainActivity.handler.sendMessage(message);
                }
            }
        }.start();
    }

    /**
     * 暂停播放音乐
     */
    public void pauseMusic() {
        Log.i(TAG, "pauseMusic");
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    /**
     * 继续播放音乐
     */
    public void conMusic() {
        Log.i(TAG, "conMusic");
        mediaPlayer.start();
    }

    /**
     * 设置SeekBar位置
     *
     * @param pos 当前位置
     */
    public void seekToPos(int pos) {
        mediaPlayer.seekTo(pos);
    }
}
