
public class LpDiff {

	public static void main( String argv[] ) {
		
		if( argv.length == 0 ) {
			DiffFrame diffFrame = new DiffFrame() ;
		}
		else {
			CliDiff cli = new CliDiff() ;
			cli.run(argv) ;
			System.exit(0) ;
		}
		
	}
}

