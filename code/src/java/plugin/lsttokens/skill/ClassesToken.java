/*
 * Copyright (c) 2008 Tom Parker <thpr@users.sourceforge.net>
 * 
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
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
import pcgen.rules.persistence.token.AbstractTokenWithSeparator;
import pcgen.rules.persistence.token.CDOMPrimaryParserToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with CLASSES Token
 */
public class ClassesToken extends AbstractTokenWithSeparator<Skill> implements
		CDOMPrimaryParserToken<Skill>
{

	private static final Class<ClassSkillList> SKILLLIST_CLASS = ClassSkillList.class;

	@Override
	public String getTokenName()
	{
		return "CLASSES";
	}
	
	@Override
	public ParseResult parseToken(LoadContext context, Skill skill, String value)
	{
		if (Constants.LST_ALL.equals(value))
		{
			addSkillAllowed(context, skill, context.ref
					.getCDOMAllReference(SKILLLIST_CLASS));
			return ParseResult.SUCCESS;
		}
		return super.parseToken(context, skill, value);
	}

	@Override
	protected char separator()
	{
		return '|';
	}

	@Override
	protected ParseResult parseTokenWithSeparator(LoadContext context,
		Skill skill, String value)
	{
		StringTokenizer pipeTok = new StringTokenizer(value, Constants.PIPE);
		boolean added = false;

		while (pipeTok.hasMoreTokens())
		{
			String className = pipeTok.nextToken();
			if (Constants.LST_ALL.equals(className))
			{
				if (added)
				{
					return new ParseResult.Fail("Non-sensical Skill " + getTokenName()
							+ ": Contains ALL after a specific reference: "
							+ value);
				}
				addSkillAllowed(context, skill, context.ref
						.getCDOMAllReference(SKILLLIST_CLASS));
				break;
			}
			if (className.startsWith("!"))
			{
				return new ParseResult.Fail("Non-sensical Skill " + getTokenName()
						+ ": Contains ! without (or before) ALL: " + value);
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
					return new ParseResult.Fail("Invalid " + getTokenName()
							+ " cannot use !ALL");
				}
				addSkillNotAllowed(context, skill, context.ref
						.getCDOMReference(SKILLLIST_CLASS, clString));
			}
			else
			{
				return new ParseResult.Fail("Non-sensical Skill " + getTokenName()
						+ ": Contains ALL and a specific reference: " + value);
			}
		}
		return ParseResult.SUCCESS;
	}

	private void addSkillAllowed(LoadContext context, Skill skill,
			CDOMReference<ClassSkillList> ref)
	{
		context.list.addToMasterList(getTokenName(), skill, ref, skill);
	}

	private void addSkillNotAllowed(LoadContext context, Skill skill,
			CDOMReference<ClassSkillList> ref)
	{
		context.obj.addToList(skill, ListKey.PREVENTED_CLASSES, ref);
	}

	public String[] unparse(LoadContext context, Skill skill)
	{
		Changes<CDOMReference> masterChanges = context.getListContext()
				.getMasterListChanges(getTokenName(), skill, SKILLLIST_CLASS);
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
		Collection<CDOMReference> added = masterChanges.getAdded();
		Collection<CDOMReference<ClassSkillList>> prevented = removedChanges
				.getAdded();
		StringBuilder sb = new StringBuilder();
		if (added.isEmpty())
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
