/**
 * Created on Mar 16, 2003
 */
package plugin.encounter;

import gmgen.io.ReadXML;
import gmgen.io.VectorTable;
import pcgen.system.LanguageBundle;
import pcgen.util.Logging;

import javax.swing.DefaultComboBoxModel;
import java.io.File;
import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * @author Jerril
 *
 */
public class EnvironmentModel extends DefaultComboBoxModel
{
	private String dir;

	/**
	 * Constructor
	 * @param parentDir
	 */
	public EnvironmentModel(String parentDir)
	{
		super();
		dir = parentDir;
	}

	/**
	 * Update the model
	 */
	public void update()
	{
		VectorTable table;
		ReadXML reader;
		File f = new File(dir, "environments.xml"); //$NON-NLS-1$

		this.removeAllElements();

		if (!f.exists())
		{
			// TODO Make it so that the view also indicate that the file is missing.
			Logging.errorPrintLocalised("in_plugin_encounter_error_missing", f); //$NON-NLS-1$
			
			return;
		}

		reader = new ReadXML(f);
		table = reader.getTable();

		this.addElement(LanguageBundle.getString("in_plugin_encounter_generic")); //$NON-NLS-1$

		for (int x = 1; x < table.sizeY(); x++)
		{
			try
			{
				this.addElement(((Vector) table.elementAt(x)).firstElement());
			}
			catch (NoSuchElementException e)
			{
				break;
			}
		}
	}
}
