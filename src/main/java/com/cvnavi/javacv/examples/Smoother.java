package com.cvnavi.javacv.examples;

import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.*;
/**
 * @ClassName: Smoother
 * @Description: TODO(Github示例：类定义基本上是C / C ++中原始头文件的Java端口，我故意决定尽可能多地保留原始语法。 例如，下面是一个尝试加载图像文件，使其平滑并将其保存回磁盘的方法：)
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-12-12
 */
public class Smoother {
    public static void smooth(String filename) {
        IplImage image = cvLoadImage(filename);
        if (image != null) {
            cvSmooth(image, image);
            cvSaveImage(filename, image);
            cvReleaseImage(image);
        }
    }
}
