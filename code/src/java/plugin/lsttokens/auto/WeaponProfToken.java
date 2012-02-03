/*
 * Copyright 2007 (C) Thomas Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package plugin.lsttokens.auto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.ChooseResultActor;
import pcgen.cdom.base.Constants;
import pcgen.cdom.content.ConditionalChoiceActor;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.cdom.helper.WeaponProfProvider;
import pcgen.cdom.reference.CDOMGroupRef;
import pcgen.cdom.reference.CDOMSingleRef;
import pcgen.core.Globals;
import pcgen.core.PlayerCharacter;
import pcgen.core.QualifiedObject;
import pcgen.core.WeaponProf;
import pcgen.core.prereq.Prerequisite;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.prereq.PreParserFactory;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.TokenUtilities;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMSecondaryToken;
import pcgen.rules.persistence.token.ParseResult;
import pcgen.util.Logging;

public class WeaponProfToken extends AbstractNonEmptyToken<CDOMObject> implements
		CDOMSecondaryToken<CDOMObject>, ChooseResultActor
{

	private static final Class<WeaponProf> WEAPONPROF_CLASS = WeaponProf.class;

	public String getParentToken()
	{
		return "AUTO";
	}

	@Override
	public String getTokenName()
	{
		return "WEAPONPROF";
	}

	private String getFullName()
	{
		return getParentToken() + ":" + getTokenName();
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context,
		CDOMObject obj, String value)
	{
		String weaponProfs;
		Prerequisite prereq = null; // Do not initialize, null is significant!
		boolean isPre = false;
		if (value.indexOf("[") == -1)
		{
			// Supported version of PRExxx using |.  Needs to be at the front of the
			// Parsing code because many objects expect the pre to have been determined
			// Ahead of time.  Until deprecated code is removed, it will have to stay
			// like this.
			weaponProfs = value;
			StringTokenizer tok = new StringTokenizer(weaponProfs, Constants.PIPE);
			while (tok.hasMoreTokens())
			{
				String token = tok.nextToken();
				if (PreParserFactory.isPreReqString(token))
				{
					if (isPre)
					{
						String errorText = "Invalid " + getTokenName() + ": " + value + "  PRExxx must be at the END of the Token";
						Logging.errorPrint(errorText);
						return new ParseResult.Fail(errorText);
					}
					prereq = getPrerequisite(token);
					if (prereq == null)
					{
						return new ParseResult.Fail("Error generating Prerequisite "
								+ prereq + " in " + getFullName());
					}
					int preStart = value.indexOf(token) - 1;
					weaponProfs = value.substring(0, preStart);
					isPre = true;
				}
			}
		}
		else
		{
			Logging.deprecationPrint("Use of [] for Prerequisites is is deprecated, "
					+ "please use | based standard");
			int openBracketLoc = value.indexOf("[");
			weaponProfs = value.substring(0, openBracketLoc);
			if (!value.endsWith("]"))
			{
				return new ParseResult.Fail("Unresolved Prerequisite in "
						+ getFullName() + " " + value + " in " + getFullName());
			}
			prereq = getPrerequisite(value.substring(openBracketLoc + 1, value
					.length() - 1));
			if (prereq == null)
			{
				return new ParseResult.Fail("Error generating Prerequisite "
						+ prereq + " in " + getFullName());
			}
		}

		ParseResult pr = checkForIllegalSeparator('|', weaponProfs);
		if (!pr.passed())
		{
			return pr;
		}

		boolean foundAny = false;
		boolean foundOther = false;

		StringTokenizer tok = new StringTokenizer(weaponProfs, Constants.PIPE);
		WeaponProfProvider wpp = new WeaponProfProvider();

		while (tok.hasMoreTokens())
		{
			String token = tok.nextToken();
			if ("%LIST".equals(token))
			{
				foundOther = true;
				ChooseResultActor cra;
				if (prereq == null)
				{
					cra = this;
				}
				else
				{
					ConditionalChoiceActor cca = new ConditionalChoiceActor(
							this);
					cca.addPrerequisite(prereq);
					cra = cca;
				}
				context.obj.addToList(obj, ListKey.CHOOSE_ACTOR, cra);
			}
			else if ("DEITYWEAPONS".equals(token))
			{
				foundOther = true;
				context.obj.put(obj, ObjectKey.HAS_DEITY_WEAPONPROF,
						new QualifiedObject<Boolean>(Boolean.TRUE, prereq));
			}
			else
			{
				if (Constants.LST_ALL.equalsIgnoreCase(token))
				{
					foundAny = true;
					CDOMGroupRef<WeaponProf> allRef = context.ref
							.getCDOMAllReference(WEAPONPROF_CLASS);
					wpp.addWeaponProfAll(allRef);
				}
				else
				{
					foundOther = true;
					if (token.startsWith(Constants.LST_TYPE_DOT)
							|| token.startsWith(Constants.LST_TYPE_EQUAL))
					{
						CDOMGroupRef<WeaponProf> rr = TokenUtilities
								.getTypeReference(context, WEAPONPROF_CLASS,
										token.substring(5));
						if (rr == null)
						{
							return ParseResult.INTERNAL_ERROR;
						}
						wpp.addWeaponProfType(rr);
					}
					else
					{
						CDOMSingleRef<WeaponProf> ref = context.ref
								.getCDOMReference(WEAPONPROF_CLASS, token);
						wpp.addWeaponProf(ref);
					}
				}
			}
		}

		if (foundAny && foundOther)
		{
			return new ParseResult.Fail("Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + value);
		}
		if (!wpp.isEmpty())
		{
			if (prereq != null)
			{
				wpp.addPrerequisite(prereq);
			}
			context.obj.addToList(obj, ListKey.WEAPONPROF, wpp);
		}
		return ParseResult.SUCCESS;
	}

	public String[] unparse(LoadContext context, CDOMObject obj)
	{
		List<String> list = new ArrayList<String>();
		Changes<ChooseResultActor> listChanges = context.getObjectContext()
				.getListChanges(obj, ListKey.CHOOSE_ACTOR);
		Changes<WeaponProfProvider> changes = context.obj.getListChanges(obj,
				ListKey.WEAPONPROF);
		QualifiedObject<Boolean> deityweap = context.obj.getObject(obj,
				ObjectKey.HAS_DEITY_WEAPONPROF);
		Collection<WeaponProfProvider> added = changes.getAdded();
		Collection<ChooseResultActor> listAdded = listChanges.getAdded();
		boolean foundAny = false;
		boolean foundOther = false;
		if (listAdded != null && !listAdded.isEmpty())
		{
			foundOther = true;
			for (ChooseResultActor cra : listAdded)
			{
				if (cra.getSource().equals(getTokenName()))
				{
					try
					{
						list.add(cra.getLstFormat());
					}
					catch (PersistenceLayerException e)
					{
						context.addWriteMessage("Error writing Prerequisite: "
								+ e);
						return null;
					}
				}
			}
		}
		if (deityweap != null && deityweap.getRawObject())
		{
			foundOther = true;
			StringBuilder sb = new StringBuilder();
			sb.append("DEITYWEAPONS");
			if (deityweap.hasPrerequisites())
			{
				sb.append('|');
				sb.append(context.getPrerequisiteString(deityweap.getPrerequisiteList()));
			}
			list.add(sb.toString());
		}
		if (added != null)
		{
			for (WeaponProfProvider wpp : added)
			{
				if (!wpp.isValid())
				{
					context.addWriteMessage("Non-sensical "
							+ "WeaponProfProvider in " + getFullName()
							+ ": Had invalid contents");
					return null;
				}
				StringBuilder sb = new StringBuilder(wpp.getLstFormat());
				List<Prerequisite> prereqs = wpp.getPrerequisiteList();
				if (prereqs != null && !prereqs.isEmpty())
				{
					if (prereqs.size() > 1)
					{
						context.addWriteMessage("Error: "
								+ obj.getClass().getSimpleName()
								+ " had more than one Prerequisite for "
								+ getFullName());
						return null;
					}
					sb.append('|');
					sb.append(context.getPrerequisiteString(prereqs));
				}
				String lstFormat = sb.toString();
				boolean isUnconditionalAll = Constants.LST_ALL
						.equals(lstFormat);
				foundAny |= isUnconditionalAll;
				foundOther |= !isUnconditionalAll;
				list.add(lstFormat);
			}
		}
		if (foundAny && foundOther)
		{
			context.addWriteMessage("Non-sensical " + getFullName()
					+ ": Contains ANY and a specific reference: " + list);
			return null;
		}
		if (list.isEmpty())
		{
			// Empty indicates no Token
			return null;
		}

		return list.toArray(new String[list.size()]);
	}

	public Class<CDOMObject> getTokenClass()
	{
		return CDOMObject.class;
	}

	public void apply(PlayerCharacter pc, CDOMObject obj, String o)
	{
		WeaponProf wp = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(WEAPONPROF_CLASS, o);
		if (wp != null)
		{
			pc.addWeaponProf(obj, wp);
		}
	}

	public void remove(PlayerCharacter pc, CDOMObject obj, String o)
	{
		WeaponProf wp = Globals.getContext().ref
				.silentlyGetConstructedCDOMObject(WEAPONPROF_CLASS, o);
		if (wp != null)
		{
			pc.removeWeaponProf(obj, wp);
		}
	}

	public String getSource()
	{
		return getTokenName();
	}

	public String getLstFormat()
	{
		return "%LIST";
	}
}
