/*
 * Copyright (c) 2008-14 Tom Parker <thpr@users.sourceforge.net>
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
package plugin.lsttokens.template;

import java.util.Collection;

import pcgen.base.calculation.FormulaModifier;
import pcgen.base.math.OrderedPair;
import pcgen.base.util.FormatManager;
import pcgen.cdom.content.VarModifier;
import pcgen.cdom.enumeration.ListKey;
import pcgen.cdom.formula.scope.PCGenScope;
import pcgen.cdom.util.CControl;
import pcgen.cdom.util.ControlUtilities;
import pcgen.core.PCTemplate;
import pcgen.rules.context.Changes;
import pcgen.rules.context.LoadContext;
import pcgen.rules.persistence.token.AbstractNonEmptyToken;
import pcgen.rules.persistence.token.CDOMPrimaryToken;
import pcgen.rules.persistence.token.ParseResult;

/**
 * Class deals with FACE Token
 */
public class FaceToken extends AbstractNonEmptyToken<PCTemplate> implements CDOMPrimaryToken<PCTemplate>
{

	private static final int MOD_PRIORITY = 100;
	private static final String MOD_IDENTIFICATION = "SET";

	@Override
	public String getTokenName()
	{
		return "FACE";
	}

	@Override
	protected ParseResult parseNonEmptyToken(LoadContext context, PCTemplate template, String value)
	{
		return parseFace(context, template, value);
	}

	protected ParseResult parseFace(LoadContext context, PCTemplate fObj, String value)
	{
		if (ControlUtilities.hasControlToken(context, CControl.FACE))
		{
			return new ParseResult.Fail("FACE: LST Token is disabled when FACE: control is used");
		}
		if (value.indexOf(',') == -1)
		{
			value = value + ',' + 0;
		}
		@SuppressWarnings("unchecked")
		FormatManager<OrderedPair> formatManager =
				(FormatManager<OrderedPair>) context.getReferenceContext().getFormatManager("ORDEREDPAIR");
		PCGenScope scope = context.getActiveScope();
		FormulaModifier<OrderedPair> modifier;
		try
		{
			modifier = context.getVariableContext().getModifier(MOD_IDENTIFICATION, value, scope, formatManager);
		}
		catch (IllegalArgumentException iae)
		{
			return new ParseResult.Fail(
				getTokenName() + " Modifier SET had value " + value + " but it was not valid: " + iae.getMessage());
		}
		modifier.addAssociation("PRIORITY=" + MOD_PRIORITY);
		OrderedPair pair = modifier.process(null);
		if (pair.getPreciseX().doubleValue() < 0.0)
		{
			return new ParseResult.Fail(getTokenName() + " had value " + value + " but first item cannot be negative");
		}
		if (pair.getPreciseY().doubleValue() < 0.0)
		{
			return new ParseResult.Fail(getTokenName() + " had value " + value + " but second item cannot be negative");
		}
		String varName = CControl.FACE.getDefaultValue();
		if (!context.getVariableContext().isLegalVariableID(scope, varName))
		{
			return new ParseResult.Fail(getTokenName() + " internal error: found invalid fact name: " + varName
				+ ", Modified on " + fObj.getClass().getSimpleName() + ' ' + fObj.getKeyName());
		}
		VarModifier<OrderedPair> vm = new VarModifier<>(varName, scope, modifier);
		context.getObjectContext().addToList(fObj, ListKey.MODIFY, vm);
		return ParseResult.SUCCESS;
	}

	@Override
	public String[] unparse(LoadContext context, PCTemplate pct)
	{
		Changes<VarModifier<?>> changes = context.getObjectContext().getListChanges(pct, ListKey.MODIFY);
		Collection<VarModifier<?>> added = changes.getAdded();
		String face = null;
		if (added != null)
		{
			for (VarModifier<?> vm : added)
			{
				FormulaModifier<?> modifier = vm.getModifier();
				if (CControl.FACE.getDefaultValue().equals(vm.getVarName())
					&& (!vm.getLegalScope().getParentScope().isPresent())
					&& (modifier.getIdentification().equals(MOD_IDENTIFICATION)))
				{
					face = modifier.getInstructions();
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
	public Class<PCTemplate> getTokenClass()
	{
		return PCTemplate.class;
	}
}
