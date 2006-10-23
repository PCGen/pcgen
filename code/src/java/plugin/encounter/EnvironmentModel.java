/**
 * Created on Mar 16, 2003
 */
package plugin.encounter;

import gmgen.io.ReadXML;
import gmgen.io.VectorTable;
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
	 * Constructor for EnvironmentModel.
	 */
	public EnvironmentModel()
	{
		this("");
	}

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
		File f = new File(dir + File.separator + "encounter_tables/environments.xml");

		this.removeAllElements();

		if (!f.exists())
		{
			Logging.errorPrint("Eek! environments.xml is missing!");

			return;
		}

		reader = new ReadXML(f);
		table = reader.getTable();

		this.addElement("Generic");

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
