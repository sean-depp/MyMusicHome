package com.so.mymusichome.service;

/**
 * Created by so on 2018/1/26.
 */

public interface IService {
    //1. 定义接口IService, 添加调用函数, 调用MusicService中对应的函数
    public void callPlayMusic(String path);

    public void callPauseMusic();

    public void callConMusic();

    public void callSeekToPos(int pos);
}
