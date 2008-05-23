package plugin.lsttokens.skill;

import java.util.Collection;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.list.ClassSkillList;
import pcgen.core.Skill;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.util.Logging;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken extends AbstractToken implements
		CDOMPrimaryToken<Skill>
{

	private static final Class<ClassSkillList> SKILLLIST_CLASS = ClassSkillList.class;

	@Override
	public String getTokenName()
	{
		return "CLASSES";
	}

	public boolean parse(LoadContext context, Skill skill, String value)
	{
		if (Constants.LST_ALL.equals(value))
		{
			addSkillAllowed(context, skill, context.ref
					.getCDOMAllReference(SKILLLIST_CLASS));
			return true;
		}
		if (isEmpty(value) || hasIllegalSeparator('|', value))
		{
			return false;
		}

		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		boolean added = false;

		while (pipeTok.hasMoreTokens())
		{
			String className = pipeTok.nextToken();
			if (Constants.LST_ALL.equals(className))
			{
				if (added)
				{
					Logging.errorPrint("Non-sensical Skill " + getTokenName()
							+ ": Contains ALL after a specific reference: "
							+ value);
					return false;
				}
				addSkillAllowed(context, skill, context.ref
						.getCDOMAllReference(SKILLLIST_CLASS));
				break;
			}
			if (className.startsWith("!"))
			{
				Logging.errorPrint("Non-sensical Skill " + getTokenName()
						+ ": Contains ! without (or before) ALL: " + value);
				return false;
			}
			addSkillAllowed(context, skill, context.ref.getCDOMReference(
					SKILLLIST_CLASS, className));
			added = true;
		}
		while (pipeTok.hasMoreTokens())
		{
			String className = pipeTok.nextToken();
			if (className.startsWith("!"))
			{
				String clString = className.substring(1);
				if (Constants.LST_ALL.equals(clString)
						|| Constants.LST_ANY.equals(clString))
				{
					Logging.errorPrint("Invalid " + getTokenName()
							+ " cannot use !ALL");
					return false;
				}
				addSkillNotAllowed(context, skill, context.ref
						.getCDOMReference(SKILLLIST_CLASS, clString));
			}
			else
			{
				Logging.errorPrint("Non-sensical Skill " + getTokenName()
						+ ": Contains ALL and a specific reference: " + value);
				return false;
			}
		}
		return true;
	}

	private void addSkillAllowed(LoadContext context, Skill skill,
			CDOMReference<ClassSkillList> ref)
	{
		context.obj.addToList(skill, ListKey.CLASSES, ref);
	}

	private void addSkillNotAllowed(LoadContext context, Skill skill,
			CDOMReference<ClassSkillList> ref)
	{
		context.obj.addToList(skill, ListKey.PREVENTED_CLASSES, ref);
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		Changes<CDOMReference<ClassSkillList>> masterChanges = context.obj
				.getListChanges(skill, ListKey.CLASSES);
		Changes<CDOMReference<ClassSkillList>> removedChanges = context.obj
				.getListChanges(skill, ListKey.PREVENTED_CLASSES);
		if (masterChanges.includesGlobalClear()
				|| removedChanges.includesGlobalClear())
		{
			context
					.addWriteMessage(getTokenName()
							+ " does not support .CLEAR");
			return null;
		}
		if (masterChanges.hasRemovedItems() || removedChanges.hasRemovedItems())
		{
			context.addWriteMessage(getTokenName()
					+ " does not support .CLEAR.");
			return null;
		}
		Collection<CDOMReference<ClassSkillList>> added = masterChanges
				.getAdded();
		Collection<CDOMReference<ClassSkillList>> prevented = removedChanges
				.getAdded();
		StringBuilder sb = new StringBuilder();
		if (added == null
				|| added.isEmpty())
		{
			if (prevented == null || prevented.isEmpty())
			{
				// That's fine - nothing to do
				return null;
			}
		}
		if (added.size() == 1
				&& added.contains(context.ref
						.getCDOMAllReference(SKILLLIST_CLASS)))
		{
			sb.append("ALL");
			if (prevented != null && !prevented.isEmpty())
			{
				for (CDOMReference<ClassSkillList> ref : prevented)
				{
					sb.append("|!");
					sb.append(ref.getLSTformat());
				}
			}
		}
		else
		{
			if (prevented != null && !prevented.isEmpty())
			{
				context.addWriteMessage("Non-sensical " + getTokenName()
						+ ": has both addition and removal");
				return null;
			}
			boolean needBar = false;
			if (added.size() > 1
					&& added.contains(context.ref
							.getCDOMAllReference(SKILLLIST_CLASS)))
			{
				context.addWriteMessage("All SkillList Reference was "
						+ "attached to " + skill.getDisplayName()
						+ " by Token " + getTokenName()
						+ " but there are also " + "other references granting "
						+ skill.getDisplayName() + " as a Class Skill.  "
						+ "This is non-sensical");
				return null;
			}
			for (CDOMReference<ClassSkillList> ref : added)
			{
				if (needBar)
				{
					sb.append(Constants.PIPE);
				}
				sb.append(ref.getLSTformat());
				needBar = true;
			}
		}
		return new String[] { sb.toString() };
	}

	public Class<Skill> getTokenClass()
	{
		return Skill.class;
	}
}
