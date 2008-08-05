package pcgen.core.npcgen;

import java.util.ArrayList;
import java.util.List;

import pcgen.base.util.WeightedCollection;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Equipment;
import pcgen.core.Globals;
import pcgen.util.Logging;
import pcgen.util.PJEP;
import pcgen.util.PjepPool;

public class EquipmentItem 
{
	private Equipment theEquipment = null;
	private List<EqmodItem> theEqMods = null;
	private EquipmentTable theLookupTable = null;
	private WeightedCollection<String> theChoices = null;
	private String theVariableEquipment = null;
	private String theTimes = null;
	private String theQuantity = null;
	
	public List<Equipment> getEquipment()
	{
		final List<Equipment> ret = new ArrayList<Equipment>();
		int numTimes = 1;
		if ( theTimes != null )
		{
			numTimes = getJepValue( theTimes );
			Logging.debugPrint("Rolling " + numTimes + " times");
		}

		for ( int i = 0; i < numTimes; i++ )
		{
			if ( theEquipment == null )
			{
				if ( theLookupTable != null )
				{
					Logging.debugPrint("Getting value from table: " + theLookupTable);
					ret.addAll( theLookupTable.getEquipment() );
				}
				else if ( theChoices != null )
				{
					final String subst = theChoices.getRandomValue();
					Logging.debugPrint("Selected " + subst + " as choice");
					final String equipKey = theVariableEquipment.replaceFirst("%CHOICE", subst); //$NON-NLS-1$
					Logging.debugPrint("\tUsing " + equipKey + " as the equipment");
					Equipment eq = Globals.getContext().ref.silentlyGetConstructedCDOMObject(
							Equipment.class, equipKey);
					eq = eq.clone();
					int quantity = 1;
					if ( theQuantity != null )
					{
						quantity = getJepValue( theQuantity );
					}
					eq.setQty(quantity);
					ret.add( eq );
				}
			}
			else
			{
				Logging.debugPrint("Selecting " + theEquipment);
				final Equipment eq = theEquipment.clone();
				int quantity = 1;
				if ( theQuantity != null )
				{
					quantity = getJepValue( theQuantity );
					Logging.debugPrint("Adding " + quantity + " items");
				}
				eq.setQty(quantity);
				ret.add( eq );
			}
			
			if ( theEqMods != null )
			{
				for ( final Equipment eq : ret )
				{
					for ( final EqmodItem eqmodItem : theEqMods )
					{
						for ( final String eqmod : eqmodItem.getEqMods() )
						{
							Logging.debugPrint("Adding eqmod: " + eqmod);
							eq.addEqModifiers(eqmod, true);
						}
					}
					// We need setBase() called.  The only way to do that is to resize.
					// We will set the size to itself.
					eq.resizeItem(null, eq.getSafe(ObjectKey.SIZE));
					eq.nameItemFromModifiers(null);
				}
			}
		}
		return ret;
	}
	
	public void setEquipment( final Equipment anEquipment )
	{
		theEquipment = anEquipment;
	}
	
	public void addEqMod( final EqmodItem aMod )
	{
		if ( theEqMods == null )
		{
			theEqMods = new ArrayList<EqmodItem>();
		}
		theEqMods.add( aMod );
	}
	
	public void setLookup( final EquipmentTable aTable )
	{
		theLookupTable = aTable;
	}
	
	public void setVariableEquipment( final String anEquipString, final List<String> aList )
	{
		theVariableEquipment = anEquipString;
		theChoices = new WeightedCollection<String>( aList );
	}
	
	public void setTimes( final String aNumberTimes )
	{
		if ( aNumberTimes.indexOf('d') != -1 )
		{
			theTimes = "roll(\"";
		}
		theTimes += aNumberTimes;
		if ( aNumberTimes.indexOf('d') != -1 )
		{
			theTimes += "\")";
		}
		
	}
	
	public void setQuantity( final String aQuantity )
	{
		theQuantity = aQuantity;
	}
	
	private int getJepValue( final String anExpression )
	{
		try
		{
			return Integer.parseInt(anExpression);
		}
		catch ( NumberFormatException e )
		{
			// Do nothing its not a number
		}
		final PJEP parser = PjepPool.getInstance().aquire(this, anExpression);
		parser.parseExpression(anExpression);
		int ret = 0;
		if (parser.hasError())
		{
			Logging.errorPrint("Not a JEP expression: " + anExpression);
		}
		else
		{
			ret = (int)parser.getValue();
		}
		PjepPool.getInstance().release(parser);
		
		return ret;
	}
}
