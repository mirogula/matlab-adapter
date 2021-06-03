/**
 * 
 */
package sk.stuba.fei.jlab.matlabadapter.matlab;

import java.io.IOException;


/**
 * 
 * * Some methods will throw {@link IllegalStateException} if connection to matlab is not established via <code>connect()</code> method.
 * 
 * @author miro
 *
 * @param <T> - Type of underlying implementation. there is no underlying implementation, 
 * use Void as type and return null from method {@link MatlabConnector#getUnderlyingImplementation()}. 
 */
public interface MatlabConnector<T> {
	
	
	/**
	 * Spusti instanciu Matlabu a nadviaze s nou spojenie.
	 * 
	 * @throws IOException - ak nie je mozne nadviazat spojenie. Implementacia 
	 * 			tejto metody musi zabezpecit, ze ak vyhodi {@link IOException} 
	 * 			a zaroven uz bola touto metodou aj spustena instancia Matlabu, 
	 * 			tak tato instancia bude ukoncena.
	 */
	public void connect() throws IOException;
	
	
	/**
	 * Ukonci spojenie s Matlabom a ukonci beh prislusnej instancie Matlabu. 
	 * Po vykonani tejto metody musi byt zarucene ze prislusna instancia 
	 * Matlabu uz nadalej nebezi.
	 * 
	 * @throws IOException - ak sa vyskytla chyba v komunikacii pocas
	 * 			ukoncovania spojenia.
	 */
	public void disconnect() throws IOException;
	
	
	/**
	 * @param command
	 * @throws IOException
	 * @throws IllegalStateException - ak 
	 */
	public void executeCommand(String command) throws IOException;
	
	
	/**
	 * Vykona Matlab prikaz a vrati cely vystup v podobe {@link String}-u, 
	 * tak ako ho Matlab zapisal do command window vratane chybovych hlasok.
	 * 
	 * @param command
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public String executeCommandWithOutput(String command) throws IOException;
	
	
	/**
	 * @param variable
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public double getScalar(String variable) throws IOException;
	
	
	/**
	 * @param variable
	 * @param dimension
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public Object getArray(String variable, int dimension) throws IOException;
	
	
	/**
	 * @param variable
	 * @return
	 * @throws IOException
	 * @throws IllegalStateException
	 */
	public String getString(String variable) throws IOException;
	
	
	
	// TODO remove this method and replace it with 'String getString(String variable)' method
	/**
	 * @param variable
	 * @return
	 * @throws IOException
	 * @throws IllegalArgumentException
	 */
	public String[] getStrings(String variable) throws IOException;
	
	
	/**
	 * Zisti ci je dana instancia {@link MatlabConnector}-a pripojena k Matlabu.
	 * 
	 * @return - {@code true} ak je tato instancia pripojena k matlabu, inak {@code false}.
	 */
	public boolean isConnected();
	
	
	/**
	 * Vrati nazov danej implementacie {@link MatlabConnector}-a.
	 * 
	 * @return
	 */
	public String getConnectionName();
	
	
	/**
	 * @return - {@code true} ak je na nadviazanie spojenia s Matlabom pouzita 
	 * 				kniznica tretej strany, inak vrati {@code false}
	 */
	public boolean hasUnderlyingImplementation();
	
	
	/**
	 * Vrati vnutornu implementaciu, ktora je pouzita na nadviazanie spojenia s Matlabom.
	 * 
	 * @return
	 */
	public T getUnderlyingImplementation();
}
