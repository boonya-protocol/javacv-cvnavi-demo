package com.cvnavi.ffmpeg.fileupload;

import java.io.*;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName: UploadVideoUtil
 * @Description: TODO(功能描述:文件上传并转换格式http://blog.csdn.net/sunroyi666/article/details/68066480)
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-11-29
 */
public class UploadVideoUtil {

    private static final Logger logger = LoggerFactory.getLogger(UploadVideoUtil.class);

    public static final String ROOT_PATH = "";//URL基础路径

    public static final String UPLOAD_ROOT_PATH = "upload";

    public static final String UPLOAD_VIDEO_PATH="videos";

    public static String getUploadPath(String path){
        return ROOT_PATH+File.separator+UPLOAD_ROOT_PATH+path;
    }

    public static String getUploadRootPath(){
        return getUploadPath("");
    };

//    public static String getUploadIMGPath(){
//        return getUploadPath(File.separator+UPLOAD_IMG_PATH);
//    }

    public static File mkDir(String path){
        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        return dir;
    }

    //生成下载根目录
    public static File mkUploadRootDir(){
        return mkDir(getUploadRootPath());
    }

    /**
     *
     * @param originalFileName
     * @return 返回原文件的后缀
     *
     */
    public static String getOriginalFileSuffix(String originalFileName){
        int index=originalFileName.lastIndexOf(".");
        if(index!=-1){
            return originalFileName.substring(index);
        }else
            return originalFileName;
    }

    /**
     *
     * @param path 这个path 是upload的子目录路径
     * @param orginalFileName
     * @return
     */
    public static File createServerFile(String path,String orginalFileName){
        // Creating the directory to store file
        File dir = mkDir(getUploadPath(path));

        String orginalFileNameSuffix = getOriginalFileSuffix(orginalFileName);

        // Create the file on server
        File serverFile = new File(dir.getAbsolutePath()
                + File.separator +new Date().getTime()+orginalFileNameSuffix);

        return serverFile;
    }

    /**
     *
     * @param file
     * @return 返回从upload目录下面的相对路径 */
    public static String getRelativePathFromUploadDir(File file){
        if(null==file)
            return "";
        String absolutePath = file.getAbsolutePath();
        if(absolutePath.indexOf(ROOT_PATH)!=-1 && ROOT_PATH.length()<absolutePath.length())
            return absolutePath.substring(absolutePath.indexOf(ROOT_PATH)+ROOT_PATH.length());
        else
            return "";
    }

    public static String uploadSingleVideoFile(MultipartFile file) {
        String path = File.separator+UPLOAD_VIDEO_PATH;
        if (!file.isEmpty()) {

            try {

                // Create the file on server
                File serverFile = createServerFile(path,file.getOriginalFilename());
                BufferedOutputStream stream = new BufferedOutputStream(
                        new FileOutputStream(serverFile));
                int length=0;
                byte[] buffer = new byte[1024];
                InputStream inputStream = file.getInputStream();
                while ((length = inputStream.read(buffer)) != -1) {
                    stream.write(buffer, 0, length);
                }
                stream.flush();
                stream.close();

                logger.info("Server File Location=" + serverFile.getAbsolutePath());
                System.out.println("文件上传完成,并开始转换:" + serverFile.getAbsolutePath());

                String ffmpegPath = ROOT_PATH + "\\scripts\\ffmpeg\\ffmpeg.exe";

                int dot = serverFile.getAbsolutePath().lastIndexOf('.');
                if ((dot >-1) && (dot < (serverFile.getAbsolutePath().length()))) {
                    String codcFilePath = serverFile.getAbsolutePath().substring(0, dot) + "_changed.mp4";

                    ExecuteCodecs executeCodecs = new ExecuteCodecs();
                    executeCodecs.exchangeToMp4(ffmpegPath, serverFile.getAbsolutePath(), codcFilePath);

                    File newFile = new File(codcFilePath);
                    return getRelativePathFromUploadDir(newFile).replaceAll("\\\\", "/");
                }
                else
                    return null;
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }

        }else{
            System.out.println("文件内容为空");
        }
        return null;
    }
}
