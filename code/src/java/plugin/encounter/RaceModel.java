/**
 * Created on Mar 12, 2003
 *
 * To change this generated comment edit the template variable "filecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of file comments go to
 * Window>Preferences>Java>Code Generation.
 */
package plugin.encounter;

import pcgen.core.Constants;
import pcgen.core.Globals;
import pcgen.core.Race;

import javax.swing.DefaultListModel;
import java.util.Iterator;

/**
 * @author Jerril
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
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

	public void update()
	{
		clear();

		for (Iterator it = Globals.getRaceMap().values().iterator(); it.hasNext();)
		{
			final Race aRace = (Race) it.next();

//TODO: This is the original code. I took a wild guess at what the code was intended to do, and added braces.
//			if (!contains(aRace.toString()));
//				this.addElement(aRace.toString());
//				this.removeElement(Globals.getRaceMap().get(Constants.s_NONESELECTED).toString());
			if (!contains(aRace.toString()))
			{
				this.addElement(aRace.toString());
				this.removeElement(Globals.getRaceMap().get(Constants.s_NONESELECTED).toString());
			}
		}
	}
}
