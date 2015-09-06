/*
 * Copyright 2015 (C) Tom Parker <thpr@users.sourceforge.net>
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
package pcgen.output.factory;

import pcgen.core.GameMode;
import pcgen.output.base.ModeModelFactory;
import pcgen.output.library.ObjectWrapperLibrary;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * An ModeNameModelFactory is a ModeModelFactory that returns the name of the
 * current Game Mode.
 */
public class ModeNameModelFactory implements ModeModelFactory
{
	
	/*
	 * Note: It would be nice someday if this wasn't necessary as a separate
	 * class. A more "CDOM-like" structure in the GameMode object would allow
	 * the UnitSet to be grabbed "generically" as an ObjectKey, but that is just
	 * not in place today for GameMode.
	 */
	
	/**
	 * @see pcgen.output.base.ModeModelFactory#generate()
	 */
	@Override
	public TemplateModel generate(GameMode mode) throws TemplateModelException
	{
		return ObjectWrapperLibrary.getInstance().wrap(mode.getName());
	}
}
