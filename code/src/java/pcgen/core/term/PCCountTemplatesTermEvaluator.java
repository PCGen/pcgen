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
 * Created 09-Aug-2008 20:34:40
 */

package pcgen.core.term;

import pcgen.cdom.enumeration.ObjectKey;
import pcgen.core.PCTemplate;
import pcgen.core.display.CharacterDisplay;
import pcgen.util.enumeration.View;

public class PCCountTemplatesTermEvaluator 
		extends BasePCDTermEvaluator implements TermEvaluator
{
	public PCCountTemplatesTermEvaluator(String originalText)
	{
		this.originalText = originalText;
	}

	@Override
	public Float resolve(CharacterDisplay display)
	{
		Float count = new Float(0);
		for (PCTemplate template : display.getTemplateSet())
		{
			//TODO This is a bug, it assumes export
			if (template.getSafe(ObjectKey.VISIBILITY).isVisibleTo(View.VISIBLE_EXPORT))
			{
				count++;
			}
		}
		return count;
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
