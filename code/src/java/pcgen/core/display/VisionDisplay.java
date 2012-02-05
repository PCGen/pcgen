package pcgen.core.display;

import java.util.Collection;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.core.PlayerCharacter;
import pcgen.core.Vision;

public class VisionDisplay
{

	public static String getVision(final PlayerCharacter aPC, CDOMObject cdo)
	{
		if (aPC == null)
		{
			return "";
		}
		Collection<CDOMReference<Vision>> mods = cdo.getListMods(Vision.VISIONLIST);
		if (mods == null)
		{
			return "";
		}
	
		StringBuilder visionString = new StringBuilder(25);
		for (CDOMReference<Vision> ref : mods)
		{
			for (Vision v : ref.getContainedObjects())
			{
				if (visionString.length() > 0)
				{
					visionString.append(';');
				}
				visionString.append(v.toString(aPC));
			}
		}
	
		return visionString.toString();
	}

	public static String getVision(PlayerCharacter pc)
	{
		final StringBuffer visionString = new StringBuffer();
	
		for (Vision vision : pc.getVisionList())
		{
			if (visionString.length() > 0)
			{
				visionString.append(", ");
			}
	
			visionString.append(vision);
		}
	
		return visionString.toString();
	}

}
