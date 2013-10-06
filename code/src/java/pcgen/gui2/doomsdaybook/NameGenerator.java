/*
 * NameGenerator.java
 *
 * Created on April 25, 2003, 4:37 PM
 */
package pcgen.gui2.doomsdaybook;

import pcgen.gui2.dialog.RandomNameDialog;

/**
 * A standalone window to run the name generator. It is not used directly by 
 * PCGen but instead can be invoked on its own from the command line.
 * 
 * @author  devon
 * @version $Revision$
 */
@SuppressWarnings("serial")
public class NameGenerator extends javax.swing.JFrame
{
	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args)
	{
		RandomNameDialog dialog = new RandomNameDialog(null, null);
		dialog.setVisible(true);
		System.exit(0);
	}
}
