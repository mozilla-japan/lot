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
		
		String str = null;
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
			
			// to avoid trim from multiline difinition...
			String trimedline = line.trim() ;
			if ( trimedline.startsWith( new String("<!ENTITY") ) ) {
				str = line ;
			}
			else if ( str != null ) {
				str += line ;
			}
			
			if ( str != null && trimedline.endsWith( new String(">") ) ) {
				String[] eStrs = parseEntity( str ) ;
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
				str = null ;
			}
		}
	}

	private String[] parseEntity( String str ) {
		
		str = str.trim() ;
		
		if( str.startsWith( new String("<!ENTITY") ) &&
			str.endsWith( new String(">") ) ) {
			
			int v1, v2 ;
			int v2d = str.lastIndexOf('"') ;
			int v1d = str.indexOf('"') ;
			int v2s = str.lastIndexOf("'") ;
			int v1s = str.indexOf("'") ;
			
			if ( v1d != -1 ) {
				if ( v1s != -1 ) {
					if ( v1d < v1s ) {
						v1 = v1d ;  v2 = v2d ;
					}
					else {
						v1 = v1s ;  v2 = v2s ;
					}
				}
				else {
					v1 = v1d ;  v2 = v2d ;
				}
			}
			else {
				v1 = v1s ;  v2 = v2s ;
			}
			
			String eName = str.substring( 8, v1 ) ;
			eName = eName.trim() ;
			String eValue = str.substring( v1+1, v2 ) ;
			eValue = eValue.trim() ;
			
			String[] rtnStrs = new String[2] ;
			rtnStrs[0] = new String( eName ) ;
			rtnStrs[1] = new String( eValue ) ;

			return rtnStrs ;
		}
		
		return null ;
	}

}

