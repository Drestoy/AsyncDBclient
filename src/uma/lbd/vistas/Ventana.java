package uma.lbd.vistas;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class Ventana extends JFrame{
	
	
    private JPanel menu;
    //Botones
    private JButton CrearS;
    private JButton Cancelar;
    //LABELS
    private JLabel LabelServidor;
    private JLabel LabelUsuario;
    private JLabel LabelPass;
    private JLabel LabelMax;
    //JTEXTAREAS
    private JTextArea TAserver;
    private JTextArea TAusuario;
    private JTextArea TApass;
    private JTextArea TAmaxThreads;
    
    private Controlador cont; 
    
    public Ventana(Controlador c){
    	this.cont = c;
    	try {
            Iniciar();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
       
    public void Iniciar() throws Exception{
    	this.setSize(350, 300);
        this.setTitle("Crear servidor");
        menu = new JPanel();
        menu.setLayout(null);
        menu.setBounds(0, 0, 350, 300);
        
        
        
        CrearS = new JButton("Crear");
		CrearS.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String s,u,p,m;
                s = TAserver.getText();
                u = TAusuario.getText();
                p = TApass.getText();
                m = TAmaxThreads.getText();
            	if(s.isEmpty()){
            		LabelServidor.setForeground(Color.red);
            		LabelUsuario.setForeground(Color.black);
            		LabelPass.setForeground(Color.black);
            		LabelMax.setForeground(Color.black);
            	}else if(u.isEmpty()){
            		LabelServidor.setForeground(Color.black);
            		LabelUsuario.setForeground(Color.red);
            		LabelPass.setForeground(Color.black);
            		LabelMax.setForeground(Color.black);
            	}else if(p.isEmpty()){
            		LabelServidor.setForeground(Color.black);
            		LabelUsuario.setForeground(Color.black);
            		LabelPass.setForeground(Color.red);
            		LabelMax.setForeground(Color.black);
            	}else if(m.isEmpty()){
            		LabelServidor.setForeground(Color.black);
            		LabelUsuario.setForeground(Color.black);
            		LabelPass.setForeground(Color.black);
            		LabelMax.setForeground(Color.red);
            	}else{
            		cont.añadirServidor(s, u, p, Integer.valueOf(m));
            		Ventana.this.dispose();
            	}
            }
        });
		CrearS.setBounds(150,230,70,25);
		
		
		Cancelar = new JButton("Cancelar");
		Cancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	Ventana.this.dispose();
            }
        });
		Cancelar.setBounds(230,230,100,25);
		
		LabelServidor = new JLabel("Servidor: jdbc:oracle:thin:@");
		LabelServidor.setBounds(10,15,170,30);
		
		LabelUsuario = new JLabel("Usuario");
		LabelUsuario.setBounds(10,65,50,30);
		
		LabelPass = new JLabel("Password");
		LabelPass.setBounds(10,115,70,30);
		
		LabelMax = new JLabel("Nº máximo de sentencias: ");
		LabelMax.setBounds(10,175,150,30);
		
		TAserver = new JTextArea();
		TAserver.setBounds(10, 40,320,25);
		
		TAusuario = new JTextArea();
		TAusuario.setBounds(10, 90,320,25);
		
		TApass = new JTextArea();
		TApass.setBounds(10, 140,320,25);
		
		TAmaxThreads = new JTextArea();
		TAmaxThreads.setBounds(165,180,60,25);
		TAmaxThreads.setText("50");
		
		menu.add(CrearS);
		menu.add(Cancelar);
		menu.add(LabelServidor);
		menu.add(LabelUsuario);
		menu.add(LabelPass);
		menu.add(LabelMax);
		menu.add(TAserver);
		menu.add(TAusuario);
		menu.add(TApass);
		menu.add(TAmaxThreads);
		
		
		super.add(menu);
		
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
	 
}
