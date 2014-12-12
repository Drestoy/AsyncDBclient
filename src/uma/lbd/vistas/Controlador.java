package uma.lbd.vistas;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import uma.lbd.clases.ServerSignalListener;
import uma.lbd.clases.Servidor;

public class Controlador implements ServerSignalListener{
	private Interface vista;
	
	Map<Integer, Servidor> servidores;
	
	Map<Integer,String> ejecucion;
	
	public Controlador(){
		
		servidores = new HashMap<Integer, Servidor>();
	}
	
	public Controlador(Interface v){
		servidores = new HashMap<Integer, Servidor>();
		this.vista = v;
		
	}
	
	public void añadirServidor(String server, String user, String pass,int max){
		try{
			vista.Escribir("Conectando a " + server);
			Servidor s = new Servidor(server,user,pass,max);
			s.addListener(this);
			servidores.put(s.id(), s);
			vista.InsertarServer(true, server + "("+user+")",s.id());
			vista.Escribir("Conectado!");
		}catch(SQLException ex){
			vista.Escribir("Error al añadir servidor: " + ex.getErrorCode() + " - " + ex.getMessage() );
		}
	}
	
	
	public void conexion(int id){
		Servidor s = obtenerServer(id);
		try{
			if(!s.Conectado()){
				vista.Escribir("Reconectando a " + s.Nombre());
				s.Reconectar();
				vista.Escribir("Reconexion a " + s.Nombre() + " realizada.");
			}else{
				s.DesconectarBruto();
				vista.Escribir("Desconectado de " + s.Nombre());
			}
		}catch(SQLException ex){
			vista.Escribir("Error en la conexion al servidor: " + ex.getErrorCode() + " - " + ex.getMessage()  );
		}
		
	}

	public void estado(int idServer, int idSentencia){
		Servidor s = obtenerServer(idServer);
		vista.Escribir(s.Estado(idSentencia));
	}
	
	@SuppressWarnings("finally")
	public int modificar(int idServer, int idSentencia, String nuevaSentencia){
		Servidor s = obtenerServer(idServer);
		int x = 1;
		try{
			int id = s.Alterar(idSentencia, nuevaSentencia);
			if(id==-1){
				vista.Escribir("Error al intentar modificar la sentencia. Vuelva a intentarlo.");
				x = -1;
			}else{
				vista.InsertarE(id, nuevaSentencia, s.Nombre()+"("+s.Usuario()+")");
			}
		}catch(SQLException ex){
			vista.Escribir("Error al modificar sentencia: " + ex.getErrorCode() + " - " + ex.getMessage() );
		}finally{
			return x;
		}
		
	}
	
	public int Cancelar(int idServer, int idSentencia){
		Servidor s = obtenerServer(idServer);
		int x = s.Cancelar(idSentencia); 
		if(x==-1){
			vista.Escribir("Error al intentar cancelar la sentencia. Vuelva a intentarlo.");
		}
		return x;
	}
	
	public void resultado(int idServer, int idSentencia){
		Servidor s = obtenerServer(idServer);
		vista.Escribir(s.Resultado(idSentencia));
	}
	
	public int borrarRes(int idServer, int idSentencia){
		Servidor s = obtenerServer(idServer);
		if(s.BorrarResultado(idSentencia)==-1){
			vista.Escribir("Error al borrar resultado: el resultado no se ha encontrado.");
			return -1;
		}else{
			return 1;
		}
	}
	
	public void ejecutar(int idServer, String sentencia){
		Servidor s = obtenerServer(idServer);
		int id=0;
		try{
			id = s.Ejecutar(sentencia);
			if(id==-1){
				vista.Escribir("Error: la sentencia no ha sido creada.");
			}else{
				vista.InsertarE(id, sentencia, s.Nombre()+"("+s.Usuario()+")");
			}
		}catch(SQLException ex){
			vista.Escribir("Error al ejecutar sentencia: " + ex.getErrorCode() + " - " + ex.getMessage() );
		}	
	}
	
	public Servidor obtenerServer(int id){
		Iterator<Entry<Integer, Servidor>> it = servidores.entrySet().iterator();
		Servidor res = null;
		while (it.hasNext()) {
			Entry<Integer, Servidor> e = it.next();
			if(id==e.getKey()){
				res = e.getValue();
			}	
		}
		return res;
	}

	@Override
	public void señal(Servidor s, int id, String sentencia, int res) {
		vista.BorrarE(id, s.Nombre()+"("+s.Usuario()+")");
		if(res==1){
			vista.InsertarR(id, sentencia, s.Nombre()+"("+s.Usuario()+")");
		}
		
	}
	
	public Boolean conectado(int id){
		Servidor s = obtenerServer(id);
		boolean b = true;
		try {
			b = s.Conectado();
		} catch (SQLException e) {
			vista.Escribir("Error revisando conexión del servidor seleccionado.");
		}
		return b;
	}
	
	public void reconectar(final String server, String user){
		vista.Escribir("El usuario " + user + " ha perdido la conexión con "+ server + ". Pulse el boton de conexión para intentarlo de nuevo.");
	}

	@Override
	public void error(Servidor s, SQLException ex, String sentencia, int id) {
		vista.Escribir("Error en la ejecucion de la sentencia " + sentencia + " en el servidor " + s.Nombre());
	}
}
