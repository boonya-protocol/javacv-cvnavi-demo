package com.cvnavi.ffmpeg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
/**
 * @ClassName: VideoConvertUtil
 * @Description: TODO(功能:视频转换工具http://lib.csdn.net/article/liveplay/55377?knid=1586)
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-12-01
 */
public class VideoConvertUtil implements Runnable{

    private final static Logger logger = LoggerFactory.getLogger(VideoConvertUtil.class);

    public static final String FFMPEG_PATH = "D:/ffmpeg/bin/ffmpeg.exe";

    private static Process process;

    private String hostPath;
    private String videoPath;
    private String videoFileName;

    public VideoConvertUtil(String hostPath,String videoPath,String videoFileName){
        this.hostPath = hostPath;
        this.videoPath = videoPath;
        this.videoFileName = videoFileName;
    }

    public void run() {
        convertCommand(hostPath,videoPath,videoFileName);
    }

    /**
     * 视频转换
     * @param videoPath
     * @return
     */
    public static String convertCommand(String serverBasePath,String videoPath,String videoFileName) {
        //D:/ffmpeg.exe -i D:\360Downloads\7049a5246a2d44fe897c2ea1c917eeee.wmv -vcodec libx264 -preset ultrafast -profile:v baseline -acodec aac -strict experimental -s 640*480 -b 568k -ab 128k iCulture.mp4
        if(StringUtils.isEmpty(videoPath)){
            return null;
        }
        File file = new File(videoPath);
        if (!file.exists()) {
            System.err.println("路径[" + videoPath + "]对应的视频文件不存在!");
            return null;
        }
        String videoUrl = "";
        String format = ".mp4";
        try {
//            String videoName = videoFileName.substring(0,videoFileName.lastIndexOf("."))+format;
//            String newVideoPath = video_path.substring(0,video_path.lastIndexOf("/")+1)+videoName;
            String newVideoPath = videoPath.substring(0,videoPath.lastIndexOf("."))+format;
            String videoName = videoFileName.substring(0,videoFileName.lastIndexOf("."))+format;
            Integer type = checkVideoType(videoPath);
            logger.info("old vodeo path："+videoPath);
            logger.info("new video path"+newVideoPath);
            if(0==type){
                List<String> commands = new java.util.ArrayList<String>();
                commands.add(FFMPEG_PATH);
                commands.add("-i");
                commands.add(videoPath);
                commands.add("-vcodec");
                commands.add("libx264");
                commands.add("-preset");
                commands.add("ultrafast");
                commands.add("-profile:v");
                commands.add("baseline");
                commands.add("-acodec");
                commands.add("aac");
                commands.add("-strict");
                commands.add("experimental");
//                commands.add("-s");
//                commands.add("640*480");
//                commands.add("-b");//视频品质设置（有模糊，要视频清晰使用-qscale）
//                commands.add("568k");
                commands.add("-qscale");//视频品质
                commands.add("6");//视频品质参数
                commands.add("-ab");
                commands.add("128k");
                commands.add("-y");//文件存在选择重写
                commands.add(newVideoPath);
                ProcessBuilder builder = new ProcessBuilder();
                builder.command(commands);
                process = builder.start();
                //process.waitFor();//等待进程执行完毕
                //防止ffmpeg进程塞满缓存造成死锁
                InputStream error = process.getErrorStream();
                InputStream is = process.getInputStream();
                byte[] b = new byte[1024];
                int readbytes = -1;
                try {
                    while((readbytes = error.read(b)) != -1){
                        logger.info("FFMPEG视频转换进程错误信息："+new String(b,0,readbytes));
                    }
                    while((readbytes = is.read(b)) != -1){
                        logger.info("FFMPEG视频转换进程输出内容为："+new String(b,0,readbytes));
                    }
                }catch (IOException e2){

                }finally {
                    error.close();
                    is.close();
                }
            }
            videoUrl = serverBasePath+"/wamei/upload/video/"+videoName;
            logger.info("视频格式转换："+videoUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return videoUrl;
    }

    private static Integer checkVideoType(String PATH) {
        String type = PATH.substring(PATH.lastIndexOf(".") + 1, PATH.length())
                .toLowerCase();
        // ffmpeg能解析的格式：（asx，asf，mpg，wmv，3gp，mp4，mov，avi，flv等）
        if (type.equals("avi")) {
            return 0;
        } else if (type.equals("mpg")) {
            return 0;
        } else if (type.equals("wmv")) {
            return 0;
        } else if (type.equals("3gp")) {
            return 0;
        } else if (type.equals("mov")) {
            return 0;
        } else if (type.equals("mp4")) {
            return 9;//本身是MP4格式不用转换
        } else if (type.equals("asf")) {
            return 0;
        } else if (type.equals("asx")) {
            return 0;
        } else if (type.equals("flv")) {
            return 0;
        }
        // 对ffmpeg无法解析的文件格式(wmv9，rm，rmvb等),
        else if (type.equals("wmv9")) {
            return 1;
        } else if (type.equals("rm")) {
            return 1;
        } else if (type.equals("rmvb")) {
            return 1;
        }
        return 9;
    }




    public static void main(String[] args) {
        String serverBasePath="http://172.16.20.10:9999";
        //String videoPath = "E:\\wamei_app\\04编码\\java\\idea\\wamei\\out\\artifacts\\wamei_war_exploded\\upload\\video\\20160819\\1471575811202054258.mp4";
        String videoPath = "D:/360Downloads/7049a5246a2d44fe897c2ea1c917eeee.mp4";
        //String filename = "test001.wmv";
        //String url = convertCommand(videoPath,filename);
        //System.out.println("视频链接："+url);
        for(int i=0;i<10;i++){
            logger.info("线程"+i);
            String fileName = "test00"+i+".wmv";
            VideoConvertUtil v1= new VideoConvertUtil(serverBasePath,videoPath,fileName);
            Thread t1 = new Thread(v1);
            t1.start();
        }
    }
}
