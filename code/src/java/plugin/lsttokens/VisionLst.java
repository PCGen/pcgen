/*
 * Created on Sep 2, 2005
 *
 */
package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.lang.StringUtil;
import pcgen.base.util.HashMapToList;
import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Vision;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * <code>VisionLst</code> handles the processing of the VISION tag in LST
 * code.
 * 
 * Last Editor: $Author$ Last Edited: $Date: 2008-06-15 22:14:51 -0400
 * (Sun, 15 Jun 2008) $
 * 
 * @author Devon Jones
 * @version $Revision$
 */
public class VisionLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	/**
	 * @see pcgen.persistence.lst.LstToken#getTokenName()
	 */
	@Override
	public String getTokenName()
	{
		return "VISION";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		StringTokenizer aTok = new StringTokenizer(value, Constants.PIPE);

		List<Vision> list = new ArrayList<Vision>();
		while (aTok.hasMoreTokens())
		{
			String visionString = aTok.nextToken();

			if (".CLEAR".equals(visionString))
			{
				context.getListContext().removeAllFromList(getTokenName(), obj,
						Vision.VISIONLIST);
				continue;
			}

			if (visionString.startsWith(".CLEAR."))
			{
				try
				{
					Vision vis = Vision.getVision(visionString.substring(7));
					context.getListContext().removeFromList(getTokenName(),
							obj, Vision.VISIONLIST,
							new CDOMDirectSingleRef<Vision>(vis));
				}
				catch (IllegalArgumentException e)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"Bad Syntax for Cleared Vision in "
									+ getTokenName());
					Logging.addParseMessage(Logging.LST_ERROR, e.getMessage());
					return false;
				}
			}
			else
			{
				if (visionString.startsWith(".SET."))
				{
					// TODO Need a deprecation warning here
					context.getListContext().removeAllFromList(getTokenName(),
							obj, Vision.VISIONLIST);
					visionString = visionString.substring(5);
				}
				try
				{
					list.add(Vision.getVision(visionString));
				}
				catch (IllegalArgumentException e)
				{
					Logging.addParseMessage(Logging.LST_ERROR,
							"Bad Syntax for Vision in " + getTokenName());
					Logging.addParseMessage(Logging.LST_ERROR, e.getMessage());
					return false;
				}
			}
		}
		for (Vision vis : list)
		{
			context.getListContext().addToList(getTokenName(), obj,
					Vision.VISIONLIST, new CDOMDirectSingleRef<Vision>(vis));
		}
		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<CDOMReference<Vision>> changes = context
				.getListContext().getChangesInList(getTokenName(), obj,
						Vision.VISIONLIST);
		List<String> list = new ArrayList<String>();
		Collection<CDOMReference<Vision>> removedItems = changes.getRemoved();
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			list.add(Constants.LST_DOT_CLEAR);
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			list.add(Constants.LST_DOT_CLEAR_DOT
					+ ReferenceUtilities
							.joinLstFormat(removedItems, ",.CLEAR."));
		}
		MapToList<CDOMReference<Vision>, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (mtl != null && !mtl.isEmpty())
		{
			MapToList<Set<Prerequisite>, Vision> m = new HashMapToList<Set<Prerequisite>, Vision>();
			for (CDOMReference<Vision> ab : mtl.getKeySet())
			{
				for (AssociatedPrereqObject assoc : mtl.getListFor(ab))
				{
					m.addAllToListFor(new HashSet<Prerequisite>(assoc
							.getPrerequisiteList()), ab.getContainedObjects());
				}
			}
			Set<String> set = new TreeSet<String>();
			for (Set<Prerequisite> prereqs : m.getKeySet())
			{
				StringBuilder sb = new StringBuilder(StringUtil.join(m
						.getListFor(prereqs), Constants.PIPE));
				if (prereqs != null && !prereqs.isEmpty())
				{
					sb.append(Constants.PIPE);
					sb.append(getPrerequisiteString(context, prereqs));
				}
				set.add(sb.toString());
			}
			list.addAll(set);
		}
		if (list.isEmpty())
		{
			return null;
		}
		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
