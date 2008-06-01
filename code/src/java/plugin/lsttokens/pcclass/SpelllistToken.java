package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMListObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.choiceset.SpellReferenceChoiceSet;
import pcgen.cdom.content.TransitionChoice;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSpellList;
import pcgen.cdom.list.DomainSpellList;
import pcgen.core.PCClass;
import pcgen.core.spell.Spell;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SPELLLIST Token
 */
public class SpelllistToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{

	private static Class<ClassSpellList> SPELLLIST_CLASS = ClassSpellList.class;
	private static Class<DomainSpellList> DOMAINSPELLLIST_CLASS = DomainSpellList.class;

	@Override
	public String getTokenName()
	{
		return "SPELLLIST";
	}

	public boolean parse(LoadContext context, PCClass pcc, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}
		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);
		int count;
		try
		{
			count = Integer.parseInt(tok.nextToken());
			if (count <= 0)
			{
				Logging.addParseMessage(Logging.LST_ERROR, "Number in "
						+ getTokenName() + " must be greater than zero: "
						+ value);
				return false;
			}
		}
		catch (NumberFormatException nfe)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Invalid Number in "
					+ getTokenName() + ": " + value);
			return false;
		}
		if (!tok.hasMoreTokens())
		{
			Logging.addParseMessage(Logging.LST_ERROR, getTokenName()
					+ " must have a | separating "
					+ "count from the list of possible values: " + value);
			return false;
		}
		List<CDOMReference<? extends CDOMListObject>> refs = new ArrayList<CDOMReference<? extends CDOMListObject>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<? extends CDOMListObject> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(SPELLLIST_CLASS);
			}
			else if (token.startsWith("DOMAIN."))
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(DOMAINSPELLLIST_CLASS, token
						.substring(7));
			}
			else
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(SPELLLIST_CLASS, token);
			}
			refs.add(ref);
		}

		if (foundAny && foundOther)
		{
			Logging.addParseMessage(Logging.LST_ERROR, "Non-sensical "
					+ getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}

		PrimitiveChoiceSet<CDOMListObject> rcs = new SpellReferenceChoiceSet(
				refs);
		ChoiceSet<? extends CDOMListObject<Spell>> cs = new ChoiceSet(
				getTokenName(), rcs);
		TransitionChoice<CDOMListObject<Spell>> tc = new TransitionChoice<CDOMListObject<Spell>>(
				cs, count);
		context.getObjectContext().put(pcc, ObjectKey.SPELLLIST_CHOICE, tc);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		TransitionChoice<CDOMListObject<Spell>> grantChanges = context
				.getObjectContext().getObject(pcc, ObjectKey.SPELLLIST_CHOICE);
		if (grantChanges == null)
		{
			// Zero indicates no Token
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(grantChanges.getCount());
		sb.append(Constants.PIPE);
		sb.append(grantChanges.getChoices().getLSTformat());
		return new String[] { sb.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
