package com.cvnavi.javacv.util;

import org.bytedeco.javacv.Frame;
import org.bytedeco.javacpp.opencv_core.IplImage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacv.OpenCVFrameConverter;

/**
 * @ClassName: FormatConverter
 * @Description: TODO(javaCV图像处理之Frame、Mat和IplImage三者相互转换(使用openCV进行Mat和IplImage转换))
 * @author: pengjunlin
 * @company: 上海势航网络科技有限公司
 * @date 2017-11-27
 */
public class FormatConverter {

    static OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();

    /**
     * 将Frame转为Mat
     * @param frame
     * @return
     */
    public static Mat converFrameToMat(Frame frame) {
        return converter.convertToMat(frame);
    }

    /**
     * 将Mat转为Frame
     * @param mat
     * @return
     */
    public static Frame converMatToFrame(Mat mat) {
        return  converter.convert(mat);
    }

    /**
     * 将Frame转为IplImage
     * @param frame
     * @return
     */
    public static IplImage converFrameToIplImage(Frame frame) {
        IplImage image1 = converter.convertToIplImage(frame);
        IplImage image2 = converter.convert(frame);
        return image1!=null?image1:image2;
    }

    /**
     * 将IplImage转为Frame
     * @param iplImage
     * @return
     */
    public static Frame convertIplImageToFrame(IplImage iplImage) {
       return  converter.convert(iplImage);
    }

    /**
     * Mat转IplImage
     * @param mat
     * @return
     */
    public static IplImage convertMatToIplImage(Mat mat) {
       return  new IplImage(mat);
    }

    /**
     * IplImage转Mat
     * @param iplImage
     * @return
     */
    public static Mat convertIplImageToMat(IplImage iplImage) {
        return new Mat(iplImage);
    }


}
