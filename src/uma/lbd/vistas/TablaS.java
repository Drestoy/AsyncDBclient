package uma.lbd.vistas;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;


@SuppressWarnings("serial")
public class TablaS extends JPanel {
    
    private MyTableModel tabla;
    private JTable table;
 
    public TablaS() {
        super(new GridLayout(1,0));
        
        tabla = new MyTableModel();
        table = new JTable(tabla);
        table.setPreferredScrollableViewportSize(new Dimension(220, 70));
        table.getColumnModel().getColumn(0).setPreferredWidth(20);
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.setFillsViewportHeight(true);
        
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
    
    public void removeValueAt(int row, int col){
    	tabla.removeValueAt(row, col);
    }
    
    public int selectedRow(){
    	return table.getSelectedRow();
    }
    
        
    ////////////////////////////////////////////////////////////////////CLASE INTERNA
    
    class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"",
                                        "Servidor","ID"};
    
        private Map<Map<Integer,Boolean>,Map<Integer, String>> data = Initialice();
        private Map<Integer,Integer> dataid = new HashMap<Integer,Integer>();
        
        
        public Map<Map<Integer, Boolean>, Map<Integer, String>> Initialice(){
        	Map<Map<Integer,Boolean>,Map<Integer, String>> d = new HashMap<Map<Integer,Boolean>, Map<Integer,String>>();
        	Map<Integer,Boolean> res = new HashMap<Integer,Boolean>();
        	Map<Integer,String> res2 = new HashMap<Integer,String>();
        	d.put(res, res2);
        	return d;
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }
 
        public int getRowCount() {
        	if(data!=null){
        		Iterator<Entry<Map<Integer, Boolean>, Map<Integer, String>>> it = data.entrySet().iterator();
            	Map<Integer,Boolean> res = null;
            	while (it.hasNext()) {
        			Entry<Map<Integer, Boolean>, Map<Integer, String>> e = it.next();
        			res = e.getKey();	
        		}
                return res.size();
        	}else{
        		return 0;
        	}
        	
        }
 
        public String getColumnName(int col) {
            return columnNames[col];
        }
 
        public Object getValueAt(int row, int col) {
        	Boolean b=false;
        	String s="";
        	int id=0;
        	if(col<2){
        		Iterator<Entry<Map<Integer, Boolean>, Map<Integer, String>>> it = data.entrySet().iterator();
        		Map<Integer,Boolean> res = null;
        		Map<Integer,String> res2 = null;
        		while (it.hasNext()) {
        			Entry<Map<Integer, Boolean>, Map<Integer, String>> e = it.next();
        			res = e.getKey();
        			res2 = e.getValue();
        			if(col==0){
        				Iterator<Entry<Integer, Boolean>> it2 = res.entrySet().iterator();
        				while (it2.hasNext()) {
        					Entry<Integer, Boolean> e1 = it2.next();
        					if(row==e1.getKey()){
        						b = e1.getValue();
        					}
        				}
        			
        			}else{
        				Iterator<Entry<Integer, String>> it2 = res2.entrySet().iterator();
        				while (it2.hasNext()) {
        					Entry<Integer, String> e2 = it2.next();
        					if(row==e2.getKey()){
        						s = e2.getValue();
        					}
        				}
        			
        			}
        		}
    		}else{
    			Iterator<Entry<Integer, Integer>> it = dataid.entrySet().iterator();
    			while (it.hasNext()) {
    				Entry<Integer, Integer> e = it.next();
    				if(row==e.getKey()){
    					id = e.getValue();
    				}
    			}
    		}
    		if(col==0){
    			return b;
    		}else if(col==1){
    			return s;
    		}else{
    			return id;
    		}
    		
        }
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }
 
        
        public boolean isCellEditable(int row, int col) {
            if (col==0){
            	return true;
            }else{
            	return false;
            }
        }
 
        
        public void setValueAt(Object value, int row,int col) {
        	
        	
        	Map<Integer, Boolean> res = null;
        	Map<Integer, String> res2 = null;
        	if(col<2){
        		Iterator<Entry<Map<Integer, Boolean>, Map<Integer, String>>> it = data.entrySet().iterator();
           		while (it.hasNext()) {
        			Entry<Map<Integer, Boolean>, Map<Integer, String>> e = it.next();
        			res = e.getKey();
        			res2 = e.getValue();
        			if(value.getClass().toString().toUpperCase().equals("CLASS JAVA.LANG.BOOLEAN")){
        				res.put(row, (Boolean)value);
        			}else{
        				res2.put(row, (String)value);
        			}
        		}
        	}else{
        		if(value.getClass().toString().toUpperCase().equals("CLASS JAVA.LANG.INTEGER")){
        			dataid.put(row, (Integer)value);
        		}
        	}
        	
       		
       		fireTableRowsInserted(row,col);
        }
        
        public void removeValueAt(int row, int col) {
        	
        	Iterator<Entry<Map<Integer, Boolean>, Map<Integer, String>>> it = data.entrySet().iterator();
    		Map<Integer, Boolean> res = null;
    		Map<Integer, String> res2 = null;
    		int x = 0;
    		while (it.hasNext()) {
    			Entry<Map<Integer, Boolean>, Map<Integer, String>> e = it.next();
    			res = e.getKey();
    			res2 = e.getValue();
    			res.remove(row);
    			res2.remove(row);
    			Iterator<Entry<Integer, Boolean>> it2 = res.entrySet().iterator();
    			Iterator<Entry<Integer, String>> it3 = res2.entrySet().iterator();
    			while (it2.hasNext()) {
        			Entry<Integer, Boolean> e1 = it2.next();
        			if(e1.getKey()>row){
        				res.put(e1.getKey()-1, e1.getValue());
        			}
    			}
    			while (it3.hasNext()) {
        			Entry<Integer, String> e1 = it3.next();
        			if(e1.getKey()>row){
        				x++;
        				res2.put(e1.getKey()-1, e1.getValue());
        			}
    			}
    			res.remove(x);
    			res2.remove(x);
    		}
    		
    		dataid.remove(row);
    		x = 0;
    		Iterator<Entry<Integer, Integer>> it2 = dataid.entrySet().iterator();
    		while(it2.hasNext()){
    			Entry<Integer,Integer> e1 = it2.next();
    			if(e1.getKey()>row){
    				x++;
    				dataid.put(e1.getKey()-1,e1.getValue());
    			}
    		}
    		
    		dataid.remove(x);
    		fireTableDataChanged();
    	}

    }
    
}
