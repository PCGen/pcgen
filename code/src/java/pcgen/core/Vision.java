/*
 * PCClass.java
 * Copyright 2006 (C) Tom Parker <thpr@users.sourceforge.net>
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
 * Created on October 25, 2006
 *
 * $Id: PCClass.java 1526 2006-10-25 03:56:08Z thpr $
 */
package pcgen.core;

import pcgen.util.enumeration.VisionType;

public class Vision extends PrereqObject implements Comparable<Vision> {

	private final VisionType visionType;

	private final String distance;

	public Vision(VisionType type, String dist) {
		if (type == null) {
			throw new IllegalArgumentException("Vision Type cannot be null");
		}
		visionType = type;
		distance = dist;
	}

	public String getDistance() {
		return distance;
	}

	public VisionType getType() {
		return visionType;
	}

	public String toString() {
		try {
			return toString(Integer.parseInt(distance));
		} catch (NumberFormatException e) {
			return visionType + " (" + distance + "')";
		}
	}

	private String toString(int distance) {
		String vision = visionType + " (" + distance + "')";
		if(distance <= 0) {
			vision = visionType.toString();
		}
		return vision;
	}

	public boolean equals(Vision v) {
		return distance.equals(v.distance) && visionType.equals(v.visionType);
	}

	public int hashCode() {
		return distance.hashCode() ^ visionType.hashCode();
	}

	public String toString(PlayerCharacter aPC) {
		return toString(aPC.getVariableValue(distance, "").intValue());
	}

	public int compareTo(Vision v) {
		//CONSIDER This is potentially a slow method, but definitely works - thpr 10/26/06
		return toString().compareTo(v.toString());
	}
}
