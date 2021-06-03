package sk.stuba.fei.jlab.matlabadapter;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;


public class MatlabAdapterUtils {

	public static String getStackTrace(Throwable aThrowable) {
		if(aThrowable == null) {
			return null;
		}
	    final Writer result = new StringWriter();
	    final PrintWriter printWriter = new PrintWriter(result);
	    aThrowable.printStackTrace(printWriter);
	    return result.toString();
	}
	
}
