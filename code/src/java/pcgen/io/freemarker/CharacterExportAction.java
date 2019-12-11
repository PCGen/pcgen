/*
 * Copyright 2013 (C) James Dempsey <jdempsey@users.sourceforge.net>
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
 *
 */
package pcgen.io.freemarker;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringWriter;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;
import pcgen.util.Logging;

/**
 * CharacterExportAction is a base class for functions and directives that 
 * handle export tokens to produce a result.
 * 
 * 
 */
public interface CharacterExportAction
{

	/**
	 * Convert the supplied export token into an string value 
	 * @param exportToken The export token to be processed.
	 * @param pc The character being exported.
	 * @param modelEh The ExportHandler managing the output.
	 * @return The value fot he export token for the character.
	 */
	default String getExportVariable(String exportToken, PlayerCharacter pc, ExportHandler modelEh)
	{
		final StringWriter sWriter = new StringWriter();
		final BufferedWriter aWriter = new BufferedWriter(sWriter);
		modelEh.replaceToken(exportToken, aWriter, pc);
		sWriter.flush();

		try
		{
			aWriter.flush();
		}
		catch (IOException e)
		{
			Logging.errorPrint("Couldn't flush the StringWriter used in " + "PCStringDirective.getExportVariable.", e);
		}

        return sWriter.toString();
	}

}
