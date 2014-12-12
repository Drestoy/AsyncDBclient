package uma.lbd.clases;

import java.sql.SQLException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public abstract class Notificador extends Thread {
	
	protected int ID, posicion;
	protected String  sentencia = "", resultado = "", tablas = "";
	
	private final Set<ThreadCompleteListener> listeners = new CopyOnWriteArraySet<ThreadCompleteListener>();

	public final void addListener(final ThreadCompleteListener listener) {
		listeners.add(listener);
	}

	
	public final void removeListener(final ThreadCompleteListener listener) {
		listeners.remove(listener);
	}

	private final void notifyListeners() {
		for (ThreadCompleteListener listener : listeners) {
			listener.sentenciaCompleta(this);
		}
	}
	
	private final void notifyErrorListeners(SQLException ex) {
		for (ThreadCompleteListener listener : listeners) {
			listener.sentenciaError(ex, this);
		}
	}
	
	public final String tablas(){
		return tablas;
	}
	
	public final int posicion(){
		return posicion;
	}
	
	public final String resultado(){
		return resultado;
	}
	
	public final void run(){
		try {
			doRun();
		} catch(SQLException ex){
			resultado = "";
			notifyErrorListeners(ex);
		}finally {
			notifyListeners();
		}
	}
	
	public final String sentencia(){
		return sentencia;
	}
	
	public final int id(){
		return ID;
	}
	
	public abstract void doRun() throws SQLException;
	
}
