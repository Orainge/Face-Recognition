package extern;

public class ExternModel {
	static {
	       System.loadLibrary("ExternModel");
	   }
	public static native void picture(String path); //图片识别模块
	public static native void video(); //视频识别模块
}