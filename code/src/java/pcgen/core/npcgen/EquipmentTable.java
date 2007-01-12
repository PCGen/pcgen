package pcgen.core.npcgen;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pcgen.core.Equipment;
import pcgen.util.Logging;

public class EquipmentTable extends Table
{
	private static HashMap<String, EquipmentTable> theTables = null;

	public EquipmentTable( final String anId )
	{
		super( anId );
	}
	
	public List<Equipment> getEquipment()
	{
		final List<Equipment> ret = new ArrayList<Equipment>();
		
		final TableEntry entry = getEntry();
		Logging.debugPrint("Table: " + this + " -> " + entry);
		final List<Object> items = entry.getData();
		for ( final Object item : items )
		{
			final EquipmentItem eqItem = (EquipmentItem)item;
			ret.addAll(eqItem.getEquipment());
		}
		return ret;
	}
	
	public static EquipmentTable get( final String anId )
	{ 
		if ( theTables == null )
		{
			return null;
		}
		return theTables.get( anId );
	}
	
	public static void addTable( final EquipmentTable aTable )
	{
		if ( theTables == null )
		{
			theTables = new HashMap<String, EquipmentTable>();
		}
		theTables.put( aTable.getId(), aTable );
	}
}
