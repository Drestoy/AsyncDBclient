package uma.lbd.clases;

import java.sql.*;

public class RThread extends Notificador{

		
		private Connection conn;
		private Statement stmt;
		private int columnas=0;
		
		
		public RThread(int x, Connection c, String s,int p, String t){
			ID = x;
			conn = c;
			sentencia = s;
			posicion = p;
			tablas = t;
		}
		
		public void doRun() throws SQLException{
			
			
				while(!conn.isValid(0)){   //Esperando a que la conexión sea válida
					
				}
				
				stmt = conn.createStatement();
				ResultSet rset = stmt.executeQuery(sentencia);
				ResultSetMetaData rsmd = rset.getMetaData();
	        
				columnas = rsmd.getColumnCount();
	        
				resultado = resultado + "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
				resultado = resultado + "<table>\n";
				while (rset.next()) {
					int x=1;
	     	
					resultado = resultado + "\t<row>\n";
					while(x<=columnas){
						resultado = resultado + "\t\t<" + rsmd.getColumnName(x) + ">" + rset.getString(x) + "</" + rsmd.getColumnName(x) + ">\n";
						x++;
					}
					resultado = resultado + "\t</row>\n";
				}
				resultado = resultado + "</table>\n";

				stmt.close();
		}
		

}
