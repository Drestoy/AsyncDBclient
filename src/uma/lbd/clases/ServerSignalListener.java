package uma.lbd.clases;

import java.sql.SQLException;

public interface ServerSignalListener {
	void señal(final Servidor s, int id, String sentencia, int res);
	void reconectar(String server, String user);
	void error(Servidor s, SQLException ex,String sentencia, int id);
}
