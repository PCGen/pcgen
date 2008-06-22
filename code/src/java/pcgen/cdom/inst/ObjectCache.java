package pcgen.cdom.inst;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.enumeration.ListKey;
import pcgen.core.PlayerCharacter;
import pcgen.core.Vision;
import pcgen.core.prereq.PrereqHandler;
import pcgen.util.enumeration.VisionType;

public class ObjectCache extends CDOMObject
{

	@Override
	public boolean isType(String str)
	{
		return false;
	}

	public void initializeListFor(ListKey<?> lk)
	{
		listChar.initializeListFor(lk);
	}

	public void initializeVisionCache(PlayerCharacter pc)
	{
		initializeListFor(ListKey.VISION_CACHE);
		Map<VisionType, Integer> map = new HashMap<VisionType, Integer>();
		for (CDOMObject cdo : pc.getCDOMObjectList())
		{
			Collection<CDOMReference<Vision>> mods = cdo
					.getListMods(Vision.VISIONLIST);
			if (mods == null)
			{
				continue;
			}
			for (CDOMReference<Vision> ref : mods)
			{
				Collection<AssociatedPrereqObject> assoc = cdo
						.getListAssociations(Vision.VISIONLIST, ref);
				for (AssociatedPrereqObject apo : assoc)
				{
					if (PrereqHandler.passesAll(apo.getPrerequisiteList(), pc,
							null))
					{
						for (Vision v : ref.getContainedObjects())
						{
							VisionType visType = v.getType();
							int a = pc.getVariableValue(v.getDistance(), "")
									.intValue();
							Integer current = map.get(visType);
							if (current == null || current < a)
							{
								map.put(visType, a);
							}
						}
					}
				}
			}
		}

		/*
		 * parse through the global list of vision tags and see if this PC has
		 * any BONUS:VISION tags which will create a new visionMap entry, and
		 * add any BONUS to existing entries in the map
		 */
		for (VisionType vType : VisionType.getAllVisionTypes())
		{
			final int aVal = (int) pc.getTotalBonusTo("VISION", vType
					.toString());

			if (aVal > 0)
			{
				Integer current = map.get(vType);
				map.put(vType, aVal + (current == null ? 0 : current));
			}
		}
		TreeSet<Vision> set = new TreeSet<Vision>();
		for (Map.Entry<VisionType, Integer> me : map.entrySet())
		{
			set.add(new Vision(me.getKey(), me.getValue().toString()));
		}
		addAllToListFor(ListKey.VISION_CACHE, set);
	}

}
