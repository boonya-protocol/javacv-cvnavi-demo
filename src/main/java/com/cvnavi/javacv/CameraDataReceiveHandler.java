package com.cvnavi.javacv;

import org.bytedeco.javacv.*;

/**
 * @ClassName: CameraReceiveHandler
 * @Description: TODO(收流器实现，录制流媒体服务器的rtsp/rtmp视频文件(基于javaCV-FFMPEG))
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-11-24
 */
public class CameraDataReceiveHandler {

    /**
     * 按帧录制视频
     *
     * @param inputFile-该地址可以是网络直播/录播地址，也可以是远程/本地文件路径
     * @param outputFile
     *            -该地址只能是文件地址，如果使用该方法推送流媒体服务器会报错，原因是没有设置编码格式
     * @throws Exception
     * @throws FrameRecorder.Exception
     * @throws org.bytedeco.javacv.FrameRecorder.Exception
     */
    public static void frameRecord(String inputFile, String outputFile, int audioChannel)
            throws Exception,FrameRecorder.Exception {

        boolean isStart=true;//该变量建议设置为全局控制变量，用于控制录制结束
        // 获取视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        // 流媒体输出地址，分辨率（长，高），是否录制音频（0:不录制/1:录制）
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(outputFile, 1280, 720, audioChannel);
        // 开始取视频源
        recordByFrame(grabber, recorder, isStart);
    }

    private static void recordByFrame(FFmpegFrameGrabber grabber, FFmpegFrameRecorder recorder, Boolean status)
            throws Exception, FrameRecorder.Exception {
        try {//建议在线程中使用该方法
            grabber.start();
            recorder.start();
            Frame frame = null;
            while (status&& (frame = grabber.grabFrame()) != null) {
                recorder.record(frame);
            }
            recorder.stop();
            grabber.stop();
        } finally {
            if (grabber != null) {
                grabber.stop();
            }
        }
    }

    /**
     * inputFile设置为服务器播放地址，outputFile设置为本地地址，这里演示.mp4，也可以是flv等其他后缀名
     * @param args
     * @throws Exception
     * @throws FrameRecorder.Exception
     * @throws FrameGrabber.Exception
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception,FrameRecorder.Exception, FrameGrabber.Exception, InterruptedException {

        String inputFile = "rtsp://admin:admin@192.168.2.236:37779/cam/realmonitor?channel=1&subtype=0";
        // Decodes-encodes
        String outputFile = "C:\\record.mp4";
        frameRecord(inputFile, outputFile,1);
    }
}
