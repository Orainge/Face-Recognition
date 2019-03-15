package extern;

public class ExternModel {
	static {
	       System.loadLibrary("ExternModel");
	   }
	public static native void picture(String path); //Picture Recognition Module
	public static native void video(); //Video Recognition Module
}