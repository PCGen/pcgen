/*
 * Copyright 2010 (C) Tom Parker <thpr@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.reference;

import java.util.ArrayList;
import java.util.List;

import pcgen.cdom.base.ChooseInformation;
import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.Ability;
import pcgen.core.AbilityUtilities;
import pcgen.util.Logging;

public class AbilityReferenceResolver implements ReferenceResolver<Ability>
{
	public void resolve(ReferenceManufacturer<Ability> rm, String name,
			CDOMSingleRef<Ability> reference)
	{
		Ability activeObj = rm.getObject(name);
		if (activeObj == null)
		{
			List<String> choices = new ArrayList<String>();
			String reduced = AbilityUtilities.getUndecoratedName(name, choices);
			activeObj = rm.getObject(reduced);
			if (activeObj == null
					&& (rm.containsUnconstructed(name) || rm
							.containsUnconstructed(reduced)))
			{
				activeObj = rm.buildObject(name);
			}
			if (activeObj == null)
			{
				Logging.errorPrint("Unable to Resolve: "
						+ rm.getReferenceDescription() + " " + name);
			}
			else
			{
				reference.addResolution(activeObj);
			}
			if (choices.size() == 1)
			{
				reference.setChoice(choices.get(0));
			}
			else if (choices.size() > 1)
			{
				Logging.errorPrint("Invalid use of multiple items "
						+ "in parenthesis (comma prohibited) in " + activeObj
						+ " " + choices.toString());
			}
		}
		else
		{
			reference.addResolution(activeObj);
			if (reference.requiresTarget()
					&& activeObj.getSafe(ObjectKey.MULTIPLE_ALLOWED))
			{
				ChooseInformation<?> ci = activeObj.get(ObjectKey.CHOOSE_INFO);
				//Is MULT:YES.... and not CHOOSE:NOCHOICE
				//Null check (unfortunately) required to protect vs. bad data
				//No error message though, that is caught by MULT token
				if ((ci != null) && !"No Choice".equals(ci.getName()))
				{
					Logging.errorPrint("Invalid use of MULT:YES Ability "
							+ activeObj + " where a target [parens] is required");
					Logging.errorPrint("PLEASE TAKE NOTE: "
							+ "If usage locations are reported, "
							+ "not all usages are necessary illegal "
							+ "(at least one is)");
					rm.fireUnconstuctedEvent(reference);
				}
			}
		}
	}

}
