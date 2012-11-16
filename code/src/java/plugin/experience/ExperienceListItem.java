package plugin.experience;

import gmgen.plugin.Combatant;

/**
 * The <code>ExperienceAdjusterController</code> handles the functionality of
 * the Adjusting of experience.  This class is called by the <code>GMGenSystem
 * </code> and will have it's own model and view.<br>
 * Created on February 26, 2003<br>
 * Updated on February 26, 2003
 * @author  Expires 2003
 * @version 2.10
 */
public class ExperienceListItem
{
	/** Combatant for the List Item */
	protected Combatant cbt;

	/**
	 * Creates a new instance of ExperienceListItem taking in a
	 * Combatant
	 *@param cbt Combatant this item represents
	 */
	public ExperienceListItem(Combatant cbt)
	{
		this.cbt = cbt;
	}

	/**
	 * Get combatant
	 * @return combatant
	 */
	public Combatant getCombatant()
	{
		return cbt;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(cbt.getName() + " (" + cbt.getCR() + ") ");

		if (cbt.getXP() != 0)
		{
			sb.append(cbt.getXP());
		}

		return sb.toString();
	}
}
