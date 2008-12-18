import java.io.* ;
import java.util.* ;
import javax.swing.tree.* ;

public class DirFile extends ChromeFile {

	protected int diffStatus = -1 ; // -1:unknown 0:equal 1: difference

	public boolean	isDirectory() {
		return true ;
	}

	public Hashtable makeHash() {

		Hashtable hashtable = new Hashtable() ;
		return hashtable ;
	}

	public boolean isDiff() {

		if( diffStatus == -1 ) {

			for( int j = 0 ; j < myNode.getChildCount() ; j++ ) {
				DefaultMutableTreeNode childNode =	(DefaultMutableTreeNode)myNode.getChildAt(j) ;
				ChromeFile cFile = (ChromeFile)childNode.getUserObject() ;
				if( cFile.isDiff() ) {
					diffStatus = 1 ;
					break ;
				}
			}
			if( diffStatus == -1 ) {
				diffStatus = 0 ;
			}

		}

		if( diffStatus == 0 ) {
			return false ;
		}

		return true ;

	}

	public int numDiff() {

		if( diffStatus == -1 ) {

			diffStatus = 0 ;

			for( int j = 0 ; j < myNode.getChildCount() ; j++ ) {
				DefaultMutableTreeNode childNode =	(DefaultMutableTreeNode)myNode.getChildAt(j) ;
				ChromeFile cFile = (ChromeFile)childNode.getUserObject() ;
				//if( cFile.isDiff() ) {
				diffStatus += cFile.numDiff() ;
				//}
			}
			//if( diffStatus == -1 ) {
			//	diffStatus = 0 ;
			//}

		}

		return diffStatus ;
	}

	public String toString() {

		String label = null ;

		if( (files[0] != null) && (files[1] != null) ) {
			label = files[0].getName() ;
			int nDiff = numDiff() ;
			if( nDiff != 0 ) {
				label = label + "(" + Integer.toString(nDiff) + ")" ;
			}
		}
		else if( files[0] != null ) {
			label = files[0].getName() + "(-)" ;
		}
		else {
			label = files[1].getName() + "(+)";
		}
		return label ;
	}


}

