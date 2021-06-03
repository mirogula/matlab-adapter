package sk.stuba.fei.jlab.matlabadapter.matlab;

import java.io.IOException;

import jmatlink.JMatLink;



public class JMatlinkConnector implements MatlabConnector<JMatLink> {
	
	protected JMatLink engine;

	@Override
	public void connect() throws IOException {
		if(isConnected()) {		// if already connected, do nothing and return
			return;
		}
		try {
			engine = new JMatLink();
			engine.engOpen();
			engine.engOutputBuffer();
		} catch (Exception e) {
			engine = null;
			throw new IOException(e);
		}
	}

	@Override
	public void disconnect() throws IOException {
		if(!isConnected()) {	// if not connected, do nothing and return
			return;
		}
		try {
			engine.engClose();
//			engine.kill();
			engine = null;
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public void executeCommand(String command) throws IOException, IllegalStateException {
		if(engine == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			engine.engEvalString(command);
		} catch (Exception e) {
			throw new IOException(e);
		}		
	}

	@Override
	public double getScalar(String variable) throws IOException, IllegalStateException {
		if(engine == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			return engine.engGetScalar(variable);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public String getString(String variable) throws IOException, IllegalStateException {
		if(engine == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			String[] strings = engine.engGetCharArray(variable);
			StringBuilder sb = new StringBuilder();
			for (String string : strings) {
				sb.append(string);
			}
			return sb.toString();
//			return engine.engGetCharArray(variable)[0];
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public String[] getStrings(String variable) throws IOException, IllegalStateException {
		if(engine == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			return engine.engGetCharArray(variable);
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

	@Override
	public Object getArray(String variable, int dimension) throws IOException, IllegalStateException {
		if(engine == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		try {
			if(dimension == 1) {
				return engine.engGetArray(variable)[0];
				
			} else if(dimension == 2) {
				return engine.engGetArray(variable);
			} else {
				throw new IOException("unsupported array dimension");
			}
		} catch (Exception e) {
			throw new IOException(e);
		}
	}
	
	@Override
	public String getConnectionName() {
		return "JMatLink";
	}

	@Override
	public boolean isConnected() {
		if(engine == null) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public String executeCommandWithOutput(String command) throws IOException, IllegalStateException {
		if(engine == null) {
			throw new IllegalStateException("not connected to matlab");
		}
		
		String result;
		try {
			engine.engEvalString(command);
			result = engine.engGetOutputBuffer();
		} catch (Exception e) {
			throw new IOException(e);
		}
		
		return result;
	}

	@Override
	public boolean hasUnderlyingImplementation() {
		return true;
	}

	@Override
	public JMatLink getUnderlyingImplementation() {
		// TODO Auto-generated method stub
		return null;
	}

}
