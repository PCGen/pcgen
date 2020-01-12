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
 */
package pcgen.io.freemarker;

import java.util.List;

import pcgen.core.PlayerCharacter;
import pcgen.io.ExportHandler;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

/**
 * PCBooleanFunction allows character boolean values to be exported to a 
 * Freemarker template. It evaluates an export token for the current character and 
 * returns the value as a boolean. e.g. ${pcboolean("WEAPON.0.ISTYPE.Double")} 
 * 
 */
public class PCBooleanFunction implements TemplateMethodModelEx, CharacterExportAction
{
	private PlayerCharacter pc;
	private ExportHandler eh;

	/**
	 * Create a new instance of PCBooleanFunction
	 * @param pc The character being exported.
	 * @param eh The managing export handler.
	 */
	public PCBooleanFunction(PlayerCharacter pc, ExportHandler eh)
	{
		this.pc = pc;
		this.eh = eh;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arg0) throws TemplateModelException
	{
		if (arg0.size() != 1)
		{
			throw new TemplateModelException("Wrong arguments. formula required");
		}

		String tag = arg0.get(0).toString();

		String value = getExportVariable(tag, pc, eh);

		if ("1.0".equals(value) || "1".equals(value) || "Y".equals(value))
		{
			return Boolean.TRUE;
		}

		if (tag.equals(value))
		{
			throw new TemplateModelException("Invalid export tag '" + tag + "'.");
		}

        return Boolean.valueOf(value);
	}

}
