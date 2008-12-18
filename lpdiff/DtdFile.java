import java.io.* ;
import java.util.* ;

public class DtdFile extends UnknownFile {
	
	//private Hashtable hashtable = new Hashtable() ;

	public Hashtable makeHash() {
	
		Hashtable hashtable = new Hashtable() ;
		
		for( int i = 0 ; i < files.length ; i++ ) {
			if( files[i] != null ) {
				LoadFile( files[i].toString(), i, hashtable ) ;
			}
		}
		
		return hashtable ;
	}

	private void LoadFile( String filename, int fileNumber, Hashtable hashtable ) {

		BufferedReader bufReader = null ;
		try {
			bufReader = new BufferedReader( new InputStreamReader( new FileInputStream(filename),"UTF-8" ) ) ;
		}
		catch( Exception e1 ) {
			e1.printStackTrace() ;
		}
		
		while(true) {
			String line = null ;
			try {
				line = bufReader.readLine() ;
			}
			catch( Exception e2 ) {
				e2.printStackTrace() ;
			}
			if( line == null ) {
				break ;
			}
			
			String[] eStrs = parseEntity( line ) ;
			if( eStrs != null ) {
				String eName = eStrs[0] ;
				String eValue = eStrs[1] ;
				
				Object o = hashtable.get( eName ) ;
				if( o == null ) {
					Entity e = new Entity( eName, eValue, fileNumber ) ;
					hashtable.put( eName, e ) ;
				}
				else {
					((Entity)o).setValue( eValue, fileNumber ) ;
				}
			}
		}
	}

	private String[] parseEntity( String line ) {
		
		line = line.trim() ;
		
		if( line.startsWith( new String("<!ENTITY") ) &&
			line.endsWith( new String(">") ) ) {
			
			int v2 = line.lastIndexOf('"') ;
			int v1 = line.indexOf('"') ;
			
			String eName = line.substring( 8, v1 ) ;
			eName = eName.trim() ;
			String eValue = line.substring( v1+1, v2 ) ;
			eValue = eValue.trim() ;
			
			String[] rtnStrs = new String[2] ;
			rtnStrs[0] = new String( eName ) ;
			rtnStrs[1] = new String( eValue ) ;

			return rtnStrs ;
		}
		
		return null ;
	}

}

