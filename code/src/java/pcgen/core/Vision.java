/*
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
 *
 */
package pcgen.core;

import java.util.Objects;

import pcgen.base.formula.Formula;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.base.CDOMReference;
import pcgen.cdom.base.FormulaFactory;
import pcgen.cdom.list.VisionList;
import pcgen.cdom.reference.CDOMDirectSingleRef;
import pcgen.util.enumeration.VisionType;

public class Vision extends CDOMObject implements Comparable<Vision>
{

    public static final CDOMReference<VisionList> VISIONLIST;

    static
    {
        VisionList wpl = new VisionList();
        wpl.setName("*Vision");
        VISIONLIST = CDOMDirectSingleRef.getRef(wpl);
    }

    private final VisionType visionType;

    private final Formula distance;

    public Vision(VisionType type, Formula dist)
    {
        Objects.requireNonNull(type, "Vision Type cannot be null");
        visionType = type;
        if (!dist.isValid())
        {
            throw new IllegalArgumentException("Vision Type distance must be valid");
        }
        distance = dist;
    }

    public Formula getDistance()
    {
        return distance;
    }

    public VisionType getType()
    {
        return visionType;
    }

    @Override
    public String toString()
    {
        try
        {
            return toString(Integer.parseInt(distance.toString()));
        } catch (NumberFormatException e)
        {
            return visionType + " (" + distance + ')';
        }
    }

    private String toString(int dist)
    {
        String vision = visionType + " (" + dist + "')";
        if (dist <= 0)
        {
            vision = visionType.toString();
        }
        return vision;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Vision)
        {
            Vision v = (Vision) obj;
            return distance.equals(v.distance) && visionType.equals(v.visionType);
        }
        return false;
    }

    @Override
    public int hashCode()
    {
        return distance.hashCode() ^ visionType.hashCode();
    }

    public String toString(PlayerCharacter aPC)
    {
        return toString(distance.resolve(aPC, "").intValue());
    }

    @Override
    public int compareTo(Vision v)
    {
        //CONSIDER This is potentially a slow method, but definitely works - thpr 10/26/06
        return toString().compareTo(v.toString());
    }

    public static Vision getVision(String visionType)
    {
        // expecting value in form of Darkvision (60') or Darkvision
        int commaLoc = visionType.indexOf(',');
        if (commaLoc != -1)
        {
            throw new IllegalArgumentException("Invalid Vision: " + visionType + ". May not contain a comma");
        }
        int quoteLoc = visionType.indexOf('\'');
        int openParenLoc = visionType.indexOf('(');
        Formula distance;
        String type;
        if (openParenLoc == -1)
        {
            if (visionType.indexOf(')') != -1)
            {
                throw new IllegalArgumentException(
                        "Invalid Vision: " + visionType + ". Had close paren without open paren");
            }
            if (quoteLoc != -1)
            {
                throw new IllegalArgumentException("Invalid Vision: " + visionType + ". Had quote parens");
            }
            type = visionType;
            distance = FormulaFactory.ZERO;
        } else
        {
            int length = visionType.length();
            if (visionType.indexOf(')') != length - 1)
            {
                throw new IllegalArgumentException(
                        "Invalid Vision: " + visionType + ". Close paren not at end of string");
            }
            int endDistance = length - 1;
            if (quoteLoc != -1)
            {
                if (quoteLoc == length - 2)
                {
                    endDistance--;
                } else
                {
                    throw new IllegalArgumentException(
                            "Invalid Vision: " + visionType + ". Foot character ' not immediately before close paren");
                }
            }
            type = visionType.substring(0, openParenLoc).trim();
            String dist = visionType.substring(openParenLoc + 1, endDistance);
            if (dist.isEmpty())
            {
                throw new IllegalArgumentException("Invalid Vision: " + visionType + ". No Distance provided");
            }
            if (quoteLoc != -1)
            {
                try
                {
                    Integer.parseInt(dist);
                } catch (NumberFormatException nfe)
                {
                    throw new IllegalArgumentException(
                            "Invalid Vision: " + visionType + ". Vision Distance with Foot character ' was not an "
                                    + "integer", nfe);
                }
            }
            distance = FormulaFactory.getFormulaFor(dist);
            if (!distance.isValid())
            {
                throw new IllegalArgumentException("Invalid: Vision Distance was not valid: " + distance);
            }
        }
        if (type.isEmpty())
        {
            throw new IllegalArgumentException("Invalid Vision: " + visionType + ". No Vision Type provided");
        }
        return new Vision(VisionType.getVisionType(type), distance);
    }

    @Override
    public boolean isType(String str)
    {
        return false;
    }

    @Override
    public String getKeyName()
    {
        return toString();
    }

}
