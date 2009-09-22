
public class Entity {

	static int OLD_ONLY = 0 ;
	static int NEW_ONLY = 1 ;
	static int MATCH = 2 ;
	static int DIFF = 3 ;

	String name ;
	String oldValue ;
	String newValue ;

	public Entity( String aName, String aValue, int fileNo ) {
		name = new String(aName) ;
		if( fileNo == 0 ) {
			oldValue = new String( aValue ) ;
		}
		else {
			newValue = new String( aValue ) ;
		}
	}

	public void setValue( String aValue, int fileNo ) {
		
		if( fileNo == 0 ) {
			oldValue = new String( aValue ) ;
		}
		else {
			newValue = new String( aValue ) ;
		}
	}


	public boolean isMatch() {
	
		if( oldValue == null || newValue == null ) {
			return false ;
		}
		else if( oldValue.equals(newValue) ) {
			return true ;
		}
		
		return false ;
	
	}

	public int getFileState() {
	
		if( oldValue == null ) {
			return NEW_ONLY ;
		}
		else if( newValue == null ) {
			return OLD_ONLY ;
		}
		else if( oldValue.equals(newValue) ) {
			return MATCH ;
		}
		
		return DIFF ;
		
	
	}

}

