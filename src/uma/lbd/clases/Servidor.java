/**
 *
 * @author Daniel Domínguez Restoy
 * @version 0.1
 * @date 2013-06-10
 */
package uma.lbd.clases;

import java.sql.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.Date;



public class Servidor implements ThreadCompleteListener {
	
	private int MAX_HILOS;
	
	private String nombre = "", usuario = "", password = "";
	private Connection conn;
	private int tCreados, idServer;
	
	private Notificador[] hilos;
	private int[] enEjecucion;
	
	private Map<Integer, String> resultados;
	
	public Servidor(String n, String u, String p, int max) throws SQLException {
		
		
		nombre = n;
		usuario = u;
		password = p;
		
		idServer = n.hashCode() + u.hashCode();
		MAX_HILOS = max;
		
		hilos = new Notificador[MAX_HILOS];
		for (int x = 0; x < MAX_HILOS ; x++){
			hilos[x] = null;
		}
		
		enEjecucion = new int[MAX_HILOS];
		for (int x = 0; x < MAX_HILOS ; x++){
			enEjecucion[x] = -1;
		}
		
		tCreados = 0;
		resultados = new HashMap<Integer, String>();
		

		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			
		conn = DriverManager.getConnection("jdbc:oracle:thin:@" + nombre, usuario, password);
	
	}
	
	
	private final Set<ServerSignalListener> listeners = new CopyOnWriteArraySet<ServerSignalListener>();

	public final void addListener(final ServerSignalListener listener) {
		listeners.add(listener);
	}

	
	public final void removeListener(final ServerSignalListener listener) {
		listeners.remove(listener);
	}

	private final void notifyListeners(Servidor s, int id, String sentencia, int res) {
		for (ServerSignalListener listener : listeners) {
			listener.señal(s,id,sentencia,res);
		}
	}
	
	private final void reconectaListeners(String s,String u){
		for(ServerSignalListener listener : listeners){
			listener.reconectar(s,u);
		}
	}
	
	private final void notifyErrorListeners(Servidor s, SQLException ex, String sentencia, int id){
		for (ServerSignalListener listener : listeners) {
			listener.error(s, ex, sentencia, id);
		}
	}
	
	private String Limpiar(String sentencia){  //Funcion para obtener las tablas a las que hace referencia una sentencia
		
		String s = sentencia;
		if(Es(sentencia,"SELECT")){
		 s = sentencia.substring(sentencia.toUpperCase().indexOf("FROM")+5, sentencia.length());
		
		if(s.toUpperCase().contains("JOIN")){
        	String aux = s.substring(s.toUpperCase().indexOf("JOIN")-6,s.toUpperCase().indexOf("JOIN")-1);
        
        	if((aux.equalsIgnoreCase("INNER"))||(aux.equalsIgnoreCase("OUTER"))||(aux.equalsIgnoreCase("LEFT"))||(aux.equalsIgnoreCase("RIGHT"))){
        		s = s.substring(0,s.indexOf("JOIN")-7).concat(",").concat(s.substring(s.indexOf("JOIN")+4,s.length()));
        		
        	}else{
        		s = s.substring(0,s.indexOf("JOIN")-1).concat(",").concat(s.substring(s.indexOf("JOIN")+4,s.length()));
        	}
        }
    	
        int x = 0;
        while(x<s.length()){
        	if(s.charAt(x)==','){
        		x++;
        		while((x<s.length())&&(s.charAt(x)==' ')){
        			x++;
        		}
        	}else if(s.charAt(x)==' '){
        		while((x<s.length())&&(s.charAt(x)==' ')){
        			x++;
        		}
        		if(s.charAt(x)!=','){
        			s = s.substring(0,x);
        		}
        	}else if(s.charAt(x)==';'){
        		s = s.substring(0,x);
        	}
        	x++;
        }
		}else if(Es(sentencia,"INSERT")){
			s = s.substring(s.toUpperCase().indexOf("INTO")+5,s.toUpperCase().indexOf("VALUES")-1);
		}else if(Es(sentencia,"UPDATE")){
			s = s.substring(s.toUpperCase().indexOf("UPDATE")+7,s.toUpperCase().indexOf("SET")-1);
		}else if(Es(sentencia,"DELETE")){
			s = s.substring(s.toUpperCase().indexOf("FROM")+5,s.toUpperCase().indexOf("WHERE")-1);
		}else if(Es(sentencia,"DROP TABLE")){
			s = s.substring(s.toUpperCase().indexOf("TABLE")+6,s.length());
		}
		s = s.replaceAll(";", "");
		s = s.replaceAll(" ", "");
        return s;
        
	}
	
	private int EjecutarS(String sentencia, int pos) throws SQLException{  // Funcion interna de ejecución de sentencias
		int x=0;
		String s = "";
		int i = (int) (new Date().getTime()/100);
		int id = sentencia.hashCode() + i + idServer;
		boolean conexion;
		if(!(conexion = conn.isValid(0))){
			ReconectarInterno(1);
		}
		if(BuscarPosicion(id)==-1){
			if(tCreados<MAX_HILOS){  //Comprobacion de que queda espacio para ejecutar sentencias.
				
				if(pos==-1){
					x = ObtenerPosicion(); //pos == -1, la ejecucion es pedida por el usuario y hay que buscar una posicion válida.
				}else{
					x = pos;  //La posicion nos la ha dado el sistema.
				}
			 
				if((x!=-1)&&(conexion)){  //Si la posicion y la conexion son validas, se ejecuta la sentencia
					if((Es(sentencia,"COMMIT"))||(Es(sentencia,"ROLLBACK"))||(Es(sentencia,"CREATE TABLE"))){  //Si es Commit, rollback o create table, no hay comprobacion y se ejecuta directamente
						hilos[x] = new SThread(id,conn,sentencia,x, "");
						tCreados++;
						hilos[x].addListener(this);
						hilos[x].start();
						enEjecucion[x]=1;
					}else{ 
						if(tCreados < (MAX_HILOS-2)){  //Dejo 2 espacios libres para Commit, rollback o create tabla, que pueden iniciar ejecuciones de threads que esperan.
							Statement stmt;
							stmt = conn.createStatement();
							ResultSet rset = stmt.executeQuery("select table_name from user_tables");
					        
							s = Limpiar(sentencia).toUpperCase();	// Obtenemos las tablas que va a usar la sentecia, lo almacenamos en s...
							s = " " + s.replaceAll(",", " , ") + " ";
							while (rset.next()) {	// ... y borramos de s las que existen en el servidor
								if(s.contains(" " + rset.getString(1).toUpperCase() + " ")){
									s = s.replaceAll(" " + rset.getString(1).toUpperCase()+ " ", "");
								}
							}
							s = s.replaceAll(" USER_TABLES ", "");
							s = s.replaceAll(" ", "");
							stmt.close();
					    
							if(Es(sentencia,"select")){  // Se crea el thread
								hilos[x] = new RThread(id,conn,sentencia,x,s);
							}else{
								hilos[x] = new SThread(id,conn,sentencia,x,s);
							}
							
							enEjecucion[x]=0;  // Thread a la espera de ser activado
							tCreados++;
							hilos[x].addListener(this);
							
							if((s.replaceAll(",", "").isEmpty())){  //Si s está vacio, todas las tablas existen y la sentencia se puede ejecutar
								hilos[x].start();
								enEjecucion[x]=1;
							}else{
								System.out.println("Sentencia (" + sentencia + ") esperando a que se creen las tablas: " + s);
							}
						}else{
							System.out.println("Numero máximo de hilos alcanzado. Por favor, espere a que se libere la cola de ejecución y vuelva a intentarlo.");
							id = -1;
						}
					}	
				}
				else if(!conexion){ //Si ha fallado la conexion y hay hueco, se crea el thread pero no se activa.
					if(tCreados < (MAX_HILOS-2)){
						if(Es(sentencia,"select")){
							hilos[x] = new RThread(id,conn,sentencia,x,s);
						}else{
							hilos[x] = new SThread(id,conn,sentencia,x,s);
						}
						enEjecucion[x]=0;
						System.out.println("Thread " + sentencia + " creado en posicion " + x + ". Esperando reconexion al servidor.");
					}else{
						System.out.println("Numero máximo de hilos alcanzado. Por favor, espere a que se libere la cola de ejecución y vuelva a intentarlo.");
						id = -1;
					}
				}
			}else{
				System.out.println("Numero máximo de hilos alcanzado. Por favor, espere a que se libere la cola de ejecución y vuelva a intentarlo.");
				id = -1;
			}
		}else{
			System.out.println("La id ya existe. Intentelo de nuevo.");
			id = -1;
		}
		
		
		return id;
	}
	
	private int ObtenerPosicion(){  //Busca una posición para almacenar el thread que se va a ejecutar
		
		int x = 0;
		while((x<MAX_HILOS)&&(hilos[x]!=null)&&(enEjecucion[x]!=-1)){
			x++;	
		}
		if(x==MAX_HILOS){
			x = -1;
		}
		return x;
	}
	
	
	private int BuscarPosicion(int id){	 //Devuelve la posicion en la que se encuentra la id especificada, o -1 si no la encuentra
		int x = 0;
		while((x<MAX_HILOS)&&(hilos[x]!=null)&&(hilos[x].id()!=id)){
			x++;
		}
		if(hilos[x]==null){
			return -1;
		}else if(hilos[x].id()==id){
			return x;
		}else{
			return -1;
		}
	}

	
	private boolean Es(String s, String comando){ //Verdadero si la sentencia es el comando especificado
		if(s.length()<10){
			return s.toUpperCase().contains(comando.toUpperCase());
		}else{
			return s.substring(0, comando.length()).toUpperCase().contains(comando.toUpperCase());
		}
		
	}
	
	
	
	private boolean EsVacio(){  //Comprueba que el array de hilos esta vacío
		boolean res=true;
		for(int x=0;x<MAX_HILOS;x++){
			if((hilos[x]!=null)){
				res=false;
			}
		}
		return res;
	}
	
	/**
	 * Ejecuta la sentencia indicada.
	 * @param sentencia cadena a ejecutar
	 * @return Devuelve la id de la sentencia creada o -1 si no se ha creado sentencia.
	 * */
	public int Ejecutar(String sentencia) throws SQLException{  
		return EjecutarS(sentencia,-1);
	}
	
	
	
	
	/**
	 * Cancela la sentencia de id indicada.
	 * @param id id de la sentencia a cancelar
	 * @return Devuelve la posición de la sentencia cancelada o -1 si no la ha encontrado.
	 * */
	@SuppressWarnings("deprecation")
	public int Cancelar(int id){	
		int x = BuscarPosicion(id);
		if(x!=-1){
			hilos[x].stop();
			enEjecucion[x]=-1;
			tCreados--;
		}else{
			System.out.println("No se ha encontrado la id especificada en ejecución.");
		}
		return x;
	}
	
	/**
	 * Altera una sentencia ya creada si no ha terminado su ejecucion.
	 * @param id id de la sentencia a alterar
	 * @param sentencia nueva sentencia que se cambiará por la sentencia a alterar
	 * @return Devuelve la nueva id para la sentencia modificada, o -1 si ha habido un fallo.
	 * */
	public int Alterar(int id, String sentencia) throws SQLException{  
		int x = Cancelar(id); 
		if(x!=-1){
			x = EjecutarS(sentencia,x);
		}
		return x;
	}
	
	/**
	 * Devuelve el estado en el que se encuentra la sentencia especificada.
	 * @param id id de la sentencia objetivo
	 * @return Devuelve el estado en formato cadena de texto
	 * */
	public String Estado(int id){	
		int x = BuscarPosicion(id);
		String res = Resultado(id);
		if(x!=-1){  // si se ha encontrado la id, el hilo se encuentra en ejecucion o a la espera
			String tablas = hilos[x].tablas();
			if(enEjecucion[x]==0){
				res = "Sentencia aun sin comenzar. Esperando a que se creen las siguientes tablas: " + tablas;
			}else if(enEjecucion[x]==1){
				res = "Sentencia en ejecución.";
			}else if(enEjecucion[x]==-1){
				res = "Sentencia terminada.";
			}
		}else if(!res.isEmpty()){
			res = "Sentencia terminada y resultado listo para ser visualizado.";
		}else{
			res = "No se ha encontrado la sentencia de id " + id + " en la cola de ejecución ni hay resultados disponibles con esa id.";
		}
		return res;
	}
	/**
	 * Devuelve el resultado de la sentencia especificada, siempre que la sentencia haya concluido su ejecución y devuelva un resultado.
	 * @param id id de la sentencia objetivo
	 * @return Devuelve el resultado de la sentencia especificada
	 * 
	 * */
	public String Resultado(int id){	
		Iterator<Entry<Integer, String>> it = resultados.entrySet().iterator();
		String res = "";
		while (it.hasNext()) {
			Entry<Integer, String> e = it.next();
			if(id==e.getKey()){
				res = e.getValue();
			}
			
		}
		
		return res;
	}
	
	/**
	 * Función de borrado de elementos en la estructura de resultados del server
	 * @param id id del resultado a borrar
	 * @return Devuelve 1 si el borrado ha sido realizado, -1 en cualquier otro caso
	 * */
	public int BorrarResultado(int id){
		Iterator<Entry<Integer, String>> it = resultados.entrySet().iterator();
		int res = -1;
		while (it.hasNext()) {
			Entry<Integer, String> e = it.next();
			if(id==e.getKey()){
				resultados.remove(e);
				res = 1;
			}
		}
		return res;
	}
	/**
	 * Desconecta del servidor, esperando a que terminen las sentencias.
	 * 
	 * */
	public void Desconectar() throws SQLException {  
		while(!EsVacio()){
			/* Esperando */
		}
		conn.close();
	}
	/**
	 * Desconecta del servidor, cancelando las sentencias que aun existan.
	 * 
	 * */
	public void DesconectarBruto() throws SQLException { 
		for(int x=0;x<MAX_HILOS;x++){
			if((hilos[x]!=null)&&(hilos[x].isAlive())){
				this.Cancelar(x);
			}
		}
		conn.close();   
	}
	
	/**
	 * Función que hace hasta 5 intentos para conectar al servidor
	 * @return Devuelve 1 si se ha conectado, -1 en caso contrario.
	 * */
	public int Reconectar() throws SQLException{
		return ReconectarInterno(0);
	}
	
	private int ReconectarInterno(int i) throws SQLException{
		int x = 0, res = 1;
		
		while((!conn.isValid(0))&&(x<5)){
			System.out.println("Reconectando a " + nombre + ".Intento: " + (x+1)); 
			x++;
			conn = DriverManager.getConnection("jdbc:oracle:thin:@" + nombre, usuario, password);
			System.out.println("Conectado a " + nombre);	
		}
		if(x>=5){
			res = -1;
			reconectaListeners(this.Nombre(),this.Usuario());
		}
		if(i==0){
			sentenciaCompleta(null);
		}
		return res;
		
	}
	
	/**
	 * Función para obtener el nombre del servidor
	 * @return Devuelve el nombre del servidor
	 * */
	public String Nombre(){
		return nombre;
	}
	/**
	 * Función para obtener el usuario de esta conexión
	 * @return Devuelve el nombre del usuario
	 * */
	public String Usuario(){
		return usuario;
	}
	/**
	 * Función para obtener la id del servidor
	 * @return Devuelve la id del servidor
	 * */
	public int id(){
		return idServer;
	}
	
	/**
	 * Función para saber el estado de la conexión del servidor.
	 * @return true si está conectado, false si no lo está.
	 * */
	public Boolean Conectado() throws SQLException{
		return conn.isValid(0);
	}
	

	/**
	 * Listener para actuar cuando una sentencia termine. No debe usarse fuera del paquete.
	 * 
	 * 
	 * */
	public void sentenciaCompleta(final Notificador thread){
		
		int id = thread.id();
		String sentencia = thread.sentencia();
		int resultado = 0;
		if(thread!=null){
			if(!thread.resultado().isEmpty()){
				resultados.put(thread.id(),thread.resultado());
				resultado = 1;
			}
			
			enEjecucion[thread.posicion()] = -1;
			hilos[thread.posicion()]=null;
			tCreados--;
		}
		if((tCreados>0)&&(thread!=null)&&(Es(thread.sentencia(),"CREATE TABLE"))){
			notifyListeners(this,id, sentencia, resultado);
			try{
				if(Conectado()){
					
					for(int x=0;x<MAX_HILOS;x++){
						if((hilos[x]!=null)&&(enEjecucion[x]==0)){
							
							Statement stmt;
							stmt = conn.createStatement();
							ResultSet rset = stmt.executeQuery("select table_name from user_tables");

							String s = Limpiar(hilos[x].sentencia()).toUpperCase();
							s = " " + s.replaceAll(",", " , ") + " ";
							while (rset.next()) {
								if(s.contains(" " + rset.getString(1).toUpperCase() + " ")){
									s = s.replaceAll(" " + rset.getString(1).toUpperCase()+" ", "");
								}
							}
							s= s.replaceAll(" USER_TABLES ", "");
							s = s.replaceAll(" ", "");
						    stmt.close();
						    if(s.replaceAll(",","").isEmpty()){
								enEjecucion[x]=1;
								hilos[x].start();
							}else{
								System.out.println(s);
							}
						}
					}
				}else{
					System.out.println("No se ha podido reconectar. Vuelva a reconectar para continuar.");
				}
				
			}catch(SQLException ex){
				System.out.println("Error " + ex.getErrorCode() + ": " + ex.getMessage() );
			}
		}else{
			notifyListeners(this,id, sentencia, resultado);
		}
		
	}


	@Override
	public void sentenciaError(SQLException ex,final Notificador thread){
		notifyErrorListeners(this,ex,thread.sentencia(),thread.id());
	}
}


