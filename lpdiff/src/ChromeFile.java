import java.io.* ;
import java.net.* ;
import java.util.* ;
import javax.swing.tree.* ;

public abstract class ChromeFile {

	public abstract boolean isDiff() ;
	public abstract int numDiff() ;
	public abstract boolean isDirectory() ;
	public abstract Hashtable makeHash() ;
	
	protected String name = null ;
	public static int MAXFILENO = 2 ;
	protected File[] files = new File[MAXFILENO] ;
	protected DefaultMutableTreeNode myNode = null ;
	
	public static ChromeFile createInstance( File file, int fileno ) {
		
		if( fileno >= MAXFILENO ) {
			System.err.println("fileno exceeded max:" + Integer.toString(fileno)) ;
			System.exit(1) ;
		}
		
		ChromeFile newFile = null ;
		if( file.isDirectory() ) {
			newFile = new DirFile() ;
		}
		else if( file.getName().endsWith(".dtd" ) ) {
			newFile = new DtdFile() ;
		}
		else if( file.getName().endsWith(".properties" ) ) {
			newFile = new PropFile() ;
		}
		else {
			newFile = new UnknownFile() ;
		}
		
		newFile.setFile( file, fileno ) ;
		return newFile ;
	}
	
	public void setFile( File file, int fileno ) {
		
		if( fileno >= MAXFILENO ) {
			System.err.println("fileno exceeded max:" + Integer.toString(fileno)) ;
			System.exit(1) ;
		}
		
		files[fileno] = file ;
		name = file.getName() ;
	}

	public File getFile( int fileno ) {
		return files[fileno] ;
	}
	
	public void setTreeNode( DefaultMutableTreeNode node ) {
		myNode = node ;
	}
	
	public DefaultMutableTreeNode getNode() {
		return myNode ;
	}
	
	/**
	rootNode          = "/"
	root/dir          = "dir"
	root/filename     = "filename"
	root/dir/filename = "filename"
	*/
	public String getName() {
		return name ;
	}
	
	/**
	rootNode          = [/]
	root/dir          = [/][dir]
	root/filename     = [/][filename]
	root/dir/filename = [/][dir][filename]
	*/
	public String[] getPath() {
		
		DefaultMutableTreeNode pNode =
			(DefaultMutableTreeNode)myNode.getParent() ;
		ChromeFile pcFile = (ChromeFile)pNode.getUserObject() ;
		String[] pcPath = pcFile.getPath() ;
		
		String[] path = new String[ pcPath.length + 1 ] ;
		for( int i = 0 ; i < pcPath.length ; i++ ) {
			path[i] = pcPath[i] ;
		}
		path[path.length-1] = name ;
		
		return path ;
	}
	
	/**
	rootNode          = "/"
	root/dir          = "/dir/"
	root/filename     = "/filename"
	root/dir/filename = "/dir/filename"
	*/
	public String getAbstractName() {
		if( myNode.isRoot() ) {
			return "/" ;
		}
		
		DefaultMutableTreeNode pNode =
			(DefaultMutableTreeNode)myNode.getParent() ;
		ChromeFile pcFile = (ChromeFile)pNode.getUserObject() ;
		String pcPath = pcFile.getAbstractName() ;
		pcPath += name ;
		if( isDirectory() ) {
			pcPath += "/" ;
		}
		
		return pcPath ;
	}
	
	/**
	rootNode          = 0
	root/dir          = 1
	root/filename     = 1
	root/dir/filename = 2
	*/
	public int getLevel() {
		return myNode.getLevel() ;
	}
	
	public boolean isFileExist( int fileno ) {
		if( files[fileno] == null ) {
			return false ;
		}
		return true ;
	}
	
	public String getCanonicalPath( int fileno ) {
		String cPath = null ;
		try {
			cPath = files[fileno].getCanonicalPath() ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(1) ;
		}
		return cPath ;
	}

	public URL toURL( int fileno ) {
		URL url = null ;
		try {
			url = files[fileno].toURL() ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
			System.exit(1) ;
		}
		return url ;
	}
	
	public String toString() {
		
		String label = null ;
		
		if( (files[0] != null) && (files[1] != null) ) {
			label = files[0].getName() ;
			int nDiff = numDiff() ;
			if( isDiff() ) {
				label = label + "(*)" ;
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

	public Hashtable makeDiffHash( int state ) {
		
		Hashtable allHash = makeHash() ;
		
		Collection col = allHash.values() ;
		Iterator it = col.iterator() ;
		
		Hashtable diffHash = new Hashtable() ;
		
		while( it.hasNext() ) {
			Entity entity = (Entity)it.next() ;
			if( state == entity.getFileState() ) {
				diffHash.put( entity.name, entity ) ;
			}
		}
		
		return diffHash ;
	}

	public Hashtable makeUndiffHash( int state ) {
		
		Hashtable allHash = makeHash() ;
		
		Collection col = allHash.values() ;
		Iterator it = col.iterator() ;
		
		Hashtable diffHash = new Hashtable() ;
		
		while( it.hasNext() ) {
			Entity entity = (Entity)it.next() ;
			if( state != entity.getFileState() ) {
				diffHash.put( entity.name, entity ) ;
			}
		}
		
		return diffHash ;
	}

}
