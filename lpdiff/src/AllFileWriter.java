import java.io.* ;
import java.util.* ;
import javax.swing.tree.* ;

public class AllFileWriter extends HtmlWriter {

	private String[] headers = null ;

	public void printAll( LpTree lpTree, String baseDir, String[] hdrs, String cssFileName ) {

		headers = hdrs ;

		DefaultMutableTreeNode rootNode =
			(DefaultMutableTreeNode)lpTree.getModel().getRoot() ;
		printDir( rootNode, baseDir, cssFileName ) ;

	}

	public void printDir( DefaultMutableTreeNode parentNode,
						   String writeBaseDir, String cssFileName ) {
		
		ChromeFile curFile = (ChromeFile)parentNode.getUserObject() ;
		String cssFile = "" ;
		for( int d = 0 ; d < curFile.getLevel() ; d++ ) {
			cssFile += "../" ;
		}
		cssFile += cssFileName ;
		
		File currentDir = new File( writeBaseDir ) ;
		currentDir.mkdir() ;
		
		for( int j = 0 ; j < parentNode.getChildCount() ; j++ ) {
		
			DefaultMutableTreeNode childNode =
				(DefaultMutableTreeNode)parentNode.getChildAt(j) ;
			ChromeFile cFile = (ChromeFile)childNode.getUserObject() ;
			
			String newFileName = new File(writeBaseDir, cFile.getName() ).toString() ;
			
			if( cFile.isDirectory() ) {
				printDir( childNode, newFileName, cssFileName ) ;
			}
			else {
				printFile( writeBaseDir, cFile, cssFile ) ;
			}
		}
	}

	private void printFile( String writeBaseDir, ChromeFile cFile, String cssFile ) {
		
		try {
			String filename = new File( writeBaseDir, cFile.getName()+".html" ).toString() ;
			File outFile = new File( filename ) ;
			outFile.createNewFile() ;
			
			FileOutputStream fos = new FileOutputStream( outFile ) ;
			OutputStreamWriter writer = new OutputStreamWriter( fos, "UTF-8" ) ;
			
			writeHtmlHeader( writer, cssFile ) ;
			writeAbstractName( writer, cFile ) ;
			writeFilename( writer, cFile ) ;
			
			/*
			String[] headers = new String[3] ;
			headers[0] = new String("ENTITY") ;
			headers[1] = new String("OLD VALUE") ;
			headers[2] = new String("NEW VALUE") ;
			*/
			writeTableHeader(writer, headers) ;
			
			Hashtable hash = cFile.makeHash() ;
			Collection col = hash.values() ;
			Iterator it = col.iterator() ;

			while( it.hasNext() ) {
				Entity entity = (Entity)it.next() ;
				writeTableRow( writer, entity ) ;
			}
			
			writeTableFooter( writer ) ;
			writeHtmlFooter( writer ) ;
			writer.close() ;
		}
		catch( IOException ioe ) {
			ioe.printStackTrace() ;
			System.exit(2) ;
		}
	}


}
