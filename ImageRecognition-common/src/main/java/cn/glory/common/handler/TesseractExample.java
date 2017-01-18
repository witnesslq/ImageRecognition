package cn.glory.common.handler;

import cn.glory.common.utils.NativeUtils;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;

/**
 * @author Glory
 * @create 2017-01-18 22:00
 **/
public class TesseractExample {
    private static final Logger logger = LoggerFactory.getLogger(TesseractExample.class);
    // 默认的资源位置
    private static volatile String resourcesPath;
    /*
     * 之前获取路径是通过NativeUtils.class.getResource("/").getPath()获取
     * 当在普通jar包(crawler)执行时会报空指针异常,但是在web环境和开发环境下是不会有这种错误
     * 为了兼容在crawler.jar下正常运行和保留原来的自动拷贝资源位置,改为使用URL方式获取
     */
    static {
        URL url = NativeUtils.class.getProtectionDomain().getCodeSource().getLocation();
        try {
            resourcesPath = java.net.URLDecoder.decode(url.getPath(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(),e);
        }

        // 获取拷贝后的资源位置
        String copyResourcesPath = new NativeUtils().loadLibraryFromJar();
        // 如果拷贝过程中出现异常,使用默认的资源位置,否则使用拷贝后的资源位置
        resourcesPath = "".equals(copyResourcesPath) ? resourcesPath : copyResourcesPath;
        if (System.getProperty("os.name").toLowerCase().contains("linux")) {
            System.load(resourcesPath + "/linux-x86-64/liblept.so.5");
            System.load(resourcesPath + "/linux-x86-64/libtesseract.so");
        }
        logger.info("final resourcesPath is :{}",resourcesPath);
    }
    public static void main(String[] args) {
        File imageFile = new File("E:\\sample\\sample_1.png");
        ITesseract instance = new Tesseract();  // JNA Interface Mapping
        // ITesseract instance = new Tesseract1(); // JNA Direct Mapping

        try {
            String result = instance.doOCR(imageFile);
            System.out.println(result);
        } catch (TesseractException e) {
            System.err.println(e.getMessage());
        }
    }
}
