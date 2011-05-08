/**
 * Created on Mar 12, 2003
 *
 */
package plugin.encounter;

import pcgen.core.Globals;
import pcgen.core.Race;

import javax.swing.DefaultListModel;

/**
 * @author Jerril
 */
public class RaceModel extends DefaultListModel
{
	/**
	 * Constructor for RaceModel.
	 */
	public RaceModel()
	{
		super();
	}

	/**
	 * Performs an update of the RaceModel.
	 */
	public void update()
	{
		clear();

		for (final Race race : Globals.getContext().ref.getConstructedCDOMObjects(Race.class))
		{
			if (!contains(race.toString()))
			{
				this.addElement(race.toString());
				this.removeElement(Globals.s_EMPTYRACE.toString());
			}
		}
	}
}
