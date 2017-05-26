/*
 * Copyright (c) 2008-15 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.race;

import java.util.Collection;

import pcgen.base.calculation.PCGenModifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.formula.base.ScopeInstance;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Race;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with FACE Token
 */
public class FaceToken extends AbstractNonEmptyToken<Race> implements
		CDOMPrimaryToken<Race>
{

	private static final int MOD_PRIORITY = 10;
	private static final String VAR_NAME = "Face";
	private static final String MOD_IDENTIFICATION = "SET";

	@Override
	public String getTokenName()
	{
		return "FACE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, Race race,
		String value)
	{
		if (ControlUtilities.hasControlToken(context, CControl.FACE))
		{
			return new ParseResult.Fail(
				"FACE: LST Token is disabled when FACE: control is used",
				context);
		}
		if (value.indexOf(',') == -1)
		{
			value = value + ',' + 0;
		}
		FormatManager<OrderedPair> formatManager =
				(FormatManager<OrderedPair>) context.getReferenceContext()
					.getFormatManager("ORDEREDPAIR");
		ScopeInstance scopeInst = context.getActiveScope();
		LegalScope scope = scopeInst.getLegalScope();
		PCGenModifier<OrderedPair> modifier;
		try
		{
			modifier =
					context.getVariableContext().getModifier(
						MOD_IDENTIFICATION, value, MOD_PRIORITY, scope,
						formatManager);
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail(getTokenName() + " Modifier "
				+ MOD_IDENTIFICATION + " had value " + value
				+ " but it was not valid: " + iae.getMessage(), context);
		}
		OrderedPair pair = modifier.process(null);
		if (pair.getPreciseX().doubleValue() < 0.0)
		{
			return new ParseResult.Fail(getTokenName() + " had value " + value
				+ " but first item cannot be negative", context);
		}
		if (pair.getPreciseY().doubleValue() < 0.0)
		{
			return new ParseResult.Fail(getTokenName() + " had value " + value
				+ " but second item cannot be negative", context);
		}

		if (!context.getVariableContext().isLegalVariableID(scope, VAR_NAME))
		{
			return new ParseResult.Fail(getTokenName()
				+ " internal error: found invalid var name: " + VAR_NAME
				+ ", Modified on " + race.getClass().getSimpleName() + ' '
				+ race.getKeyName(), context);
		}
		VarModifier<OrderedPair> vm =
				new VarModifier<>(VAR_NAME, scope, modifier);
		context.getObjectContext().addToList(race, ListKey.MODIFY, vm);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, Race race)
	{
		Changes<VarModifier<?>> changes =
				context.getObjectContext().getListChanges(race, ListKey.MODIFY);
		Collection<VarModifier<?>> added = changes.getAdded();
		String face = null;
		if (added != null)
		{
			for (VarModifier<?> vm : added)
			{
				PCGenModifier<?> modifier = vm.getModifier();
				if (VAR_NAME.equals(vm.getVarName())
					&& (vm.getLegalScope().getParentScope() == null)
					&& (modifier.getUserPriority() == MOD_PRIORITY)
					&& (vm.getModifier().getIdentification()
						.equals(MOD_IDENTIFICATION)))
				{
					face = vm.getModifier().getInstructions();
					if (face.endsWith(",0"))
					{
						face = face.substring(0, face.length() - 2);
					}
				}
			}
		}
		return (face == null) ? null : new String[]{face};
	}

	@Override
	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

}
