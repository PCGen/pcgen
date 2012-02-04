/*
 * SourceFilter.java
 * Copyright 2002 (C) Thomas Behr <ravenlock@gmx.de>
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
 *
 * Created on February 9, 2002, 2:30 PM
 */
package pcgen.gui.filter;

import java.util.StringTokenizer;

import pcgen.cdom.base.Constants;
import pcgen.cdom.enumeration.SourceFormat;
import pcgen.core.PObject;
import pcgen.core.PlayerCharacter;
import pcgen.system.LanguageBundle;

/**
 * <code>SourceFilter</code>
 *
 * @author Thomas Behr
 * @version $Revision$
 */
final class SourceFilter extends AbstractPObjectFilter
{
	/** HIGH */
	public static final int HIGH = 0;
	/** LOW */
	public static final int LOW = 1;
	private String source;
	private int detailLevel;

	SourceFilter(String src, int argDetailLevel)
	{
		super();
		this.detailLevel = argDetailLevel;
		this.source = (this.detailLevel == LOW) ? normalizeSource(src) : src;

		int cInt = source.indexOf(":");
		int pInt = source.indexOf(Constants.PIPE);

		if (source.startsWith("SOURCE") && (cInt > -1) && (pInt > cInt)) //$NON-NLS-1$
		{
			source = source.substring(cInt + 1, pInt);
		}

		setCategory(LanguageBundle.getString("in_sourceLabel")); //$NON-NLS-1$
		setName(source);
		setDescription(LanguageBundle.getFormattedString("in_filterAccObj",getName()));  //$NON-NLS-1$//$NON-NLS-2$
	}

	public boolean accept(PlayerCharacter aPC, PObject pObject)
	{
		if (pObject == null)
		{
			return false;
		}

		final String sourceStr = SourceFormat.getFormattedString(pObject,
				SourceFormat.MEDIUM, true );
		if (detailLevel == LOW)
		{
			return normalizeSource(sourceStr).equals(source);
		}

		return sourceStr.equals(source);
	}

	private static String normalizeSource(String s)
	{
		String work = s;

		if (work.indexOf(Constants.COMMA) > -1)
		{
			work = new StringTokenizer(s, Constants.COMMA).nextToken();
		}

		return work.trim();
	}
}
