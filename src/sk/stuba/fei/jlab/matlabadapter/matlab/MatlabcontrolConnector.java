package sk.stuba.fei.jlab.matlabadapter.matlab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import sk.stuba.fei.jlab.matlabadapter.MatlabAdapterUtils;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;
import matlabcontrol.MatlabProxy;
import matlabcontrol.MatlabProxyFactory;
import matlabcontrol.extensions.MatlabNumericArray;
import matlabcontrol.extensions.MatlabTypeConverter;


/**
 * @author Miroslav Gula
 *
 */
public class MatlabcontrolConnector implements MatlabConnector<MatlabProxy> {

	protected MatlabProxy proxy;
	
	protected File matlabOutputDiaryFile = new File("matlabControlOutputDiary.txt");
	protected Charset matlabOutputDiaryCharset = Charset.defaultCharset();
	
	
	public File getMatlabOutputDiaryFile() {
		return matlabOutputDiaryFile;
	}

	public void setMatlabOutputDiaryFile(File matlabOutputDiaryFile) {
		this.matlabOutputDiaryFile = matlabOutputDiaryFile;
	}

	public Charset getMatlabOutputDiaryCharset() {
		return matlabOutputDiaryCharset;
	}

	public void setMatlabOutputDiaryCharset(Charset matlabOutputDiaryCharset) {
		this.matlabOutputDiaryCharset = matlabOutputDiaryCharset;
	}
	
	
	@Override
	public void connect() throws IOException {
		if(isConnected()) {		// if already connected, do nothing and return
			return;
		}
		MatlabProxyFactory factory = new MatlabProxyFactory();
		try {
			proxy = factory.getProxy();
		} catch (MatlabConnectionException e) {
			throw new IOException(e);
		}

	}

	@Override
	public void disconnect() throws IOException {
		if(!isConnected()) {		// if not connected, do nothing and return
			return;
		}
		try {
			proxy.exit();
			proxy = null;
		} catch (MatlabInvocationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public void executeCommand(String command) throws IOException, IllegalStateException {
		if(proxy == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			proxy.eval(command);
		} catch (MatlabInvocationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public double getScalar(String variable) throws IOException, IllegalStateException {
		if(proxy == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			return ((double[])proxy.getVariable(variable))[0];
		} catch (MatlabInvocationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public Object getArray(String variable, int dimension) throws IOException, IllegalStateException {
		if(proxy == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			MatlabNumericArray matlabArray = new MatlabTypeConverter(proxy).getNumericArray(variable);
			switch (dimension) {
				case 1:
					return (double[])proxy.getVariable(variable);
				case 2:
					return matlabArray.getRealArray2D();
				case 3:
					return matlabArray.getRealArray3D();
				case 4:
					return matlabArray.getRealArray4D();
				default:
					throw new IOException("unsupported array dimension");
			}
		} catch (Exception e) {
			throw new IOException(e);
		}	
	}

	@Override
	public String getString(String variable) throws IOException, IllegalStateException {
		if(proxy == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			return (String)proxy.getVariable(variable);
		} catch (MatlabInvocationException e) {
			throw new IOException(e);
		}
	}

	@Override
	public String[] getStrings(String variable) throws IOException, IllegalStateException {
		if(proxy == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			return (String[])proxy.getVariable(variable);
		} catch (MatlabInvocationException e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public String getConnectionName() {
		return "MatlabControl";
	}

	@Override
	public boolean isConnected() {
		if(proxy == null) {
			return false;
		} else {
			return proxy.isConnected();
		}
	}

	@Override
	public String executeCommandWithOutput(String command) throws IOException, IllegalStateException {
		if(proxy == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		
		boolean errorOutput = false;
		executeCommand("diary('"+matlabOutputDiaryFile.getAbsolutePath()+"')");
		try {
			executeCommand(command);
		} catch (Exception e) {
			String stackTrace = MatlabAdapterUtils.getStackTrace(e);
			// if exception wasn't coused by internal MATLAB exception, rethrow it
			// (because if it was caused by internal MATLAB exception we want that excepton to be present in diary file)
			if(!stackTrace.contains("Method did not return properly because of an internal MATLAB exception")) {
				// exception is already wrapped in IOException because we use executeCommand method
				throw e;
			}
			errorOutput = true;
		}
		executeCommand("diary off");
		
		String result = readFile(matlabOutputDiaryFile, matlabOutputDiaryCharset);
		
		matlabOutputDiaryFile.delete();
		
		if(errorOutput) {
			// if output contains error message, we must filter out some unwanted characters
			result = result.replaceAll("\\{\b\\?\\?\\?", "???");
			result = result.replaceAll("}\b", "");
		}
		
		return result;
	}
	
	protected String readFile(File file, Charset charset) throws IOException {
		try (BufferedReader br = 
				new BufferedReader(new InputStreamReader(new FileInputStream(file), charset))) {
			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[8192];
			int read;
			while ((read = br.read(buffer, 0, buffer.length)) > 0) {
				sb.append(buffer, 0, read);
			}
			return sb.toString();
		}
	}

	@Override
	public boolean hasUnderlyingImplementation() {
		return true;
	}

	@Override
	public MatlabProxy getUnderlyingImplementation() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
