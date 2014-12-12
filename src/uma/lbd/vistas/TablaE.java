package uma.lbd.vistas;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;



@SuppressWarnings("serial")
public class TablaE extends JPanel {
    
    private MyTableModel tabla;
    private JTable table;
    
    public TablaE() {
        super(new GridLayout(1,0));
 
        tabla = new MyTableModel();
        table = new JTable(tabla);
        table.setPreferredScrollableViewportSize(new Dimension(220, 70));
        table.setFillsViewportHeight(true);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(115);
        table.getColumnModel().getColumn(2).setPreferredWidth(115);
 
        JScrollPane scrollPane = new JScrollPane(table);
 
        add(scrollPane);
    }
    
    public int getColumnCount() {
        return tabla.getColumnCount();
    }

    public int getRowCount() {
        return tabla.getRowCount();
    }

    public String getColumnName(int col) {
        return tabla.getColumnName(col);
    }

    public Object getValueAt(int row, int col) {
        return tabla.getValueAt(row, col);
    }
    
    public void setValueAt(Object value, int row, int col) {
        tabla.setValueAt(value, row, col);
    }
    
    public void removeValue(int id, String server){
    	tabla.removeValue(id, server);
    }
    
    public void removeValueAt(int row, int col){
    	tabla.removeValueAt(row, col);
    }
    
    public int selectedRow(){
    	return table.getSelectedRow();
    }
    
        
    ////////////////////////////////////////////////////////////////////CLASE INTERNA
    
    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"ID",
                                        "Sentencia","Servidor"};

        private Map<Integer,String> dataSentencia = new ConcurrentSkipListMap<Integer,String>();
        private Map<Integer,String> dataServer = new ConcurrentSkipListMap<Integer,String>();

        private Map<Integer,Integer> dataid = new ConcurrentSkipListMap<Integer,Integer>();

        
        public int getColumnCount() {
            return columnNames.length;
        }
 
        public int getRowCount() {
        	if(dataid!=null){
            	return dataid.size();
        	}else{
        		return 0;
        	}
        	
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
        	
        	if(col==0){
        		int x = 0;
        		Iterator<Entry<Integer, Integer>> it = dataid.entrySet().iterator();
        		while(it.hasNext()){
        			Entry<Integer, Integer> e1 = it.next();
        			if(row==e1.getKey()){
        				x = e1.getValue();
        			}
        		}
        		return x;
        	}else if(col==1){
        		String s = "";
        		Iterator<Entry<Integer, String>> it = dataSentencia.entrySet().iterator();
        		while(it.hasNext()){
        			Entry<Integer, String> e1 = it.next();
        			if(row==e1.getKey()){
        				s = e1.getValue();
        			}
        		}
        		return s;
        	}else{
        		String s = "";
        		Iterator<Entry<Integer, String>> it = dataServer.entrySet().iterator();
        		while(it.hasNext()){
        			Entry<Integer, String> e1 = it.next();
        			if(row==e1.getKey()){
        				s = e1.getValue();
        			}
        		}
        		return s;
        	}
        	
        }
        
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
 
        
        public boolean isCellEditable(int row, int col) {
            return false;
        }
 
       
        public void setValueAt(Object value, int row,int col) {
        	
        	
        	if(col==0){
        		dataid.put(row, (Integer)value);
        	}else if(col==1){
        		dataSentencia.put(row, (String)value);
        	}else{
        		dataServer.put(row, (String)value);
        	}
        	
        	
        	fireTableDataChanged();
        	
        }
        
        public void removeValue(int id, String server){
        		
        		int x = 0;
        		boolean cambio = true;
        		Iterator<Entry<Integer, Integer>> it = dataid.entrySet().iterator();
        		Iterator<Entry<Integer, String>> it3 = dataServer.entrySet().iterator();
        		while((it.hasNext())&&(it3.hasNext())){
        			Entry<Integer, Integer> e1 = it.next();
        			Entry<Integer, String> e3 = it3.next();
        			if(cambio){
        				
        			}else if((id==e1.getValue())&&(server.equals(e3.getValue()))){
        				dataid.remove(x);
        				dataSentencia.remove(x);
        				dataServer.remove(x);
        				cambio = true;
        			}
        			
        			x++;
        		}
        		fireTableDataChanged();
        }
        
        public void removeValueAt(int row, int col) {
        	
        	
        		dataSentencia.remove(row);
            	dataServer.remove(row);
            	dataid.remove(row);
            	
            	Map<Integer,Integer> tmp = new ConcurrentSkipListMap<Integer,Integer>(); //id
            	Map<Integer,String> tmp2 = new ConcurrentSkipListMap<Integer,String>(); //sentencia
            	Map<Integer,String> tmp3 = new ConcurrentSkipListMap<Integer,String>();  //server
            	
            	Iterator<Entry<Integer, String>> it = dataSentencia.entrySet().iterator();
            	Iterator<Entry<Integer, String>> it2 = dataServer.entrySet().iterator();
            	Iterator<Entry<Integer, Integer>> it3 = dataid.entrySet().iterator();
            	
            	while(it.hasNext()){
            		Entry<Integer,String> e1 = it.next();
            		if(e1.getKey()>row){
            			tmp2.put(e1.getKey()-1, e1.getValue());
            		}else{
            			tmp2.put(e1.getKey(), e1.getValue());
            		}
            		
            	}
            	dataSentencia = tmp2;
            	
            	while(it2.hasNext()){
            		Entry<Integer,String> e2 = it2.next();
            		if(e2.getKey()>row){
            			tmp3.put(e2.getKey()-1, e2.getValue());
            		}else{
            			tmp3.put(e2.getKey(), e2.getValue());
            		}
            		
            	}
            	dataServer = tmp3;
            	
            	while(it3.hasNext()){
            		Entry<Integer, Integer> e3 = it3.next();
            		if(e3.getKey()>row){
            			tmp.put(e3.getKey()-1, e3.getValue());
            		}else{
            			tmp.put(e3.getKey(), e3.getValue());
            		}
            		
            	}
            	dataid = tmp;
            	
            	
            	fireTableDataChanged();
        }

    }
    
}
