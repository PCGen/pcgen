/*
 * Copyright 2016 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.rules.persistence;

import java.net.URI;

import pcgen.cdom.inst.CodeControl;
import pcgen.persistence.PersistenceLayerException;
import pcgen.persistence.lst.LstLineFileLoader;
import pcgen.persistence.lst.LstUtils;
import pcgen.rules.context.AbstractReferenceContext;
import pcgen.rules.context.LoadContext;
import pcgen.util.Logging;

/**
 * A CodeControlLoader is a loader that processes the Code Control Definitions
 */
public class CodeControlLoader extends LstLineFileLoader
{
	@Override
	public void parseLine(LoadContext context, String inputLine, URI sourceURI) {
		int sepLoc = inputLine.indexOf('\t');
		if (sepLoc != -1)
		{
			Logging.errorPrint(
				"Unsure what to do with line with multiple tokens: " + inputLine + " in file: " + sourceURI);
			return;
		}

		AbstractReferenceContext refContext = context.getReferenceContext();
		CodeControl controller = refContext.constructNowIfNecessary(CodeControl.class, "Controller");
		LstUtils.processToken(context, controller, sourceURI, inputLine);
	}
}
