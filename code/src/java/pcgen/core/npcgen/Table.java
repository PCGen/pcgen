package pcgen.core.npcgen;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.base.Constants;

public class Table 
{
	private WeightedCollection<TableEntry> theData = new WeightedCollection<TableEntry>();
	
	private String theId;
	private String theName = Constants.EMPTY_STRING;

	public Table( final String anId )
	{
		theId = anId;
	}

	public void setName( final String aName )
	{
		theName = aName;
	}
	
	public String getId()
	{
		return theId;
	}
	
	public TableEntry getEntry()
	{
		return theData.getRandomValue();
	}
	
	public void add( final int aWeight, final TableEntry anEntry )
	{
		theData.add(anEntry, aWeight);
	}
	
	@Override
	public String toString()
	{
		return theName;
	}
}
