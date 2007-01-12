package pcgen.core.npcgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EqmodTable extends Table
{
	private static HashMap<String, EqmodTable> theTables = null;

	public EqmodTable( final String anId )
	{
		super( anId );
	}
	
	public List<String> getEqMod()
	{
		final List<String> ret = new ArrayList<String>();
		
		final TableEntry entry = getEntry();
		final List<Object> items = entry.getData();
		for ( final Object item : items )
		{
			final EqmodItem eqItem = (EqmodItem)item;
			ret.addAll(eqItem.getEqMods());
		}
		
		return ret;
	}
	
	public static EqmodTable get( final String anId )
	{
		if ( theTables == null )
		{
			return null;
		}
		return theTables.get( anId );
	}

	public static void addTable( final EqmodTable aTable )
	{
		if ( theTables == null )
		{
			theTables = new HashMap<String, EqmodTable>();
		}
		theTables.put( aTable.getId(), aTable );
	}
}
