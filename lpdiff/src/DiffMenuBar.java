import java.awt.event.* ;
import java.io.* ;
import java.util.jar.* ;
import java.util.zip.* ;
import javax.swing.* ;
import javax.swing.border.* ;
import javax.swing.filechooser.FileFilter ;


public class DiffMenuBar extends JMenuBar {

	DiffFrame diffFrame = null ;
	
	DirSelectPanel panel1 = null ;
	DirSelectPanel panel2 = null ;
	
	PrintDiffPanel diffPanel = null ;
	PrintCssPanel cssPanelDiff = null ;
	
	PrintAllPanel allPanel = null ;
	PrintCssPanel cssPanelAll = null ;
	
	public DiffMenuBar( DiffFrame pFrame ) {
		diffFrame = pFrame ;
		
		panel1 = new DirSelectPanel( diffFrame, "Original:", "OLD" ) ;
		panel2 = new DirSelectPanel( diffFrame, "Compared:", "NEW" ) ;
		
		
		cssPanelDiff = new PrintCssPanel() ;
		cssPanelAll = new PrintCssPanel() ;

		diffPanel = new PrintDiffPanel() ;
		allPanel = new PrintAllPanel() ;
		
		
		JMenu fileMenu = new JMenu("File") ;
		
		JMenuItem dirMenuItem = new JMenuItem("Load Directories") ;
		dirMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				Object[] obj = { panel1, panel2 } ;
				int rtn = JOptionPane.showConfirmDialog(diffFrame, obj,
									"Load Directories", JOptionPane.OK_CANCEL_OPTION) ;
				if( rtn == 0 ) {
					try {
						diffFrame.LoadDirTree( panel1.getDir(),
											   panel2.getDir(),
											   panel1.getLabel(),
											   panel2.getLabel() ) ;
					} catch (LpException lpE) {
						JOptionPane.showMessageDialog(diffFrame, lpE.toString());
					}
				}
			}
		} );
		fileMenu.add( dirMenuItem ); 

		fileMenu.addSeparator() ;

		JMenuItem writDiffMenuItem = new JMenuItem("Print difference") ;
		writDiffMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				
				JLabel labelDiff = new JLabel("File name:") ;
				Object[] obj = { labelDiff, diffPanel, cssPanelDiff  } ;
				int rtn = JOptionPane.showConfirmDialog(diffFrame, obj,
										"Print difference to HTML file",
										 JOptionPane.OK_CANCEL_OPTION) ;
				if( rtn == 0 ) {
					diffFrame.PrintDiff( diffPanel.fileText.getText(), "lpdiff.css" ) ;
					
					if( cssPanelDiff.cssCB.isSelected() ) {
						File cssFile = new File(diffPanel.fileText.getText() ) ;
						File cssDir = cssFile.getParentFile() ;
						try {
							writeCSSFile(cssDir.getCanonicalPath(), "lpdiff.css" ) ;
						}
						catch( Exception ee ) {
							ee.printStackTrace() ;
						}
					}
				}
			}
		} );
		fileMenu.add( writDiffMenuItem ); 

		JMenuItem writAllMenuItem = new JMenuItem("Print all") ;
		writAllMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				JLabel labelAll = new JLabel("Directory name:") ;
				Object[] obj = { labelAll, allPanel, cssPanelAll  } ;
				int rtn = JOptionPane.showConfirmDialog(diffFrame, obj,
										"Print difference to HTML file",
										 JOptionPane.OK_CANCEL_OPTION) ;
				try {
					if( rtn == 0 ) {
						try {
							diffFrame.PrintAll( allPanel.fileText.getText(), "lpfiles.css" ) ;
						} catch (java.io.FileNotFoundException fle) {
							System.out.println(fle.toString());
							throw new LpException("Check outputDir is present");
						} catch ( IOException ioe) {
							System.out.println(ioe.toString());
							throw new LpException("Error");
						}
					}
					if( cssPanelAll.cssCB.isSelected() ) {
						writeCSSFile( allPanel.fileText.getText(), "lpfiles.css" ) ;
					}
				} catch (LpException lpE) {
					JOptionPane.showMessageDialog(diffFrame, lpE.toString());
				}
			}
		} );
		fileMenu.add( writAllMenuItem ); 

		fileMenu.addSeparator() ;


		JMenuItem exitMenuItem = new JMenuItem("Exit") ;
		exitMenuItem.addActionListener( new ActionListener() {
			public void actionPerformed( ActionEvent e ) {
				System.exit(0) ;
			}
		} );
		
		fileMenu.add( exitMenuItem ); 
		
		add( fileMenu ) ;
	}

	private void writeCSSFile( String dirName, String cssFileName ) {
		
		try {
			File cssFile = new File( "mlpdiff.css" ) ;
			InputStream is = null ;
			
			if( cssFile.canRead() ) {
				
				is = new FileInputStream( cssFile ) ;
			}
			else {
				JarFile jFile = new JarFile( "LpDiff.jar" ) ;
				ZipEntry ze = jFile.getEntry( cssFileName ) ;
				if( ze == null ) {
					return ;
				}
				is = jFile.getInputStream(ze) ;
	        
			}
			
			BufferedInputStream bis = new BufferedInputStream( is ) ;

			File outCssFile = new File( dirName, cssFileName ) ;
			BufferedOutputStream bos = 
				new BufferedOutputStream( new FileOutputStream( outCssFile ) ) ;


			byte[] b = new byte[1024] ;
			while(true) {
				int size = bis.read( b ) ;
				if( size == -1 ) {
					break ;
				}
				else {
					bos.write( b, 0, size ) ;
				}
			}
			bos.flush() ;
			bos.close() ;
			bis.close() ;
		}
		catch( Exception e ) {
			e.printStackTrace() ;
		}
		
	}


}

class DirSelectPanel extends JPanel implements ActionListener {

	JFrame pFrame = null ;

	private String title = null ; ;
	public JTextField label = new JTextField() ;
	public JTextField text = new JTextField() ;
	private JButton choose = new JButton("choose") ;
	
	public DirSelectPanel( JFrame frame, String newTitle, String newLabel ) {
	
		super() ;
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) ) ;
		pFrame = frame ;
		title = newTitle ;
		
		add( new JLabel(" Label: ") ) ;
		
		label.setText( newLabel ) ;
		label.setColumns( 10 ) ;
		add( label ) ;
		
		add( new JLabel("  Dir: ") ) ;
		
		text.setText( System.getProperty( "user.dir" ) ) ;
		text.setColumns( 20 ) ;
		add( text ) ;
		
		setBorder( new TitledBorder(title) ) ;
		
		choose.addActionListener( this ) ;
		
		add( choose ) ;
		//setSize( 100,200 ) ;
		
	}
	
	public  void actionPerformed( ActionEvent e ) {
		JFileChooser dirDialog = new JFileChooser( text.toString() ) ;
		dirDialog.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY ) ;
		int rtn = dirDialog.showOpenDialog(pFrame);
		if( rtn == JFileChooser.APPROVE_OPTION ) {
			text.setText( dirDialog.getSelectedFile().getPath() ) ;
		}
	}

	public String getDir() {
		return text.getText() ;
	}

	public String getLabel() {
		return label.getText() ;
	}
	

}

class PrintAllPanel extends JPanel implements ActionListener {

	public JTextField fileText = new JTextField() ;
	private JButton choose = new JButton("choose") ;
	
	public PrintAllPanel() {
	
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) ) ;
		
		fileText.setText( System.getProperty( "user.dir" ) ) ;
		fileText.setColumns( 20 ) ;
		add( fileText ) ;
		
		choose.addActionListener( this) ;
		add( choose ) ;
		
		//setBorder( new TitledBorder("HTML file") ) ;

	}
	
	public  void actionPerformed( ActionEvent e ) {
		JFileChooser dirDialog = new JFileChooser( fileText.toString() ) ;
		dirDialog.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY ) ;
		int rtn = dirDialog.showSaveDialog(this);
		if( rtn == JFileChooser.APPROVE_OPTION ) {
			fileText.setText( dirDialog.getSelectedFile().getPath() ) ;
		}
	}
	
}

class PrintDiffPanel extends JPanel implements ActionListener {

	public JTextField fileText = new JTextField() ;
	private JButton choose = new JButton("choose") ;
	
	public PrintDiffPanel() {
	
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) ) ;
		
		File defaultFile = new File( System.getProperty( "user.dir" ), "lpdiff.html" );
		try {
			fileText.setText( defaultFile.getCanonicalPath() ) ;
		}
		catch( Exception ioe ) {
			ioe.printStackTrace() ;
		}
		
		fileText.setColumns( 20 ) ;
		add( fileText ) ;
		
		choose.addActionListener( this) ;
		add( choose ) ;
		
		//setBorder( new TitledBorder("HTML file") ) ;

	}
	
	public  void actionPerformed( ActionEvent e ) {
		JFileChooser dirDialog = new JFileChooser( fileText.toString() ) ;
		dirDialog.setFileSelectionMode( JFileChooser.FILES_ONLY ) ;
		int rtn = dirDialog.showSaveDialog(this);
		if( rtn == JFileChooser.APPROVE_OPTION ) {
			fileText.setText( dirDialog.getSelectedFile().getPath() ) ;
		}
	}
	
}


class PrintCssPanel extends JPanel {

	public JCheckBox cssCB = new JCheckBox("Write default CSS.") ;
	//public JTextField cssName = new JTextField("mlpdiff.css") ;
	
	public PrintCssPanel() {
	
		//super() ;
		setLayout( new BoxLayout( this, BoxLayout.X_AXIS) ) ;
		
		//cssName.setColumns( 10 ) ;
		//add( cssName ) ;
		
		add( cssCB ) ;
		
		//setBorder( new TitledBorder("CSS file") ) ;
		
	}
}


