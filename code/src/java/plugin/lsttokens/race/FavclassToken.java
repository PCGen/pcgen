package plugin.lsttokens.race;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.StringTokenizer;
import java.util.TreeSet;

import pcgen.base.formula.Formula;
import pcgen.base.lang.StringUtil;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Category;
import pcgen.cdom.base.ChoiceSet;
import pcgen.cdom.base.Constants;
import pcgen.cdom.base.PrimitiveChoiceSet;
import pcgen.cdom.base.TransitionChoice;
import pcgen.cdom.choiceset.ClassReferenceChoiceSet;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.enumeration.SubClassCategory;
import pcgen.cdom.reference.CategorizedCDOMReference;
import pcgen.core.PCClass;
import pcgen.core.Race;
import pcgen.core.SubClass;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with FAVCLASS Token
 */
public class FavclassToken extends AbstractToken implements
		CDOMPrimaryToken<Race>
{
	public static final Class<PCClass> PCCLASS_CLASS = PCClass.class;
	public static final Class<SubClass> SUBCLASS_CLASS = SubClass.class;

	@Override
	public String getTokenName()
	{
		return "FAVCLASS";
	}

	public boolean parse(LoadContext context, Race race, String value)
	{
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		if (value.startsWith(Constants.LST_CHOOSE))
		{
			return parseFavoredChoose(context, race, value.substring(7));
		}
		return parseFavoredClass(context, race, value);
	}

	private boolean parseFavoredChoose(LoadContext context, Race race,
			String value)
	{
		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		List<CDOMReference<? extends PCClass>> refList = new ArrayList<CDOMReference<? extends PCClass>>();
		while (tok.hasMoreTokens())
		{
			CDOMReference<? extends PCClass> ref;
			String token = tok.nextToken();
			if (Constants.LST_ALL.equalsIgnoreCase(token)
					|| Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				ref = context.ref.getCDOMAllReference(PCCLASS_CLASS);
			}
			else
			{
				foundOther = true;
				int dotLoc = token.indexOf('.');
				if (dotLoc == -1)
				{
					// Primitive
					ref = context.ref.getCDOMReference(PCCLASS_CLASS, token);
				}
				else
				{
					// SubClass
					String parent = token.substring(0, dotLoc);
					String subclass = token.substring(dotLoc + 1);
					SubClassCategory scc = SubClassCategory.getConstant(parent);
					ref = context.ref.getCDOMReference(SUBCLASS_CLASS, scc,
							subclass);
				}
			}
			refList.add(ref);
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		PrimitiveChoiceSet<PCClass> rcs = new ClassReferenceChoiceSet(refList);
		ChoiceSet<? extends PCClass> cs = new ChoiceSet<PCClass>(
				getTokenName(), rcs);
		TransitionChoice<PCClass> tc = new TransitionChoice<PCClass>(cs,
				Formula.ONE);
		context.getObjectContext().put(race, ObjectKey.FAVCLASS_CHOICE, tc);
		tc.setTitle("Select favored class");
		tc.setRequired(true);

		return true;
	}

	public boolean parseFavoredClass(LoadContext context, CDOMObject cdo,
			String value)
	{
		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(value, Constants.PIPE);

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if (Constants.LST_ALL.equalsIgnoreCase(token)
					|| Constants.LST_ANY.equalsIgnoreCase(token))
			{
				foundAny = true;
				context.getObjectContext().put(cdo,
						ObjectKey.ANY_FAVORED_CLASS, true);
			}
			else
			{
				CDOMReference<? extends PCClass> ref;
				foundOther = true;
				int dotLoc = token.indexOf('.');
				if (dotLoc == -1)
				{
					// Primitive
					ref = context.ref.getCDOMReference(PCCLASS_CLASS, token);
				}
				else
				{
					// SubClass
					String parent = token.substring(0, dotLoc);
					String subclass = token.substring(dotLoc + 1);
					SubClassCategory scc = SubClassCategory.getConstant(parent);
					ref = context.ref.getCDOMReference(SUBCLASS_CLASS, scc,
							subclass);
				}
				context.getObjectContext().addToList(cdo,
						ListKey.FAVORED_CLASS, ref);
			}
		}
		if (foundAny && foundOther)
		{
			Logging.errorPrint("Non-sensical " + getTokenName()
					+ ": Contains ANY and a specific reference: " + value);
			return false;
		}
		return true;
	}

	public String[] unparse(LoadContext context, Race race)
	{
		Changes<CDOMReference<? extends PCClass>> changes = context
				.getObjectContext().getListChanges(race, ListKey.FAVORED_CLASS);
		if (changes == null || changes.isEmpty())
		{
			return null;
		}
		SortedSet<String> set = new TreeSet<String>();
		for (CDOMReference<? extends PCClass> ref : changes.getAdded())
		{
			Class<? extends PCClass> refClass = ref.getReferenceClass();
			if (SUBCLASS_CLASS.equals(refClass))
			{
				Category<SubClass> parent = ((CategorizedCDOMReference<SubClass>) ref)
						.getCDOMCategory();
				set.add(parent.toString() + "." + ref.getLSTformat());
			}
			else
			{
				set.add(ref.getLSTformat());
			}
		}
		return new String[] { StringUtil.join(set, Constants.PIPE) };
	}

	public Class<Race> getTokenClass()
	{
		return Race.class;
	}
}
