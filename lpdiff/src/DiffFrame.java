import java.awt.* ;
import java.awt.event.* ;
import javax.swing.* ;
import javax.swing.tree.* ;
import javax.swing.table.* ;

public class DiffFrame extends JFrame {

	JSplitPane sPane = null ;
	LpTree  chromeTree = null ;
	LpTable dtdTable = null ;
	DiffMenuBar jmb = null ;
	
	String header1 = null ;
	String header2 = null ;

	public DiffFrame() {
		super( "Mozilla LangPack Diff" ) ;
		
		chromeTree = new LpTree() ;
		
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = chromeTree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = chromeTree.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1) {
					if(e.getClickCount() == 1) {
						; //mySingleClick(selRow, selPath);
					}
					else if(e.getClickCount() == 2) {
						myDoubleClick(selRow, selPath);
					}
				}
			}
		};
		chromeTree.addMouseListener(ml);
		
		dtdTable = new LpTable(this) ;
		jmb = new DiffMenuBar(this) ;
		setJMenuBar( jmb ) ;
		
		sPane = new JSplitPane( JSplitPane.HORIZONTAL_SPLIT,
								new JScrollPane(chromeTree),
								new JScrollPane(dtdTable) ) ;
		sPane.setDividerLocation( 200 );
		getContentPane().add( sPane ) ;
		setSize(600,480);
		show() ;
		
	}

	public void LoadDirTree(String dir1, String dir2, String label1, String label2) throws LpException {
		chromeTree = new LpTree() ;
		header1 = label1 ;
		header2 = label2 ;
		MouseListener ml = new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				int selRow = chromeTree.getRowForLocation(e.getX(), e.getY());
				TreePath selPath = chromeTree.getPathForLocation(e.getX(), e.getY());
				if(selRow != -1) {
					if(e.getClickCount() == 1) {
						; //mySingleClick(selRow, selPath);
					}
					else if(e.getClickCount() == 2) {
						myDoubleClick(selRow, selPath);
					}
				}
			}
		};
		chromeTree.addMouseListener(ml);
		
		chromeTree.LoadLp( dir1, dir2 ) ;
		sPane.setLeftComponent( new JScrollPane(chromeTree) );
	}

	public void LoadDtdTable( ChromeFile cf, String[] headers ) {
		if( cf.isDirectory() ) {
			return ;
		}
		else if( cf.getClass() == DtdFile.class ||
			cf.getClass() == PropFile.class ) {
			dtdTable.LoadChromeFile( cf, headers ) ;
			sPane.setRightComponent( new JScrollPane(dtdTable) );
		}
		else {
			
			DiffTextPane newPane = new DiffTextPane( cf ) ;
			sPane.setRightComponent( newPane ) ;
		}
	}

	public void PrintDiff( String filename, String cssFileName ) {
		String[] headers = new String[3] ;
		headers[0] = new String("ENTITY") ;
		headers[1] = header1 ;
		headers[2] = header2 ;
		DiffWriter dWriter = new DiffWriter() ;
		dWriter.printDiff(chromeTree, filename, headers, cssFileName ) ;
	}

	public void PrintAll( String baseDir, String cssFileName ) throws java.io.IOException {
		TreeWriter tWriter = new TreeWriter() ;
		tWriter.writeTree( chromeTree, baseDir, cssFileName ) ;

		String[] headers = new String[3] ;
		headers[0] = new String("ENTITY") ;
		headers[1] = header1 ;
		headers[2] = header2 ;
		AllFileWriter aWriter = new AllFileWriter() ;
		aWriter.printAll(chromeTree, baseDir, headers, cssFileName) ;
	}

	void myDoubleClick(int selRow, TreePath selPath) {
		Object o = selPath.getLastPathComponent() ;
		DefaultMutableTreeNode tn = (DefaultMutableTreeNode)o ;
		ChromeFile cf = (ChromeFile)tn.getUserObject() ;
		if( cf.isDirectory() ) {
			return ;
		}
		String[] headers = new String[3] ;
		headers[0] = new String("ENTITY") ;
		headers[1] = header1 ;
		headers[2] = header2 ;
		
		LoadDtdTable( cf, headers ) ;
	}

}

