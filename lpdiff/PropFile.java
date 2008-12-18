import java.io.* ;
import java.util.* ;

public class PropFile extends UnknownFile {
	
	public Hashtable makeHash() {
	
		Hashtable hashtable = new Hashtable() ;
		
		for( int i = 0 ; i < files.length ; i++ ) {
			if( files[i] != null ) {
				LoadFile( files[i].toString(), i, hashtable ) ;
			}
		}
		return hashtable ;
		
	}

	private void LoadFile( String filename, int fileNo, Hashtable hashtable ) {

		Properties prop = new Properties();
		try {
	        FileInputStream fis = new FileInputStream(filename) ;
	        prop.load( fis );
    	    fis.close();
		}
		catch( Exception e1 ) {
			e1.printStackTrace() ;
		}
    	    
		Enumeration enum = prop.propertyNames();
		while (enum.hasMoreElements()) {
			
		    String eName = (String) enum.nextElement();
            String eValue = prop.getProperty(eName);

			Object o = hashtable.get( eName ) ;
			if( o == null ) {
				Entity e = new Entity( eName, eValue, 0) ;
				hashtable.put( eName, e ) ;
			}
			else {
				((Entity)o).setValue( eValue, 1 ) ;
			}
		}
	}

}
