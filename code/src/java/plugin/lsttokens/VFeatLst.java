package plugin.lsttokens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.base.util.MapToList;
import pcgen.cdom.base.AssociatedPrereqObject;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.AssociationKey;
import pcgen.cdom.list.AbilityList;
import pcgen.cdom.reference.ReferenceUtilities;
import pcgen.core.Ability;
import pcgen.core.AbilityCategory;
import pcgen.core.AbilityUtilities;
import pcgen.core.Ability.Nature;
import pcgen.core.prereq.Prerequisite;
import pcgen.rules.context.AssociatedChanges;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

public class VFeatLst extends AbstractToken implements
		CDOMPrimaryToken<CDOMObject>
{

	public static final Class<Ability> ABILITY_CLASS = Ability.class;

	@Override
	public String getTokenName()
	{
		return "VFEAT";
	}

	public boolean parse(LoadContext context, CDOMObject obj, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		
		String token = tok.nextToken();

		if (token.startsWith("PRE") || token.startsWith("!PRE"))
		{
			Logging.errorPrint("Cannot have only PRExxx subtoken in "
					+ getTokenName() + ": " + value);
			return false;
		}

		ArrayList<AssociatedPrereqObject> edgeList = new ArrayList<AssociatedPrereqObject>();
		boolean first = true;
		
		AbilityCategory category = AbilityCategory.FEAT;
		Nature nature = Ability.Nature.VIRTUAL;
		CDOMReference<AbilityList> list = Ability.ABILITYLIST;
		while (true)
		{
			if (token.equals(Constants.LST_DOT_CLEAR))
			{
				if (!first)
				{
					Logging.errorPrint("  Non-sensical " + getTokenName()
							+ ": .CLEAR was not the first list item: " + value);
					return false;
				}
				context.getListContext().removeAllFromList(getTokenName(), obj,
						Ability.FEATLIST);
			}
			else
			{
				CDOMReference<Ability> ability = TokenUtilities
						.getTypeOrPrimitive(context, ABILITY_CLASS, category,
								token);
				if (ability == null)
				{
					return false;
				}
				AssociatedPrereqObject assoc = context.getListContext()
						.addToList(getTokenName(), obj, list, ability);
				assoc.setAssociation(AssociationKey.NATURE, nature);
				assoc.setAssociation(AssociationKey.CATEGORY, category);
				if (token.indexOf('(') != -1)
				{
					List<String> choices = new ArrayList<String>();
					AbilityUtilities.getUndecoratedName(token, choices);
					assoc.setAssociation(AssociationKey.ASSOC_CHOICES, choices);
				}
				edgeList.add(assoc);
			}
		
			first = false;
			if (!tok.hasMoreTokens())
			{
				// No prereqs, so we're done
				return true;
			}
			token = tok.nextToken();
			if (token.startsWith("PRE") || token.startsWith("!PRE"))
			{
				break;
			}
		}

		while (true)
		{
			Prerequisite prereq = getPrerequisite(token);
			if (prereq == null)
			{
				Logging.errorPrint("   (Did you put feats after the "
						+ "PRExxx tags in " + getTokenName() + ":?)");
				return false;
			}
			for (AssociatedPrereqObject edge : edgeList)
			{
				edge.addPrerequisite(prereq);
			}
			if (!tok.hasMoreTokens())
			{
				break;
			}
			token = tok.nextToken();
		}

		return true;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		AssociatedChanges<CDOMReference<Ability>> changes = context
				.getListContext().getChangesInList(getTokenName(), obj,
						Ability.FEATLIST);
		MapToList<CDOMReference<Ability>, AssociatedPrereqObject> mtl = changes
				.getAddedAssociations();
		if (mtl == null || mtl.isEmpty())
		{
			// Zero indicates no Token
			return null;
		}
		Collection<CDOMReference<Ability>> added = changes.getAdded();
		Collection<CDOMReference<Ability>> removedItems = changes.getRemoved();
		StringBuilder sb = new StringBuilder();
		if (changes.includesGlobalClear())
		{
			if (removedItems != null && !removedItems.isEmpty())
			{
				context.addWriteMessage("Non-sensical relationship in "
						+ getTokenName()
						+ ": global .CLEAR and local .CLEAR. performed");
				return null;
			}
			sb.append(Constants.LST_DOT_CLEAR);
		}
		else if (removedItems != null && !removedItems.isEmpty())
		{
			context.addWriteMessage(getTokenName() + " does not support "
					+ Constants.LST_DOT_CLEAR_DOT);
			return null;
		}
		if (added != null && !added.isEmpty())
		{
			if (sb.length() != 0)
			{
				sb.append(Constants.PIPE);
			}
			sb.append(ReferenceUtilities.joinLstFormat(added, Constants.PIPE));
		}
		if (sb.length() == 0)
		{
			return null;
		}
		return new String[] { sb.toString() };
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}
}
