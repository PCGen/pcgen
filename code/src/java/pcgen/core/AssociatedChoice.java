package pcgen.core;

import java.util.Collection;
import java.util.HashMap;

public class AssociatedChoice <T extends Comparable> implements Comparable
{
	protected HashMap<String, T> choices = new HashMap<String, T>();

	public static final String DEFAULT_KEY = "CHOICE";

	/**
	 * Empty constructor.  No choices are associated yet.
	 */
	public AssociatedChoice()
	{
	}

	/**
	 * Constructs a simple choice object with a single choice
	 * @param aChoice The selected item to associate
	 */
	public AssociatedChoice( final T aChoice )
	{
		// Convience method to add just a single choice
		choices.put( DEFAULT_KEY, aChoice );
	}

	/**
	 * Adds a list of choices keyed by the order returned by the collection's
	 * iterator.
	 * @param aGroup A list of choices to associate
	 */
	public AssociatedChoice( final Collection<T> aGroup )
	{
		int count = 0;
		for ( T val : aGroup )
		{
			choices.put( String.valueOf(++count), val );
		}
	}

	/**
	 * Construct a choice with a specific key
	 * @param aKey Key used to reference this choice option
	 * @param aChoice The value to associate with this choice key
	 */
	public AssociatedChoice( final String aKey, final T aChoice )
	{
		choices.put( aKey, aChoice );
	}

	/**
	 * Adds a choice to the default key
	 * @param aChoice The value to associate
	 */
	public void addChoice( final T aChoice )
	{
		choices.put( DEFAULT_KEY, aChoice );
	}

	/**
	 * Adds a choice with the specified key
	 * @param aKey Key to use for this choice
	 * @param aChoice Value of the choice
	 */
	public void addChoice( final String aKey, final T aChoice )
	{
		choices.put( aKey, aChoice );
	}

	/**
	 * Get the choice for the specified key
	 * @param aKey Key to retreive choice for
	 * @return The choice associated with the specified key
	 */
	public T getChoice( final String aKey )
	{
		return choices.get( aKey );
	}

	public T getDefaultChoice()
	{
		return choices.get( DEFAULT_KEY );
	}

	public Collection<T> getChoices()
	{
		return choices.values();
	}

	public boolean remove( final String aKey )
	{
		return choices.remove( aKey ) == null ? false : true;
	}

	public boolean removeDefaultChoice( final T aChoice )
	{
		T result = choices.get( DEFAULT_KEY );
		if ( result != null )
		{
			if ( result.equals( aChoice ) )
			{
				choices.remove( DEFAULT_KEY );
				return true;
			}
		}
		return false;
	}

	/**
	 * Returns the number of choices made
	 * @return Number of choices
	 */
	public int size()
	{
		return choices.size();
	}

	public int compareTo(Object o)
	{
		AssociatedChoice<T> other = (AssociatedChoice<T>)o;

		T defaultValue = getDefaultChoice();
		if ( defaultValue != null )
		{
			T otherDefault = other.getDefaultChoice();
			if ( otherDefault != null )
			{
				return defaultValue.compareTo(otherDefault);
			}
		}
		return 1;
	}
}
