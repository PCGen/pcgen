/*
 * PartyFacade.java
 * Copyright 2011 Connor Petty <cpmeister@users.sourceforge.net>
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
package pcgen.facade.core;

import pcgen.facade.util.ReferenceFacade;

import java.io.BufferedWriter;
import java.io.File;

import pcgen.facade.util.ListFacade;
import pcgen.io.ExportHandler;

/**
 *
 */
public interface PartyFacade extends ListFacade<CharacterFacade>
{

	public ReferenceFacade<File> getFileRef();
	
	public void setFile(File file);
	
	public void export(ExportHandler theHandler, BufferedWriter buf);

}
