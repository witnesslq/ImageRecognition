package cn.glory.common.utils;

import org.slf4j.Logger;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * 复制jar包里面的资源文件
 * Create by Glory 2017-01-18
 */
public class NativeUtils {
	private static final Logger log = getLogger(NativeUtils.class);
	/**
	 * 在tomcat环境中直接解压到classes下面的话文件大小为0kb
	 * 右键直接运行或tomcat环境下加个目录就不会了
	 */
	private String unzipFolder;
	
	private static final String EXT = "libraryFromJar";

	/**
	 * 默认加载图片识别的配置文件到指定目录
	 * @return
	 */
	public  String loadLibraryFromJar() {
		String result = "";
		try {
			URL url = NativeUtils.class.getProtectionDomain().getCodeSource().getLocation();
			unzipFolder = java.net.URLDecoder.decode(url.getPath(), "utf-8");
			
			log.info("current unzipFolder is :{}",unzipFolder);
			if(unzipFolder.endsWith(".jar")){
				unzipFolder = unzipFolder.substring(0, unzipFolder.lastIndexOf('/') + 1);
			}
			log.info("current unzipFolder is :{}",unzipFolder);
			
			if(System.getProperty("os.name").toLowerCase().indexOf("linux") >=0 ){
				unzipFolder += EXT;
				loadLibraryFromJar("/linux-x86-64/liblept.so.5");
				loadLibraryFromJar("/linux-x86-64/libtesseract.so");
			}else{
				unzipFolder =unzipFolder.substring(1) + EXT;
				loadLibraryFromJar("/win32-x86-64/gsdll64.dll");
				loadLibraryFromJar("/win32-x86-64/libtesseract304.dll");
			}
			loadLibraryFromJar("/lib/pdfpagecount.ps");
			loadLibraryFromJar("/tessdata/eng.traineddata");
			loadLibraryFromJar("/tessdata/osd.traineddata");
			loadLibraryFromJar("/tessdata/pdf.ttf");
			loadLibraryFromJar("/tessdata/configs/api_config");
			loadLibraryFromJar("/tessdata/configs/digits");
			loadLibraryFromJar("/tessdata/configs/hocr");
			loadLibraryFromJar("/tessdata/configs/pdf");
			loadLibraryFromJar("/tessdata/configs/txt");
			loadLibraryFromJar("/tessdata/configs/unlv");
			result = unzipFolder;
		} catch (IOException ignored) {
			log.error(ignored.getMessage(),ignored);
		}
		return result;
	}

	/**
	 * 将path的文件复制到指定目录
	 * @param path	资源文件所在的位置
	 * @throws IOException
	 */
	public void loadLibraryFromJar(String path) throws IOException {
		if (!path.startsWith("/")) {
			throw new IllegalArgumentException("The path has to be absolute (start with '/').");
		}

		int index = path.lastIndexOf('/');

		String filename = path.substring(index + 1);
		String folderPath = unzipFolder +  path.substring(0, index + 1);
		
		// If the folder does not exist yet, it will be created. If the folder exists already, it will be ignored
		File dir = new File(folderPath);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		// If the file does not exist yet, it will be created. If the file exists already, it will be ignored
		filename = folderPath + filename;
		File file = new File(filename);

		if (!file.exists() && !file.createNewFile()) {
			log.error("create file :{} failed",filename);
			return;
		}

		// Prepare buffer for data copying
		byte[] buffer = new byte[1024];
		int readBytes;

		// Open and check input stream
		URL url = getClass().getResource(path); 
		URLConnection  urlConnection =url.openConnection(); 
		InputStream is =urlConnection.getInputStream();
		
		if (is == null) {
			throw new FileNotFoundException("File " + path + " was not found inside JAR.");
		}

		// Open output stream and copy data between source file in JAR and the
		// temporary file
		OutputStream os = new FileOutputStream(file);
		try {
			while ((readBytes = is.read(buffer)) != -1) {
				os.write(buffer, 0, readBytes);
			}
		} finally {
			// If read/write fails, close streams safely before throwing an
			// exception
			os.close();
			is.close();
		}

	}

	public static void main(String[] args) {
		NativeUtils nativeUtils = new NativeUtils();
		nativeUtils.loadLibraryFromJar();
	}
}
