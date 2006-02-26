/*
 * NameElement.java
 * Copyright 2001 (C) Devon Jones <soulcatcher@evilsoft.org>
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
 * Created on Sep 23, 2005
 */
package pcgen.gui;

import java.io.File;


/**
 * @author soulcatcher
 */
public class NameElement implements Comparable {
	private File source;
	private String name;
	
	
	/**
	 * @param source
	 * @param name
	 */
	public NameElement(File source, String name) {
		this.source = source;
		this.name = name;
	}
	
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * @return Returns the source file.
	 */
	public File getSource() {
		return source;
	}
	
	/**
	 * @param source The source file to set.
	 */
	public void setSource(File source) {
		this.source = source;
	}
	
	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return name;
	}
	
	/**
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object o) {
		if(o instanceof NameElement) {
			NameElement e = (NameElement)o;
			return name.compareTo(e.getName());
		}
		return 0;
	}
}
