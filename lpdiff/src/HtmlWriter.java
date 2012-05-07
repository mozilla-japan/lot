import java.io.* ;
import java.util.* ;

public class HtmlWriter {


	protected void writeHtmlHeader( Writer writer, String cssFile ) {

		try {
			writer.write( "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\"\n" ) ;
			writer.write( "\"http://www.w3.org/TR/html4/loose.dtd\">\n" ) ;
			writer.write( "<html>\n" ) ;
			writer.write( "<head>\n" ) ;
			writer.write( "<META http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n") ;
			writer.write( "<link rel=\"stylesheet\" type=\"text/css\" href=\"" + cssFile + "\">\n") ;
			writer.write( "<title>Mozilla Language Pack Diff</title>\n" ) ;
			writer.write( "</head>\n" ) ;
			writer.write( "<body>\n\n" ) ;
			writer.flush() ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(2) ;
		}
	}

	protected void writeHtmlFooter( Writer writer ) {

		try {
			writer.write( "</body>\n</html>\n" ) ;
			writer.flush() ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(2) ;
		}
	}
	
	protected void writeAbstractName( Writer writer, ChromeFile cFile ) {
		try {
			writer.write( "<div class=\"name\">\n" ) ;
			writer.write( "<p>\n" ) ;
			writer.write( cFile.getAbstractName() ) ;
			writer.write( "\n</p>\n" ) ;
			writer.write( "</div>\n\n" ) ;
		}
		catch( IOException ioe ) {
			ioe.printStackTrace() ;
		}
	}

	protected void writeFilename( Writer writer, ChromeFile cFile ) {

		try {
			writer.write( "<div class=\"filenames\">\n" ) ;
			writer.write( "<p>\n" ) ;
			
			if( cFile.isFileExist(0) ) {
				writer.write( "Old File:" + cFile.getCanonicalPath(0) + "<br>\n") ;
			}
			else {
				writer.write( "Old File: none<br>\n") ;
			}
			
			if( cFile.isFileExist(1) ) {
				writer.write( "New File:" + cFile.getCanonicalPath(1) + "\n") ;
			}
			else {
				writer.write( "New File: none\n") ;
			}
			writer.write( "</p>\n" ) ;
			writer.write( "</div>\n\n" ) ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(2) ;
		}
	}

	protected void writeTableHeader( Writer writer, String[] headers ) {
		try {
			writer.write( "<table>\n") ;
			writer.write( "<tr>\n") ;
			for( int size = 0 ; size < headers.length ; size++ ) {
				writer.write( "<th>") ;
				writer.write( headers[size] ) ;
				writer.write( "</th>") ;
			}
			writer.write( "\n</tr>\n") ;
		}
		catch( IOException ioe ) {
			ioe.printStackTrace() ;
			System.exit(1) ;
		}
	}

	protected void writeTableFooter( Writer writer ) {
		try {
			writer.write( "</table>\n") ;
		}
		catch( IOException ioe ) {
			ioe.printStackTrace() ;
			System.exit(1) ;
		}
	}

	protected void writeTableRow( Writer writer, Entity entity ) {

		try {
			writer.write( "<tr ") ;
			
			if( entity.oldValue == null ) {
				writer.write( "class=\"add\">" ) ;
			}
			else if( entity.newValue == null ) {
				writer.write( "class=\"remove\">" ) ;
			}
			else if( entity.newValue.equals( entity.oldValue ) ) {
				writer.write( "class=\"match\">" ) ;
			}
			else {
				writer.write( "class=\"modify\">" ) ;
			}
			writer.write( "\n") ;
			
			writer.write( "<td>" + entity.name + "</td>") ;
			if( entity.oldValue == null ) {
				writer.write( "<td>" + new String("<br>") + "</td>") ;
			}
			else {
				String v = entity.oldValue ;
				if( (v.indexOf('<') != -1) || (v.indexOf('>') != -1) ){
					v = convHtmlTab( v ) ;
				}
				writer.write( "<td>" + v + "</td>") ;
			}

			if( entity.newValue == null ) {
				writer.write( "<td>" + new String("<br>") + "</td>\n") ;
			}
			else {
				String v = entity.newValue ;
				if( (v.indexOf('<') != -1) || (v.indexOf('>') != -1) ){
					v = convHtmlTab( v ) ;
				}
				writer.write( "<td>" + v + "</td>") ;
			}
			writer.write( "</tr>\n") ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(2) ;
		}
		
	}

	private String convHtmlTab( String v ) {
	
		String buffer = new String(v) ;
	
		while( buffer.indexOf('<') != -1 ) {
			int index = buffer.indexOf('<') ;
			StringBuffer buf = new StringBuffer(buffer) ;
			buf = buf.replace( index, index+1, "&lt;" ) ;
			buffer = new String(buf) ;
		}

		while( buffer.indexOf('>') != -1 ) {
			int index = buffer.indexOf('>') ;
			StringBuffer buf = new StringBuffer(buffer) ;
			buf = buf.replace( index, index+1, "&gt;" ) ;
			buffer = new String(buf) ;
		}
		
		return buffer ;
	}

}

