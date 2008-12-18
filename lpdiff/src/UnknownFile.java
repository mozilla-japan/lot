import java.io.* ;
import java.util.* ;

public class UnknownFile extends ChromeFile {

	protected int diffStatus = -1 ; // -1:unknown 0:equal 1 or over :difference

	public boolean isDirectory() {
		return false ;
	}

	public boolean isDiff() {
	
		if( numDiff() != 0 ) {
			return true ;
		}
		
		return false ;
	}
	
	public int numDiff() {
	
		if( diffStatus == -1 ) {
			
			if( (files[0] == null) || (files[1] == null) ) {
				diffStatus = 1 ; // 0?
			}
			else if( files[0].lastModified() != files[1].lastModified() ) {
				diffStatus = 1 ;
			}
			else if( files[0].length() != files[1].length() ) {
				diffStatus = 1 ;
			}
			else {
				diffStatus = 0 ;
			}
			
		}

		return diffStatus ;
	}
	
	public Hashtable makeHash() {
		return new Hashtable() ;
	}
	

}
