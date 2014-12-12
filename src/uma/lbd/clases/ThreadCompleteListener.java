package uma.lbd.clases;

import java.sql.SQLException;

public interface ThreadCompleteListener {
	void sentenciaCompleta(final Notificador thread);
	void sentenciaError(SQLException ex,final Notificador thread);
}
