/*
 * This file is Open Game Content, covered by the OGL.
 */
package gmgen.plugin;

/**
 * Deals with the iniative part of the GMGen plugin 
 */
public class SystemInitiative
{
	protected Dice die;
	protected SystemAttribute attribute;
	protected int bonus;
	protected int currentInitiative = 0;
	protected int mod = 0;
	protected int roll;

	/**
	 * Constructor
	 * @param attribute
	 * @param bonus
	 */
	public SystemInitiative(SystemAttribute attribute, int bonus)
	{
		this.attribute = attribute;
		this.bonus = bonus;
		die = new Dice(1, 20);
	}

	/**
	 * Constructor
	 * @param attribute
	 */
	public SystemInitiative(SystemAttribute attribute)
	{
		this(attribute, 0);
	}

	/**
	 * Constructor
	 * @param bonus
	 */
	public SystemInitiative(int bonus)
	{
		this(new SystemAttribute("Attribute", 10), bonus);
	}

	/**
	 * Constructor
	 */
	public SystemInitiative()
	{
		this(new SystemAttribute("Attribute", 10), 0);
	}

	/**
	 * Set the attribute
	 * @param attribute
	 */
	public void setAttribute(SystemAttribute attribute)
	{
		this.attribute = attribute;
	}

	/**
	 * Get the attribute
	 * @return the attribute
	 */
	public SystemAttribute getAttribute()
	{
		return attribute;
	}

	/**
	 * Set the bonus for the initiative
	 * @param bonus
	 */
	public void setBonus(int bonus)
	{
		this.bonus = bonus - attribute.getModifier();
		if(getCurrentInitiative() > 0) {
			setCurrentInitiative(roll + getModifier() + mod);
		}
	}

	/**
	 * Get the bonus to the initiative
	 * @return the bonus to the initiative
	 */
	public int getBonus()
	{
		return bonus;
	}

	/**
	 * Reset the current initiative to 0
	 */
	public void resetCurrentInitiative()
	{
		currentInitiative = 0;
	}
	
	/**
	 * Set the current initiative
	 * @param currentInitiative
	 */
	public void setCurrentInitiative(int currentInitiative)
	{
		if (currentInitiative >= 1)
		{
			this.currentInitiative = currentInitiative;
		}
		else
		{
			this.currentInitiative = 1;
		}
	}

	/**
	 * Get the current initiative
	 * @return the current initiative
	 */
	public int getCurrentInitiative()
	{
		return currentInitiative;
	}

	/**
	 * Get the modifier for the initiative
	 * @return the modifier for the initiative
	 */
	public int getModifier()
	{
		return attribute.getModifier() + bonus;
	}

	/**
	 * Set the new curent initiative and return it
	 * @return the new curent initiative
	 */
	public int check()
	{
		return check(0);
	}

	/**
	 * Set the new curent initiative and return it
	 * @param modifier
	 * @return the new curent initiative
	 */
	public int check(int modifier)
	{
		roll = die.roll();
		this.mod = modifier;
		setCurrentInitiative(roll + getModifier() + modifier);

		return currentInitiative;
	}

	/**
	 * Set the new curent initiative and return it
	 * @param aRoll
	 * @return the new curent initiative
	 */
	public int checkExtRoll(int aRoll)
	{
		return checkExtRoll(aRoll, 0);
	}

	/**
	 * Set the new curent initiative and return it
	 * @param aRoll
	 * @param modifier
	 * @return the new curent initiative
	 */
	public int checkExtRoll(int aRoll, int modifier)
	{
		this.roll = aRoll;
		this.mod = modifier;
		setCurrentInitiative(aRoll + getModifier() + modifier);

		return currentInitiative;
	}

	/**
	 * Refocus with no modifier
	 * @return current initiative
	 */
	public int refocus()
	{
		return checkExtRoll(20, 0);
	}

	/**
	 * Refocus with a modifier, (calls checkExtRoll(modifer))
	 * @param modifier
	 * @return current initiative
	 */
	public int refocus(int modifier)
	{
		return checkExtRoll(20, modifier);
	}
}
