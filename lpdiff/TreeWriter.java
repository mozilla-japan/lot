import java.io.* ;
import java.util.* ;
import javax.swing.tree.* ;

public class TreeWriter extends HtmlWriter {

	public void writeTree( LpTree lpTree, String baseDir, String cssFile ) throws java.io.IOException {

		FileOutputStream fos = new FileOutputStream( new File( baseDir, "mlpfiles.html").toString() ) ;
		OutputStreamWriter writer = new OutputStreamWriter( fos, "UTF-8" ) ;
			
		writeHtmlHeader(writer, cssFile) ;
			
		DefaultMutableTreeNode rootNode =
			(DefaultMutableTreeNode)lpTree.getModel().getRoot() ;
			
		writeFilename( writer, (ChromeFile)rootNode.getUserObject() ) ;
			
		writeTreeDir( writer, rootNode ) ;

		writeHtmlFooter(writer) ;
		writer.close() ;

	}

	public void writeTreeDir( Writer writer, DefaultMutableTreeNode parentNode ) {
		
		ChromeFile currentDir = (ChromeFile)parentNode.getUserObject() ;
		
		writeAbstractName( writer, currentDir ) ;
		writeDirHeader( writer ) ;
		
		Vector dirVec = new Vector() ;
		
		for( int j = 0 ; j < parentNode.getChildCount() ; j++ ) {
		
			DefaultMutableTreeNode childNode =
				(DefaultMutableTreeNode)parentNode.getChildAt(j) ;
			
			ChromeFile cFile = (ChromeFile)childNode.getUserObject() ;
			
			if( cFile.isDirectory() ) {
				dirVec.addElement( childNode ) ;
			}
			else {
				writeFile( writer, cFile ) ;
			}
		}
		
		writeDirFooter( writer ) ;
		for( int i = 0 ; i < dirVec.size() ; i++ ) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)dirVec.get(i) ;
			writeTreeDir(  writer, node ) ;
		}
		
		
	}

	private void writeDirHeader( Writer writer ) {

		try {
			writer.write( "\n<ul>\n" ) ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(2) ;
		}

	}

	private void writeDirFooter( Writer writer ) {
		try {
			writer.write( "</ul>\n" ) ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(2) ;
		}
	}

	private void writeFile( Writer writer, ChromeFile cFile ) {
		try {
			writer.write( "<li><a href=\"" ) ;
			
			String path = cFile.getAbstractName() ;
			path = path.substring( 1 ) ;
			
			
			writer.write( path + ".html") ;
			writer.write( "\">" ) ;
			writer.write( cFile.getName() ) ;
			writer.write( "</a>" ) ;
			writer.write( "</li>\n" ) ;
		
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(2) ;
		}
	}


}
