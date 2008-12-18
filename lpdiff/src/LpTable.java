import java.awt.* ;
import java.io.* ;
import java.util.* ;
import javax.swing.* ;
import javax.swing.table.* ;


public class LpTable extends JTable {
	
	DefaultTableModel dtdModel = null ;
	DiffFrame pFrame = null ;
	
	Hashtable hashtable = new Hashtable() ;
	
	public LpTable(DiffFrame frame) {
		super() ;
		pFrame = frame ;
		
		String[] title = new String[3] ;
		title[0] = new String("ENTITY") ;
		title[1] = new String("OLD VALUE") ;
		title[2] = new String("NEW VALUE") ;
		dtdModel = new DefaultTableModel( title, 0) ;
		setModel(dtdModel);
		
		DtdTableCellRenderer renderer = new DtdTableCellRenderer() ;
		getColumnModel().getColumn(0).setCellRenderer( renderer ) ;
		getColumnModel().getColumn(1).setCellRenderer( renderer ) ;
		getColumnModel().getColumn(2).setCellRenderer( renderer ) ;
	}

	public void LoadChromeFile( ChromeFile cf, String[] headers ) {

		/*
		String[] title = new String[3] ;
		title[0] = new String("ENTITY") ;
		title[1] = new String("OLD VALUE") ;
		title[2] = new String("NEW VALUE") ;
		*/
		dtdModel = new DefaultTableModel( headers, 0) ;
		setModel(dtdModel);
		DtdTableCellRenderer renderer = new DtdTableCellRenderer() ;
		getColumnModel().getColumn(0).setCellRenderer( renderer ) ;
		getColumnModel().getColumn(1).setCellRenderer( renderer ) ;
		getColumnModel().getColumn(2).setCellRenderer( renderer ) ;
		
		//System.out.println("Row count after delete:" + Integer.toString(dtdModel.getRowCount()) ) ;

		Hashtable hashtable = cf.makeHash() ;
		Collection col = hashtable.values() ;

		Iterator it = col.iterator() ;
		while( it.hasNext() ) {
			Entity entity = (Entity)it.next() ;
			String[] str = new String[3] ;
			str[0] = entity.name ;
			if( entity.oldValue != null ) {
				str[1] = entity.oldValue ;
			}
			else {
				str[1] = "" ;
			}
			
			if( entity.newValue != null ) {
				str[2] = entity.newValue ;
			}
			else {
				str[2] = "" ;
			}
			dtdModel.addRow( str ) ;
		}
	}
}


class DtdTableCellRenderer implements TableCellRenderer {

	public Component getTableCellRendererComponent(JTable table,
                                               Object value,
                                               boolean isSelected,
                                               boolean hasFocus,
                                               int row,
                                               int column) {
	
		DefaultTableModel model = (DefaultTableModel)table.getModel() ;

		JLabel field = new JLabel((String)value) ;
		
		String str1 = (String)model.getValueAt(row, 1) ;
		String str2 = (String)model.getValueAt(row, 2) ;
		
		if( str1.equals(str2) ) {
			;
		}
		else if( str1.equals("") ) {
			field.setForeground( Color.red ) ;
		}
		else if( str2.equals("") ) {
			field.setForeground( Color.magenta ) ;
		}
		else {
			field.setForeground( Color.blue ) ;
		}
		//else {
		//	field.setForeground( Color.black ) ;
		//}
		
		return field ;
	
	}

}


