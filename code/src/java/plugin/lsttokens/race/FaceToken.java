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

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.formula.base.LegalScope;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.Race;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMCompatibilityToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with FACE Token
 */
public class FaceToken extends AbstractNonEmptyToken<Race>
		implements CDOMCompatibilityToken<Race>
{

	private static final int MOD_PRIORITY = 10;
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
				"FACE: LST Token is disabled when FACE: control is used");
		}
		if (value.indexOf(',') == -1)
		{
			value = value + ',' + 0;
		}
		FormatManager<OrderedPair> formatManager =
				(FormatManager<OrderedPair>) context.getReferenceContext()
					.getFormatManager("ORDEREDPAIR");
		LegalScope scope = context.getActiveScope();
		FormulaModifier<OrderedPair> modifier;
		try
		{
			modifier = context.getVariableContext()
				.getModifier(MOD_IDENTIFICATION, value, scope, formatManager);
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail(getTokenName() + " Modifier "
				+ MOD_IDENTIFICATION + " had value " + value
				+ " but it was not valid: " + iae.getMessage());
		}
		modifier.addAssociation("PRIORITY=" + MOD_PRIORITY);
		OrderedPair pair = modifier.process(null);
		if (pair.getPreciseX().doubleValue() < 0.0)
		{
			return new ParseResult.Fail(getTokenName() + " had value " + value
				+ " but first item cannot be negative");
		}
		if (pair.getPreciseY().doubleValue() < 0.0)
		{
			return new ParseResult.Fail(getTokenName() + " had value " + value
				+ " but second item cannot be negative");
		}

		if (!context.getVariableContext().isLegalVariableID(scope, CControl.FACE.getDefaultValue()))
		{
			return new ParseResult.Fail(getTokenName()
				+ " internal error: found invalid var name: " + CControl.FACE.getDefaultValue()
				+ ", Modified on " + race.getClass().getSimpleName() + ' '
				+ race.getKeyName());
		}
		VarModifier<OrderedPair> vm =
				new VarModifier<>(CControl.FACE.getDefaultValue(), scope, modifier);
		context.getObjectContext().addToList(race, ListKey.MODIFY, vm);
		return ParseResult.SUCCESS;
	}

	@Override
	public Class<Race> getTokenClass()
	{
		return Race.class;
	}

	@Override
	public int compatibilityLevel()
	{
		return 6;
	}

	@Override
	public int compatibilitySubLevel()
	{
		return 6;
	}

	@Override
	public int compatibilityPriority()
	{
		return 0;
	}

}
