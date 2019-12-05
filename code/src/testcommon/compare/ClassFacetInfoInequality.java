/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net> This program is
 * free software; you can redistribute it and/or modify it under the terms of
 * the GNU Lesser General Public License as published by the Free Software
 * Foundation; either version 2.1 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA
 */
package compare;

import java.lang.reflect.Field;

import pcgen.base.test.InequalityTester;
import pcgen.cdom.facet.model.ClassFacet;

class ClassFacetInfoInequality implements
        InequalityTest<ClassFacet.ClassInfo>
{

    @Override
    public String testInequality(ClassFacet.ClassInfo t1,
            ClassFacet.ClassInfo t2, InequalityTester t, String location)
    {
        if (t1.classCount() != t2.classCount())
        {
            return location + "/Class counts not equal";
        }
        try
        {
            Class<ClassFacet.ClassInfo> cl = ClassFacet.ClassInfo.class;
            Field mapField = cl.getDeclaredField("map");
            mapField.setAccessible(true);
            Object m1 = mapField.get(t1);
            Object m2 = mapField.get(t2);
            String result = t.testEquality(m1, m2, location + "/Map");
            if (result != null)
            {
                return result;
            }
            Field levelmapField = cl.getDeclaredField("levelmap");
            levelmapField.setAccessible(true);
            Object lm1 = mapField.get(t1);
            Object lm2 = mapField.get(t2);
            result = t.testEquality(lm1, lm2, location + "/LevelMap");
            if (result != null)
            {
                return result;
            }
        } catch (SecurityException e)
        {
            return location + "/SE/" + e.getLocalizedMessage();
        } catch (NoSuchFieldException e)
        {
            return location + "/NSFE/" + e.getLocalizedMessage();
        } catch (IllegalArgumentException e)
        {
            return location + "/IARE/" + e.getLocalizedMessage();
        } catch (IllegalAccessException e)
        {
            return location + "/IACE/" + e.getLocalizedMessage();
        }
        return null;
    }

}
