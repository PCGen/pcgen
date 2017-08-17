/*
 *  Copyright (C) 2003 Devon D Jones
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public
 *  License along with this library; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package gmgen.io;

import java.io.File;

/**
 * This is the interface for the classes that will be exporting or saving to a
 * file.
 */
@FunctionalInterface
public interface ExportHandler
{
	/**
	 * This method will be overridden in the using classes to save
	 * the class to a file.
	 * @param path the file and path which will be used to save.
	 */
	public abstract void export(File path);
}
