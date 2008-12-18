import javax.swing.* ;
import javax.swing.tree.* ;
import java.io.* ;
import java.awt.* ;
import java.awt.event.* ;

public class LpTree extends JTree {

	DefaultMutableTreeNode rootNode= new DefaultMutableTreeNode("Root") ;

	// コンストラクタ
	public LpTree() {
		((DefaultTreeModel)getModel()).setRoot( rootNode ) ;
	}

	// ディレクトリツリーの読み直し
	public void LoadLp( String dir1, String dir2 ) throws LpException {
		FirstScan(dir1) ;
		SecondScan(dir2) ;
	}

	public void FirstScan( String topDir1 ) throws LpException {
		ChromeFile topDir = ChromeFile.createInstance( new File(topDir1), 0 ) ;
		rootNode.setUserObject( topDir ) ;
		topDir.setTreeNode( rootNode ) ;
		
		FirstScanLoop( rootNode ) ;
	}

	public void FirstScanLoop( DefaultMutableTreeNode parentNode ) throws LpException {
		
		ChromeFile currentDir = (ChromeFile)parentNode.getUserObject() ;
		String[] files = currentDir.getFile(0).list() ;
		
		if( files == null ) {
			throw new LpException("FirstDir not found");
		}
		
		for( int i = 0 ; i < files.length ; i++ ) {
			ChromeFile file =
				ChromeFile.createInstance( new File(currentDir.getFile(0), files[i]), 0 ) ;
			DefaultMutableTreeNode node = new DefaultMutableTreeNode( file ) ;
			parentNode.add(node) ;
			file.setTreeNode( node ) ;
			
			if( file.getFile(0).isDirectory() == true ) {
				FirstScanLoop( node ) ;
			}
		}
	}











	public void SecondScan( String topDir2 ) throws LpException {
		//System.out.println("SecondScan called:"+topDir2) ;
		ChromeFile rootFile = (ChromeFile)rootNode.getUserObject() ;
		rootFile.setFile( new File(topDir2), 1 ) ;
		
		SecondScanLoop( rootNode, topDir2 ) ;
	}

	public void SecondScanLoop(DefaultMutableTreeNode originalNode, String compareDir ) throws LpException {

		File cmpDir = new File( compareDir ) ;

		String[] files = cmpDir.list() ;

		if( files == null ) {
			throw new LpException( compareDir + " is empty");
		}
		
		
		for( int i = 0 ; i < files.length ; i++ ) {
		
			boolean isExist = false ;
			DefaultMutableTreeNode childNode = null ;
			
			for( int j = 0 ; j < originalNode.getChildCount() ; j++ ) {
				childNode =	(DefaultMutableTreeNode)originalNode.getChildAt(j) ;
				ChromeFile cFile = (ChromeFile)childNode.getUserObject() ;
				if( files[i].compareTo(cFile.getName()) == 0 ) {
					cFile.setFile( new File(cmpDir, files[i]), 1 ) ;
					isExist = true ;
					break ;
				}
			}
			
			if( isExist == false ) {
				ChromeFile newFile =
					ChromeFile.createInstance( new File(cmpDir, files[i] ), 1 ) ;
				DefaultMutableTreeNode aNode = new DefaultMutableTreeNode( newFile );
				originalNode.add( aNode ) ;
				newFile.setTreeNode( aNode ) ;
				//newFile.setTreeNode( originalNode ) ;
				
				if( newFile.getFile(1).isDirectory() == true ) {
					SecondScanLoop( aNode, compareDir + "\\" + files[i] ) ;
				}
			}
			else {
				File nFile = new File( cmpDir, files[i] ) ;
				if( nFile.isDirectory() ) {
					SecondScanLoop( childNode, compareDir + "\\" + files[i] ) ;
				}
			}
		
		}
	}
	

}


