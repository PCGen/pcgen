package pcgen.core.npcgen;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import pcgen.core.GameMode;
import pcgen.core.SettingsHandler;
import pcgen.util.Logging;

public class TreasureGenerator 
{
	private static TreasureGenerator theInstance = new TreasureGenerator();
	
	private static HashMap<GameMode, List<EquipmentTable>> theTreasureTables = new HashMap<GameMode,List<EquipmentTable>>();
	
	private static File tablesDir = new File(SettingsHandler.getPcgenSystemDir()
			+ File.separator + "npcgen"  //$NON-NLS-1$ 
			+ File.separator + "treasure"); //$NON-NLS-1$

//	private static File tablesDir = new File(Globals.getDefaultPath() 
//		+ File.separator + "system" //$NON-NLS-1$
//		+ File.separator + "npcgen"  //$NON-NLS-1$ 
//		+ File.separator + "treasure"); //$NON-NLS-1$

	private TreasureGenerator()
	{
		// Private so it can't be constructed.
	}
	
	public static TreasureGenerator getInstance()
	{
		return theInstance;
	}
	
	public List<EquipmentTable> getTables( final GameMode aMode )
	{
		List<EquipmentTable> tables = theTreasureTables.get( aMode );
		
		if ( tables == null )
		{
			try
			{
				final EquipmentTableParser parser = new EquipmentTableParser( aMode );
				final File[] fileNames = tablesDir.listFiles(new FilenameFilter() {
                    @Override
					public boolean accept(final File aDir, final String aName)
					{
						if (aName.toLowerCase().endsWith(".xml")) //$NON-NLS-1$
						{
							return true;
						}
						return false;
					}
				});
		
				tables = new ArrayList<EquipmentTable>();
				tables.addAll(parser.parse(fileNames));
				theTreasureTables.put( aMode, tables );
				return tables;
			}
			catch (Exception ex)
			{
				Logging.errorPrint( "Error loading tables", ex );
			}
		}
		return tables;
	}
	
	public static void addTable( final GameMode aMode, final EquipmentTable aTable )
	{
		List<EquipmentTable> tables = theTreasureTables.get( aMode );
		if ( tables == null )
		{
			tables = new ArrayList<EquipmentTable>();
			theTreasureTables.put( aMode , tables );
		}
		tables.add( aTable );
	}
}
