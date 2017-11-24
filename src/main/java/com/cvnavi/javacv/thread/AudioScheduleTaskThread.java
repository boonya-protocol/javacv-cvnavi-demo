package com.cvnavi.javacv.thread;

import org.bytedeco.javacv.FFmpegFrameRecorder;

import javax.sound.sampled.TargetDataLine;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;

/**
 * @ClassName: TaskThread
 * @Description: TODO(功能描述)
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-11-24
 */
public class AudioScheduleTaskThread implements Runnable{

    FFmpegFrameRecorder recorder;

    TargetDataLine line;

    byte[] audioBytes;

    int sampleRate;

    int numChannels;

    public AudioScheduleTaskThread(FFmpegFrameRecorder recorder, TargetDataLine line, byte[] audioBytes, int sampleRate, int numChannels) {
        this.recorder = recorder;
        this.line = line;
        this.audioBytes = audioBytes;
        this.sampleRate = sampleRate;
        this.numChannels = numChannels;
    }

    public void run() {
        try {
            // 非阻塞方式读取
            int nBytesRead = line.read(audioBytes, 0, line.available());
            // 因为我们设置的是16位音频格式,所以需要将byte[]转成short[]
            int nSamplesRead = nBytesRead / 2;
            short[] samples = new short[nSamplesRead];
            /**
             * ByteBuffer.wrap(audioBytes)-将byte[]数组包装到缓冲区
             * ByteBuffer.order(ByteOrder)-按little-endian修改字节顺序，解码器定义的
             * ByteBuffer.asShortBuffer()-创建一个新的short[]缓冲区
             * ShortBuffer.get(samples)-将缓冲区里short数据传输到short[]
             */
            ByteBuffer.wrap(audioBytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(samples);
            // 将short[]包装到ShortBuffer
            ShortBuffer sBuff = ShortBuffer.wrap(samples, 0, nSamplesRead);
            // 按通道录制shortBuffer
            recorder.recordSamples(sampleRate, numChannels, sBuff);
        } catch (org.bytedeco.javacv.FrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
}
