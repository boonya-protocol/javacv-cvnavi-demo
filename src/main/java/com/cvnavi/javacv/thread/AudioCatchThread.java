package com.cvnavi.javacv.thread;

import org.bytedeco.javacv.FFmpegFrameRecorder;

import javax.sound.sampled.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: AudioCatchThread
 * @Description: TODO(功能描述)
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-11-24
 */
public class AudioCatchThread implements Runnable{

    FFmpegFrameRecorder recorder;

    int AUDIO_DEVICE_INDEX;

    int FRAME_RATE;

    public AudioCatchThread(FFmpegFrameRecorder recorder, int AUDIO_DEVICE_INDEX, int FRAME_RATE) {
        this.recorder = recorder;
        this.AUDIO_DEVICE_INDEX = AUDIO_DEVICE_INDEX;
        this.FRAME_RATE = FRAME_RATE;
    }

    public void run() {
        /**
         * 设置音频编码器 最好是系统支持的格式，否则getLine() 会发生错误
         * 采样率:44.1k;采样率位数:16位;立体声(stereo);是否签名;true:
         * big-endian字节顺序,false:little-endian字节顺序(详见:ByteOrder类)
         */
        AudioFormat audioFormat = new AudioFormat(44100.0F, 16, 2, true, false);

        // 通过AudioSystem获取本地音频混合器信息
        Mixer.Info[] minfoSet = AudioSystem.getMixerInfo();
        // 通过AudioSystem获取本地音频混合器
        Mixer mixer = AudioSystem.getMixer(minfoSet[AUDIO_DEVICE_INDEX]);
        // 通过设置好的音频编解码器获取数据线信息
        DataLine.Info dataLineInfo = new DataLine.Info(TargetDataLine.class, audioFormat);
        try {
            // 打开并开始捕获音频
            // 通过line可以获得更多控制权
            // 获取设备：TargetDataLine line
            // =(TargetDataLine)mixer.getLine(dataLineInfo);
            TargetDataLine line = (TargetDataLine) AudioSystem.getLine(dataLineInfo);
            line.open(audioFormat);
            line.start();
            // 获得当前音频采样率
            int sampleRate = (int) audioFormat.getSampleRate();
            // 获取当前音频通道数量
            int numChannels = audioFormat.getChannels();
            // 初始化音频缓冲区(size是音频采样率*通道数)
            int audioBufferSize = sampleRate * numChannels;
            byte[] audioBytes = new byte[audioBufferSize];

            ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
            exec.scheduleAtFixedRate(new AudioScheduleTaskThread(recorder,line,audioBytes,sampleRate,numChannels), 0, (long) 1000 / FRAME_RATE, TimeUnit.MILLISECONDS);
        } catch (LineUnavailableException e1) {
            e1.printStackTrace();
        }
    }
}
