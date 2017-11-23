package com.cvnavi.javacv;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameGrabber;
import sun.applet.Main;
import javax.swing.*;
/**
 * @ClassName: CallCameraTest
 * @Description: TODO(功能描述)
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-11-23
 */
public class CallCameraTest {

    /**
     * 测试JavaCV打开摄像头
     * @param args
     * @throws Exception
     * @throws InterruptedException
     */
    public static void main(String[] args) throws Exception, InterruptedException{
        OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
        grabber.start();
        //开始获取摄像头数据
        CanvasFrame canvas = new CanvasFrame("摄像头");
        //新建一个窗口
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.setAlwaysOnTop(true);

        while(true)
        {
            if(!canvas.isDisplayable())
            {//窗口是否关闭
                grabber.stop();//停止抓取
                System.exit(2);//退出
            }
            canvas.showImage(grabber.grab());//获取摄像头图像并放到窗口上显示， 这里的Frame frame=grabber.grab(); frame是一帧视频图像

            Thread.sleep(50);//50毫秒刷新一次图像
        }
    }
}
