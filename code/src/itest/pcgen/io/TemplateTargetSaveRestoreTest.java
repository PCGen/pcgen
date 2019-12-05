/*
 * Copyright (c) 2013 Tom Parker <thpr@users.sourceforge.net>
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
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
package pcgen.io;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import pcgen.core.PCClass;
import pcgen.core.PCTemplate;
import pcgen.io.testsupport.AbstractGlobalTargetedSaveRestoreTest;
import plugin.lsttokens.TypeLst;
import plugin.lsttokens.choose.ClassToken;
import plugin.lsttokens.template.FavoredclassToken;

import org.junit.jupiter.api.Test;

public class TemplateTargetSaveRestoreTest extends
        AbstractGlobalTargetedSaveRestoreTest<PCTemplate>
{

    @Override
    public Class<PCTemplate> getObjectClass()
    {
        return PCTemplate.class;
    }

    @Override
    protected void applyObject(PCTemplate obj)
    {
        pc.addTemplate(obj);
    }

    @Override
    protected Object prepare(PCTemplate obj)
    {
        return obj;
    }

    @Override
    protected void remove(Object o)
    {
        reloadedPC.removeTemplate((PCTemplate) o);
    }

    @Test
    public void testTemplateFavoredClass()
    {
        PCClass monclass = create(PCClass.class, "MonClass");
        new TypeLst().parseToken(context, monclass, "Monster");
        PCTemplate monster = create(PCTemplate.class, "Monster");
        create(PCClass.class, "MyClass");
        new FavoredclassToken().parseToken(context, monster, "%LIST");
        new ClassToken().parseToken(context, monster, "MonClass|MyClass");
        finishLoad();
        pc.addTemplate(monster);
        runRoundRobin(getPreEqualityCleanup());
        assertTrue(pc.getDisplay().getFavoredClasses().contains(monclass));
        assertTrue(reloadedPC.getDisplay().getFavoredClasses()
                .contains(monclass));
        reloadedPC.removeTemplate(monster);
        reloadedPC.setDirty(true);
        assertFalse(reloadedPC.getDisplay().getFavoredClasses()
                .contains(monclass));
    }
}
