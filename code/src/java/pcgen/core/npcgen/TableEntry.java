package pcgen.core.npcgen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TableEntry
{
	private String theName;
	private List<Object> theData = new ArrayList<Object>();
	
	public TableEntry( final String aName )
	{
		theName = aName;
	}

	public void addData( final Object anItem )
	{
		theData.add( anItem );
	}
	
	public List<Object> getData()
	{
		return Collections.unmodifiableList(theData);
	}
	
	@Override
	public String toString()
	{
		return theName;
	}
}
