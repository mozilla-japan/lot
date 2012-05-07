import java.io.* ;

public class CliDiff {

	private final String strArgHelp = "Usage:\njava -jar LpDiff.jar -diff [compare dir 1] [compare dir2] [output file]\n or\njava -jar LpDiff.jar -all [compare dir 1] [compare dir2] [output dir]";

	public void run( String argv[] ) {

		try {
			if( argv[0].equals("-diff") && argv.length == 4) {
				
				System.out.println("Write Diff" ) ;
				String dir1 = argv[1] ;
				File dir1File = new File(  new File(dir1).getCanonicalPath() ) ;
				if( dir1File.isFile() ) {
					System.out.println("dir1 is invalid:" + dir1File.toString() ) ;
					System.exit(1) ;
				}
				
				String dir2 = argv[2] ;
				File dir2File = new File( new File(dir2).getCanonicalPath() ) ;
				if( dir2File.isFile() ) {
					System.out.println("dir2 is invalid:" + dir2File.toString() ) ;
					System.exit(1) ;
				}
				
				String filename = argv[3] ;
				File outFile = new File( new File(filename).getCanonicalPath() ) ;
				if( ! outFile.createNewFile() ) {
					System.out.println("outFile is invalid:" + outFile.toString() ) ;
					System.exit(1) ;
				}
				LpTree lpTree = new LpTree() ;
				try {
					lpTree.LoadLp( dir1File.toString(), dir2File.toString() ) ;
				} catch (LpException e) {
					System.out.println(e.toString());
				}
				DiffWriter dWriter = new DiffWriter() ;
				String[] headers = new String[3] ;
				headers[0] = "ENTITY" ;
				headers[1] = "OLD VALUE" ;
				headers[2] = "NEW VALUE" ;
				dWriter.printDiff(lpTree, outFile.toString(), headers, "mlpdiff.css" ) ;
			}
			else if( argv[0].equals("-all") && argv.length == 4) {
				
				System.out.println("Write All" ) ;
				
				String dir1 = argv[1] ;
				File dir1File = new File( new File(dir1).getCanonicalPath() ) ;
				if( ! dir1File.isDirectory() ) {
					System.out.println("dir1 is invalid:" + dir1File.toString() ) ;
					System.exit(1) ;
				}
				
				String dir2 = argv[2] ;
				File dir2File = new File( new File(dir2).getCanonicalPath() ) ;
				if( ! dir2File.isDirectory() ) {
					System.out.println("dir2 is invalid:" + dir2File.toString() ) ;
					System.exit(1) ;
				}
				
				String filename = argv[3] ;
				File outDir = new File( new File(filename).getCanonicalPath() ) ;
				if( ! outDir.isDirectory() ) {
					System.out.println("outDir is invalid:" + outDir.toString() ) ;
					System.exit(1) ;
				}
				
				LpTree lpTree = new LpTree() ;
				try {
					lpTree.LoadLp( dir1File.toString(), dir2File.toString() ) ;
				} catch (LpException e) {
					System.out.println(e.toString());
				}
				TreeWriter tWriter = new TreeWriter() ;
				tWriter.writeTree( lpTree, outDir.toString(), "mlpdiffall.css" ) ;
				String[] headers = new String[3] ;
				headers[0] = "ENTITY" ;
				headers[1] = "OLD VALUE" ;
				headers[2] = "NEW VALUE" ;
				AllFileWriter hWriter = new AllFileWriter() ;
				hWriter.printAll( lpTree, outDir.toString(), headers, "mlpdiffall.css" ) ;
			}
			else {
				System.out.println("Parameter invalid.") ;
				System.out.println(strArgHelp);
			}
		}
		catch( IOException ioe ) {
			ioe.printStackTrace() ;
		}

	}

}
