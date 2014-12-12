package uma.lbd.vistas;

import uma.lbd.vistas.TablaE;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

@SuppressWarnings("serial")
public class Interface extends JFrame{
	private Controlador cont;
	//PANELES
	private JPanel menu;
	private JScrollPane servers;
	private JScrollPane ejecucion;
	private JScrollPane resultados;
	//BOTONES
	private JButton CrearS;
	private JButton Conectar;
	private JButton Estado;
	private JButton Modificar;
	private JButton Cancelar;
	private JButton Obtener;
	private JButton Borrar;
	//LABELS
	private JLabel LabelSentencia;
	//JTEXTAREAS
	private JTextArea JTAsentencias;
	private JTextArea JTAresul;
	//JSCROLLPANES
	private JScrollPane Jsentencias;
	private JScrollPane Jresul;
	//tablas
	private TablaS tablaS;
	private TablaE tablaE;
	private TablaE tablaR;
	
	
	public Interface(){
		try {
            Iniciar();
            this.cont = new Controlador(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
////Funciones para la tabla de servidores
	
	public void InsertarServer(Boolean b, String s, Integer id){
		int x = tablaS.getRowCount();
		tablaS.setValueAt(b, x, 0);
		tablaS.setValueAt(s, x, 1);
		tablaS.setValueAt(id, x, 2);
	}
	
	public void BorrarServer(int row){
		tablaS.removeValueAt(row, 0);
	}
	
	public Object ObtenerServer(int row, int col){
		return tablaS.getValueAt(row, col);
	}
		
	public int RowS(){
		return tablaS.selectedRow();
	}
	
///Funciones para la tabla de Ejecuciones
	
	public void InsertarE(int id, String sentencia, String Server){
		int x = tablaE.getRowCount();
		tablaE.setValueAt(id, x, 0);
		tablaE.setValueAt(sentencia, x, 1);
		tablaE.setValueAt(Server, x, 2);
	}
	
	public void BorrarE(int id, String server){
				
		int row = 0, idTabla;
		String s = "";
		System.out.println("LLEGO POR "+ id);
		while(row<tablaE.getRowCount()){
			idTabla = (int) tablaE.getValueAt(row, 0);
			if(idTabla==id){
				s = (String) tablaE.getValueAt(row, 2);
				if(s.equalsIgnoreCase(server)){
					System.out.println("ENTRO POR "+ id);
					
					tablaE.removeValueAt(row, 0);
					row = tablaE.getRowCount();
				}
			}
			row++;
		}
	}
	
	public Object ObtenerE(int row, int col){
		return tablaE.getValueAt(row, col);
	}
	
	public int RowE(){
		return tablaE.selectedRow();
	}
	
///Funciones para la tabla de Resultados
	
	public void InsertarR(int id, String sentencia, String Server){
		int x = tablaR.getRowCount();
		tablaR.setValueAt(id, x, 0);
		tablaR.setValueAt(sentencia, x, 1);
		tablaR.setValueAt(Server, x, 2);
	}
	
	public void BorrarR(int id, String server){
		int row = 0, idTabla;
		String s = "";
		while(row<tablaR.getRowCount()){
			idTabla = (int) tablaR.getValueAt(row, 0);
			if(idTabla==id){
				s = (String) tablaR.getValueAt(row, 2);
				if(s.equalsIgnoreCase(server)){
					tablaR.removeValueAt(row, 0);
					row = tablaR.getRowCount();
				}
			}
			row++;
		}
	}
	
	public Object ObtenerR(int row, int col){
		return tablaR.getValueAt(row, col);
	}
	
	public int RowR(){
		return tablaR.selectedRow();
	}
	
////Funciones para la vista
	
	public void Escribir(Object s){
		JTAresul.append(s.toString() + "\n");
	}
	
	
	
	private void Iniciar() throws Exception{
		this.setSize(800, 600);
		this.setTitle("Sistema Asincrono - Laboratorio de bases de datos");
		menu = new JPanel();
		menu.setLayout(null);
		menu.setBounds(0, 0, 600, 800);
		
		tablaS = new TablaS();
		
		
		servers = new JScrollPane(tablaS);
		//servers.setLayout(null);
		servers.setBounds(20,20,150,220);
		servers.setBorder(BorderFactory.createTitledBorder("Servidores"));
		servers.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		servers.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		tablaE = new TablaE();
		ejecucion = new JScrollPane(tablaE);
		//ejecucion.setLayout(null);
		ejecucion.setBounds(190,20,280,220);
		ejecucion.setBorder(BorderFactory.createTitledBorder("Ejecucion"));
		ejecucion.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		tablaR = new TablaE();
		resultados = new JScrollPane(tablaR);
		//resultados.setLayout(null);
		resultados.setBounds(490,20,280,220);
		resultados.setBorder(BorderFactory.createTitledBorder("Resultados"));
		resultados.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
//////////PANEL SERVERS
		
		CrearS = new JButton("Crear");
		CrearS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new Ventana(cont);
            }
        });
		CrearS.setBounds(15,250,70,30);
		
		Conectar = new JButton("Conexion");
		Conectar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = RowS();
            	int id = (int) tablaS.getValueAt(row, 2);
            	cont.conexion(id);
            }
        });
		Conectar.setBounds(85,250,90,30);

		
/////////PANEL EJECUCION
		
		Estado = new JButton("Estado");
		Estado.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int row = tablaE.selectedRow();
                int id = (int) tablaE.getValueAt(row, 0);
                String server = (String) tablaE.getValueAt(row, 2);
                int idserver = -1;
                row = 0;
                while(row<tablaS.getRowCount()){
                	if(((String) tablaS.getValueAt(row, 1)).equalsIgnoreCase(server)){
                		idserver = (int) tablaS.getValueAt(row, 2);
                	}
                	row++;
                }
                if(idserver==-1){
                	Escribir("Error: no se ha encontrado la id del servidor que contiene la sentencia seleccionada.");
                }else{
                	cont.estado(idserver, id);
                }  
            }
        });
		Estado.setBounds(200,250,80,30);
		
		Modificar = new JButton("Modificar");
		Modificar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int fila = tablaE.selectedRow();
                int id = (int) tablaE.getValueAt(fila, 0);
                String server = (String) tablaE.getValueAt(fila, 2);
                int idserver = -1;
                int row = 0;
                while(row<tablaS.getRowCount()){
                	System.out.println((String) tablaS.getValueAt(row, 1) + " comparado con " + server);
                	if(((String) tablaS.getValueAt(row, 1)).equalsIgnoreCase(server)){
                		idserver = (int) tablaS.getValueAt(row, 2);
                		row = tablaS.getRowCount();
                	}
                	row++;
                }
                if(idserver==-1){
                	Escribir("Error: no se ha encontrado la id del servidor que contiene la sentencia seleccionada.");
                }else if(JTAsentencias.getText().isEmpty()){
                	Escribir("Error: debe escribir la nueva sentencia modificada en el campo de texto de sentencias.");
                }else{
                	int tmp = cont.modificar(idserver, id, JTAsentencias.getText());
                	if(tmp!=-1){
                		BorrarE(id,server);
                	}
                	
                }
            }
        });
		
		Modificar.setBounds(280,250,90,30);
		
		Cancelar = new JButton("Cancelar");
		Cancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int fila = tablaE.selectedRow();
                int id = (int) tablaE.getValueAt(fila, 0);
                String server = (String) tablaE.getValueAt(fila, 2);
                int idserver = -1;
                int row = 0;
                
                while(row<tablaS.getRowCount()){
                	if(((String) tablaS.getValueAt(row, 1)).equalsIgnoreCase(server)){
                		idserver = (int) tablaS.getValueAt(row, 2);
                		row = tablaS.getRowCount();
                	}
                	row++;
                }
                if(idserver==-1){
                	Escribir("Error: no se ha encontrado la id del servidor que contiene la sentencia seleccionada.");
                }else{
                	int tmp = cont.Cancelar(idserver, id);
                	if(tmp!=-1){
                		BorrarE(id,server);
                	}
                }
            }
        });
		
		Cancelar.setBounds(370,250,90,30);
		
		
		
		
		
		
//////////PANEL RESULTADOS
		
		Obtener = new JButton("Obtener");
		Obtener.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int row = tablaR.selectedRow();
                int id = (int) tablaR.getValueAt(row, 0);
                String server = (String) tablaR.getValueAt(row, 2);
                int idserver = -1;
                row = 0;
                while(row<tablaS.getRowCount()){
                	if(((String) tablaS.getValueAt(row, 1)).equalsIgnoreCase(server)){
                		idserver = (int) tablaS.getValueAt(row, 2);
                	}
                	row++;
                }
                if(idserver==-1){
                	Escribir("Error: no se ha encontrado la id del servidor que contiene la sentencia seleccionada.");
                }else{
                	cont.resultado(idserver, id);
                }
            }
        });
		Obtener.setBounds(525,250,80,30);
		
		Borrar = new JButton("Borrar");
		Borrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	int row = tablaR.selectedRow();
                int id = (int) tablaR.getValueAt(row, 0);
                String server = (String) tablaR.getValueAt(row, 2);
                int idserver = -1;
                row = 0;
                while(row<tablaS.getRowCount()){
                	if(((String) tablaS.getValueAt(row, 1)).equalsIgnoreCase(server)){
                		idserver = (int) tablaS.getValueAt(row, 2);
                	}
                	row++;
                }
                if(idserver==-1){
                	Escribir("Error: no se ha encontrado la id del servidor que contiene la sentencia seleccionada.");
                }else{
                	int tmp = cont.borrarRes(idserver, id);
                	if(tmp!=-1){
                		BorrarR(id,server);
                	}
                }
            }
        });
		Borrar.setBounds(605,250,80,30);
		
		//resultados.add(new JScrollBar());
		
////////PANEL MENU
		
		LabelSentencia = new JLabel("Sentencia:");
		LabelSentencia.setBounds(20,300,70,30);
	
	
	////CUADRO DE TEXTO DE SENTENCIAS	
		JTAsentencias = new JTextArea();
		JTAsentencias.setEnabled(true);
		JTAsentencias.setLineWrap(true);
		JTAsentencias.setBounds(0,0,660,50);
		JTAsentencias.setAutoscrolls(true);
		Action actionEnter = new AbstractAction() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -437347754421478271L;

			@Override
			public void actionPerformed(ActionEvent arg0) {
				String sentencia = JTAsentencias.getText();
			//	int i = (int) (new Date().getTime()/1000);
			//	int id = sentencia.hashCode() + i;
			//	boolean encontrado;
			//	int x;
				
				
				for(int y=0;y<tablaS.getRowCount();y++){
				//	x = 0;
				//	encontrado = false;
					if((Boolean)tablaS.getValueAt(y, 0)){
						/*while((x<tablaE.getRowCount())&&(!encontrado)){
							if((((Integer)tablaE.getValueAt(x,0))==id)&&(((String)tablaE.getValueAt(x,2)).equals(((String)tablaS.getValueAt(y, 1))))){
								encontrado = true;
								Escribir("La sentencia " + sentencia + "ya se está ejecutando en el servidor " + (String)tablaS.getValueAt(y, 1));
							}
							x++;
						}*/
						//if(!encontrado){
							cont.ejecutar((Integer)tablaS.getValueAt(y, 2), sentencia);
						//}
					}
				}
				
				
				JTAsentencias.setText("");
				
			}};
		KeyStroke keyStroke = KeyStroke.getKeyStroke("ENTER");
		InputMap im = JTAsentencias.getInputMap(0);
		JTAsentencias.getActionMap().put(im.get(keyStroke), actionEnter);
		
		
		Jsentencias = new JScrollPane(JTAsentencias);
		Jsentencias.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Jsentencias.setBounds(100,300,660,50);
		
		
	////CUADRO DE TEXTO PARA RESULTADOS Y MENSAJES DE SISTEMA
		JTAresul = new JTextArea();
		JTAresul.setEnabled(true);
		JTAresul.setEditable(false);
		JTAresul.setLineWrap(true);
		JTAresul.setBounds(0,0,740,160);
		JTAresul.setAutoscrolls(true);
		Jresul = new JScrollPane(JTAresul);
		Jresul.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		Jresul.setBounds(20,370,740,160);
		

		
		//Añadir textfields
		menu.add(servers);
		menu.add(ejecucion);
		menu.add(resultados);
		//añadir labels
		menu.add(LabelSentencia);
		//Añadir Botones
		menu.add(CrearS);
		menu.add(Conectar);
		menu.add(Estado);
		menu.add(Obtener);
		menu.add(Borrar);
		menu.add(Modificar);
		menu.add(Cancelar);
		//Añadir areas de texto
		menu.add(Jsentencias);
		menu.add(Jresul);
		
		//añadir el panel
		super.add(menu);
			
		//Centrar la ventana
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	    Dimension frameSize = getSize();
	    if (frameSize.height > screenSize.height) {
	    	 frameSize.height = screenSize.height;
	    }
	    if (frameSize.width > screenSize.width) {
	    	 frameSize.width = screenSize.width;
	    }
	    setLocation((screenSize.width - frameSize.width) / 2,(screenSize.height - frameSize.height) / 2);
		
		setVisible(true);
		setResizable(false);
	}
	
	protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            System.exit(0);
        }
    }
	
}
