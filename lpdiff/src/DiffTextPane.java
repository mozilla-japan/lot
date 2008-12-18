import java.io.* ;
import javax.swing.* ;

public class DiffTextPane extends JSplitPane {


	public DiffTextPane( ChromeFile cFile ) {
		super(JSplitPane.HORIZONTAL_SPLIT) ;

		try {

			JEditorPane leftPane = new JEditorPane() ;
			if( cFile.isFileExist(0) ) {
				leftPane.setPage( cFile.toURL(0) ) ;
			}
			else {
				leftPane.setText( "None" ) ;
			}
			leftPane.setEditable( false ) ;
		
			JEditorPane rightPane = new JEditorPane() ;
			if( cFile.isFileExist(1) ) {
				rightPane.setPage( cFile.toURL(1) ) ;
			}
			else {
				rightPane.setText( "None" ) ;
			}
			rightPane.setEditable( false ) ;

			setLeftComponent( new JScrollPane(leftPane) ) ;
			setRightComponent( new JScrollPane( rightPane ) );
			
			setDividerLocation( 300 );
		}
		catch(IOException ioe ) {
			ioe.printStackTrace() ;
			System.exit(2) ;
		}
	}



}
