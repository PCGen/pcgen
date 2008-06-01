package plugin.lsttokens.pcclass;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.choiceset.ReferenceChoiceSet;
import pcgen.cdom.content.TransitionChoice;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.PCClass;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with SKILLLIST Token
 */
public class SkilllistToken extends AbstractToken implements
		CDOMPrimaryToken<PCClass>
{
	private static Class<ClassSkillList> SKILLLIST_CLASS = ClassSkillList.class;

	@Override
	public String getTokenName()
	{
		return "SKILLLIST";
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
		List<CDOMReference<ClassSkillList>> refs = new ArrayList<CDOMReference<ClassSkillList>>();
		boolean foundAny = false;
		boolean foundOther = false;

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			CDOMReference<ClassSkillList> ref;
			if (Constants.LST_ALL.equals(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(SKILLLIST_CLASS);
			}
			else
			{
				foundOther = true;
				ref = context.ref.getCDOMReference(SKILLLIST_CLASS, token);
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

		ReferenceChoiceSet<ClassSkillList> rcs = new ReferenceChoiceSet<ClassSkillList>(
				refs);
		ChoiceSet<ClassSkillList> cs = new ChoiceSet<ClassSkillList>(
				getTokenName(), rcs);
		TransitionChoice<ClassSkillList> tc = new TransitionChoice<ClassSkillList>(
				cs, count);
		context.getObjectContext().put(pcc, ObjectKey.SKILLLIST_CHOICE, tc);
		return true;
	}

	public String[] unparse(LoadContext context, PCClass pcc)
	{
		TransitionChoice<ClassSkillList> grantChanges = context
				.getObjectContext().getObject(pcc, ObjectKey.SKILLLIST_CHOICE);
		if (grantChanges == null)
		{
			// Zero indicates no Token
			return null;
		}
		StringBuilder sb = new StringBuilder();
		sb.append(grantChanges.getCount());
		sb.append(Constants.PIPE);
		sb.append(grantChanges.getChoices().getLSTformat().replaceAll(
				Constants.COMMA, Constants.PIPE));
		return new String[] { sb.toString() };
	}

	public Class<PCClass> getTokenClass()
	{
		return PCClass.class;
	}
}
