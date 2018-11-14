/**
 * Copyright (c) 2008 Andrew Wilson <nuance@users.sourceforge.net>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 * Created 09-Aug-2008 11:49:49
 */

package pcgen.core.term;

import pcgen.core.Deity;
import pcgen.core.Globals;
import pcgen.core.display.CharacterDisplay;
import pcgen.output.channel.compat.DeityCompat;

public class PCHasDeityTermEvaluator extends BasePCDTermEvaluator implements TermEvaluator
{
	private final String deity;

	public PCHasDeityTermEvaluator(String originalText, String deity)
	{
		this.originalText = originalText;
		this.deity = deity;
	}

	@Override
	public Float resolve(CharacterDisplay display)
	{
		Deity requiredDeity = Globals.getContext().getReferenceContext()
			.silentlyGetConstructedCDOMObject(Deity.class, deity);
		Deity currentDeity = DeityCompat.getCurrentDeity(display.getCharID());
		boolean matches = ((requiredDeity == null) && (currentDeity == null))
			|| ((requiredDeity != null) && requiredDeity.equals(currentDeity));
		return matches ? 1.0f : 0.0f;
	}

	@Override
	public boolean isSourceDependant()
	{
		return false;
	}

	public boolean isStatic()
	{
		return false;
	}
}
