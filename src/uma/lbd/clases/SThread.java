package uma.lbd.clases;

import java.sql.*;

public class SThread extends Notificador{
	
	private Connection conn;
	private Statement stmt;
	
	public SThread(int x, Connection c, String s,int p, String t){
		ID = x;
		conn = c;
		sentencia = s;
		posicion = p;
		tablas = t;
	}
	
	public void doRun() throws SQLException{
		
		while(!conn.isValid(0)){  //Esperando a que la conexión sea válida
		
		}
			
			stmt = conn.createStatement();
			
			stmt.execute(sentencia);
			
			stmt.close();

		
	}
	
}
