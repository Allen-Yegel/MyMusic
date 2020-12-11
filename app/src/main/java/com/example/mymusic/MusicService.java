package com.example.mymusic;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService {
    public static final File PATH = Environment.getExternalStoragePublicDirectory("/Download"); //获取SD卡总目录
    //getExternalStoragePublicDirectory（“type”）返回在根目录下的名为type的文件夹
    public List<String> musicList; //存放找到的所有mp3的绝对路径
    public List<Integer> suijilist;
    public MediaPlayer player; //定义多媒体对象
    public int songNum; //当前播放的歌曲在List中的下标，flag为标志
    public String songName; //当前播放的歌曲名

    static class MusicFilter implements FilenameFilter{
    //文件名过滤器，用来过滤不符合规格的文件名，并返回合格的文件；
        public boolean accept(File dir, String name){
            return(name.endsWith(".mp3")); //返回当前目录所有以.mp3结尾的文件
        }
    }

    public MusicService(){ //功能：扫描并获取mp3文件
        super();
        player = new MediaPlayer();
        musicList = new ArrayList<String>(); //构造动态数组存放所有mp3文件的绝对路径
        try {
            File MUSIC_PATH = new File(String.valueOf(PATH)); //获取Music文件的二级目录
            if(MUSIC_PATH.exists()){
                File[] files = MUSIC_PATH.listFiles(new MusicFilter());//将获取到的所有文件存入文件数组中
                //listFiles方法是返回某个目录文件下所有文件和目录的绝对路径，返回的是File数组
                if(files == null || files.length == 0){
                    Log.e("TAG",String.format("数据为空"));//Log.e仅显示红色的错误信息
                    return;
                }
                int length = files.length;
                if(length > 0){ //当数组不空时，遍历数组文件
                    for (File file : MUSIC_PATH.listFiles(new MusicFilter())){//for(元素类型：遍历对象)
                        musicList.add(file.getAbsolutePath()); //将数组文件的据对路径存入数组
                    }
                }
            }
        } catch (Exception e) {
            Log.i("TAG",String.format("读取文件异常%s",e.getMessage()));//log.i提示性信息
        }
    }

    public void setPlayName(String dataSource){ //截取音乐文件名
        File file = new File(dataSource); //假设其为D:\\abc.mp3
        String name = file.getName(); //name = abc.mp3
        int index = name.lastIndexOf("."); //找到最后一个.
        songName = name.substring(0,index); //截取为abc
    }

    public void play(){ //播放音乐
        try{
            player.reset(); //重置多媒体
            String dataSource = musicList.get(songNum); //得到当前播放音乐的路径
            setPlayName(dataSource); //截取歌名
            //指定播放流媒体的类型
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.setDataSource(dataSource); //为多媒体对象设置播放路径
            player.prepare(); //准备播放
            player.start(); //开始播放
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    next();
                }
            });
        } catch (IOException e) {
            Log.v("MusicService",e.getMessage());//输出任何消息
        }
    }

    public void goPlay(){ //继续播放
        int position = getCurrentProgress(); //获取当前播放的进度
        player.seekTo(position); //设置当前MediaPlayer的播放位置，单位是毫秒。
        try {
            player.prepare(); //同步的方式装载流媒体文件。
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.start();
    }

    public void randPlay(){
        songNum = new Random().nextInt(musicList.size() - 1);//介于0-数组最大值范围随机生成一个整数
        play();
    }

    public void shunxu(){play();}

    public int getCurrentProgress(){ //获取当前进度
        if(player != null & player.isPlaying()){
            return player.getCurrentPosition();
        } else if (player != null & (!player.isPlaying())){
            return player.getCurrentPosition();
        }
        return 0;
    }

    public void next(){ //下一曲
        songNum = songNum == musicList.size() - 1 ? 0 : songNum + 1;
        play();
    }

    public void last(){ //上一曲
        songNum = songNum == 0 ? musicList.size() - 1 : songNum - 1;
        play();
    }

    public void pause(){ //暂停播放
        if(player != null && player.isPlaying()){
            player.pause();
        }
    }

    public void stop(){ //停止播放
        if (player != null && player.isPlaying()){
            player.stop();
            player.reset();
        }
    }

}
