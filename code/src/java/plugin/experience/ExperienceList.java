package plugin.experience;

import gmgen.plugin.Combatant;

import javax.swing.DefaultListModel;

/**
 * ExperienceList. This class holds all the characters that are to be displayed in a JList.
 */
public class ExperienceList extends DefaultListModel
{
	/**
	 * Creates an instance of a <code>ExperienceList</code>.  This class holds
	 * all the characters that are to be displayed in a JList.
	 */
	public ExperienceList()
	{
		super();
	}

	/**
	 * Gets the average level for the party.
	 * @return the average party level.
	 */
	public int averageCR()
	{
		float groupLevel = 0;
		int num = 0;

		for (int i = 0; i < size(); i++)
		{
			Combatant cbt = ((ExperienceListItem) get(i)).getCombatant();
			groupLevel += cbt.getCR();
			num++;
		}

		if (num == 0)
		{
			return 0;
		}

		return ((int) groupLevel) / num;
	}
}
