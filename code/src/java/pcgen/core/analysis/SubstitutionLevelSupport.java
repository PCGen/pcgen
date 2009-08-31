package pcgen.core.analysis;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.enumeration.ListKey;
import pcgen.core.Globals;
import pcgen.core.PCClass;
import pcgen.core.PlayerCharacter;
import pcgen.core.SubstitutionClass;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.PCClassLoader;
import pcgen.persistence.lst.SourceEntry;
import pcgen.persistence.lst.utils.DeferredLine;
import pcgen.util.Logging;

public class SubstitutionLevelSupport
{

	public static boolean levelArrayQualifies(final PlayerCharacter pc, final String aLine,
		final SourceEntry tempSource)
	{
		final PCClassLoader classLoader = new PCClassLoader(); 
		 PCClass dummyClass = new PCClass();   
		 
		 try
		{
			classLoader.parseLine(Globals.getContext(), dummyClass, aLine, tempSource);
		}
		catch (PersistenceLayerException e)
		{
			Logging
			.errorPrint("Unable to parse line from levelArray: " + aLine);
		} 
		 return dummyClass.qualifies(pc);
	}

	/**
	 * Apply the level mods to a class
	 * @param aClass
	 */
	public static void applyLevelArrayModsToLevel(SubstitutionClass sc, final PCClass aClass, final int aLevel, final PlayerCharacter aPC)
	{
		List<DeferredLine> levelArray = sc.getListFor(ListKey.SUB_CLASS_LEVEL);
		if (levelArray == null)
		{
			return;
		}
	
		List<DeferredLine> newLevels = new ArrayList<DeferredLine>();
		for (DeferredLine line : levelArray)
		{
			String aLine = line.lstLine;
			final int modLevel = Integer.parseInt(aLine.substring(0, aLine
					.indexOf("\t")));
	
			if (aLevel == modLevel)
			{
				if (levelArrayQualifies(aPC, aLine, line.source))
				{
					newLevels.add(line);
				}
			}
		}
	
		// find all qualifying level lines for this level
		// and put into newLevels list.
		if (!newLevels.isEmpty())
		{
			aPC.setSubstitutionLevel(aClass, sc.getOriginalClassLevel(aLevel));
		}
	}

	public static boolean qualifiesForSubstitutionLevel(SubstitutionClass sc, PlayerCharacter pc, int level) 
	{ 
		List<DeferredLine> levelArray = sc.getListFor(ListKey.SUB_CLASS_LEVEL);
		if (levelArray == null)
		{
			return false;
		}
	
		for (DeferredLine line : levelArray)
		{
			String aLine = line.lstLine;
			final int modLevel = Integer.parseInt(aLine.substring(0, aLine
					.indexOf("\t")));
	
			if (level == modLevel)
			{
				if (!levelArrayQualifies(pc, aLine, line.source))
				{
					return false;
				}
			}
		}
	
		return true;
	}

}
