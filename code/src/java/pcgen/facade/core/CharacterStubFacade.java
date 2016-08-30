/*
 * CharacterStubFacade.java
 * Copyright 2012 Connor Petty <cpmeister@users.sourceforge.net>
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
 * Created on Mar 4, 2012, 5:13:42 PM
 */
package pcgen.facade.core;

import pcgen.facade.util.ReferenceFacade;

import java.io.File;

/**
 *
 * @author Connor Petty &lt;cpmeister@users.sourceforge.net&gt;
 */
public interface CharacterStubFacade
{
	public ReferenceFacade<String> getNameRef();
	public ReferenceFacade<File> getFileRef();
	
}
