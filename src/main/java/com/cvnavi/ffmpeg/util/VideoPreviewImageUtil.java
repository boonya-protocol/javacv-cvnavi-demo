package com.cvnavi.ffmpeg.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
/**
 * @ClassName: VideoPreviewImageUtil
 * @Description: TODO(功能：获取视频的截图http://lib.csdn.net/article/liveplay/55377?knid=1586)
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-12-01
 */
public class VideoPreviewImageUtil {

    private final static Logger logger = LoggerFactory.getLogger(VideoPreviewImageUtil.class);

    public static final String FFMPEG_PATH = "D:/ffmpeg.exe";

    public static final String serverPath = "http://172.16.20.10:9999";

    private static Process process;

    public static String processImg(String veido_path) {
        String imageUrl = "";
        File file = new File(veido_path);
        if (!file.exists()) {
            System.err.println("路径[" + veido_path + "]对应的视频文件不存在!");
            return null;
        }
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(FFMPEG_PATH);
        commands.add("-i");
        commands.add(veido_path);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-ss");
        commands.add("8");//这个参数是设置截取视频多少秒时的画面
        //commands.add("-t");
        //commands.add("0.001");
        commands.add("-s");
        commands.add("700x525");
        Long date = new Date().getTime();
        String filePath = "/wamei/upload/image/video_"+date+".jpg";
        imageUrl = serverPath+"/wamei/upload/image/video_"+date+".jpg";
        //imageUrl = serverPath+"/wamei_war_exploded/upload/image/video_"+date+".jpg";//本地测试路径
        System.out.println("图片截取原路径："+imageUrl);
        commands.add(imageUrl);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            process = builder.start();
            process.waitFor();//等待进程执行完毕
            //防止ffmpeg进程塞满缓存造成死锁
            InputStream error = process.getErrorStream();
            InputStream is = process.getInputStream();
            byte[] b = new byte[1024];
            int readbytes = -1;
            try {
                while((readbytes = error.read(b)) != -1){
                    logger.info("FFMPEG截图进程错误信息："+new String(b,0,readbytes));
                }
                while((readbytes = is.read(b)) != -1){
                    logger.info("FFMPEG截图进程输出内容为："+new String(b,0,readbytes));
                }
            }catch (IOException e2){

            }finally {
                error.close();
                is.close();
            }
            logger.info("视频封面截取："+imageUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return imageUrl;
    }

    public static boolean processImg(String veido_path, String ffmpeg_path) {
        File file = new File(veido_path);
        if (!file.exists()) {
            System.err.println("路径[" + veido_path + "]对应的视频文件不存在!");
            return false;
        }
        List<String> commands = new java.util.ArrayList<String>();
        commands.add(ffmpeg_path);
        commands.add("-i");
        commands.add(veido_path);
        commands.add("-y");
        commands.add("-f");
        commands.add("image2");
        commands.add("-ss");
        commands.add("8");//这个参数是设置截取视频多少秒时的画面
        //commands.add("-t");
        //commands.add("0.001");
        commands.add("-s");
        commands.add("700x525");
        String imageUrl = veido_path.substring(0, veido_path.lastIndexOf(".")).replaceFirst("vedio", "file") + ".jpg";
        Long date = new Date().getTime();
        String filePath = "/wamei/upload/image/video_"+date+".jpg";
        imageUrl = serverPath+filePath;
        commands.add(imageUrl);
        try {
            ProcessBuilder builder = new ProcessBuilder();
            builder.command(commands);
            builder.start();
            System.out.println("截取成功");
            System.out.println("===="+imageUrl);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    public static void main(String[] args) {
        String videoPath = "E:\\wamei_app\\04编码\\java\\idea\\wamei\\out\\artifacts\\wamei_war_exploded\\upload\\video\\20160819\\1471575811202054258.mp4";
        processImg(videoPath, VideoPreviewImageUtil.FFMPEG_PATH);
    }

}
