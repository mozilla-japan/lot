import java.io.* ;
import java.util.* ;
import javax.swing.tree.* ;

public class DiffWriter extends HtmlWriter {

	private String[] headers = null ;

	public void printDiff( LpTree lpTree, String filename, String[] hdrs, String cssFileName ) {

		headers = hdrs ;

		try {
			
			FileOutputStream fos = new FileOutputStream( filename ) ;
			OutputStreamWriter writer = new OutputStreamWriter( fos, "UTF-8" ) ;
			
			writeHtmlHeader(writer, cssFileName ) ;
			
			DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode)lpTree.getModel().getRoot() ;
			printDir( rootNode, writer ) ;

			writeHtmlFooter(writer) ;
			writer.close() ;

		}
		catch( IOException ioe ) {
			ioe.printStackTrace() ;
			System.exit(2) ;
		}
	}

	public void printDir( DefaultMutableTreeNode parentNode,
						   Writer writer ) {
		
		ChromeFile currentDir = (ChromeFile)parentNode.getUserObject() ;
		for( int j = 0 ; j < parentNode.getChildCount() ; j++ ) {
		
			DefaultMutableTreeNode childNode =
				(DefaultMutableTreeNode)parentNode.getChildAt(j) ;
			
			ChromeFile cFile = (ChromeFile)childNode.getUserObject() ;

			if( cFile.isDirectory() ) {
				printDir( childNode, writer ) ;
			}
			else {
				printFile( cFile, writer ) ;
			}
		}
	}

	private void printFile( ChromeFile cFile, Writer writer ) {

		if( cFile.isDiff() == true ) {
			
			Hashtable diffHash = cFile.makeUndiffHash( Entity.MATCH ) ;
			if( diffHash.size() == 0 ) {
				return ;
			}
			
			writeAbstractName( writer, cFile ) ;
			writeFilename( writer, cFile ) ;
			/*
			String[] headers = new String[3] ;
			headers[0] = new String("ENTITY") ;
			headers[1] = new String("OLD VALUE") ;
			headers[2] = new String("NEW VALUE") ;
			*/
			writeTableHeader( writer, headers ) ;
			
			Collection col = diffHash.values() ;
			Iterator it = col.iterator() ;
			
			while( it.hasNext() ) {
				Entity entity = (Entity)it.next() ;
				writeTableRow( writer, entity ) ;
			}
			writeTableFooter( writer ) ;
		}
	}

}
