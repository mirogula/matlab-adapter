package sk.stuba.fei.jlab.matlabadapter.matlab;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MockupConnector implements MatlabConnector<Void> {

	private static final Logger LOGGER = LoggerFactory.getLogger(MockupConnector.class);
	
	@Override
	public void connect() throws IOException {
		LOGGER.info("Connecting");
	}

	@Override
	public void disconnect() throws IOException {
		LOGGER.info("Disconnecting");		
	}

	@Override
	public void executeCommand(String command) throws IOException {
		LOGGER.info("Executing command: "+command);
	}

	@Override
	public String executeCommandWithOutput(String command) throws IOException {
		LOGGER.info("Executing command: "+command);
		return command;
	}
	
	@Override
	public double getScalar(String variable) throws IOException {
		double returnVar = Math.random();
		LOGGER.info("Returning variable: "+variable+", and it's value is: "+returnVar);
		return returnVar;
	}

	@Override
	public Object getArray(String variable, int dimension) throws IOException {
		double[][] returnVar = new double[][] {{Math.random(), Math.random(), Math.random()}};
		LOGGER.info("Returning variable: "+variable+", and it's value is: "+returnVar);
		return returnVar;
	}

	@Override
	public String getString(String variable) throws IOException {
		String returnVar = "test";
		LOGGER.info("Returning variable: "+variable+", and it's value is: "+returnVar);
		return returnVar;
	}

	@Override
	public String[] getStrings(String variable) throws IOException {
		String[] returnVar = new String[] {"test1", "test2", "test3"};
		LOGGER.info("Returning variable: "+variable+", and it's value is: "+returnVar);
		return returnVar;
	}

	@Override
	public String getConnectionName() {
		LOGGER.info("Returning value: MockupConnection");
		return "MockupConnection";
	}

	@Override
	public boolean isConnected() {
		return false;
	}

	@Override
	public boolean hasUnderlyingImplementation() {
		return false;
	}

	@Override
	public Void getUnderlyingImplementation() {
		return null;
	}

}
