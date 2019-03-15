package extern;

public class ExternModel {
	static {
	       System.loadLibrary("ExternModel");
	   }
	public static native void picture(String path); //Picture recognition module
	public static native void video(); //Video recognition module
}