/*
 * Copyright 2009 (C) James Dempsey
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
 */

package pcgen.cdom.helper;

import java.util.Collection;

import pcgen.cdom.facet.base.AbstractStorageFacet;

import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

/**
 * The Class {@code SpringHelper} is a simple helper for
 * integrating the Spring framework into PCGen.
 */
public final class SpringHelper
{
    private SpringHelper()
    {
    }

    private static final ListableBeanFactory BEAN_FACTORY;

    static
    {
        Resource appClassRes = new ClassPathResource("applicationContext.xml");
        DefaultListableBeanFactory xbf = new DefaultListableBeanFactory();
        BeanDefinitionReader beanReader = new XmlBeanDefinitionReader(xbf);
        beanReader.loadBeanDefinitions(appClassRes);
        BEAN_FACTORY = xbf;
    }

    /**
     * Retrieve a Spring bean based on the class that it implements. Where multiple
     * beans implement a class, the first will be returned.
     *
     * @param cl The type of bean to be retrieved.
     * @return The bean, or null if none exists.
     */
    public static @Nullable <T> T getBean(Class<T> cl)
    {
        String[] beanNamesForType = BEAN_FACTORY.getBeanNamesForType(cl);
        if (beanNamesForType.length == 0)
        {
            return null;
        }
        return BEAN_FACTORY.getBean(beanNamesForType[0], cl);
    }

    public static Collection<AbstractStorageFacet> getStorageBeans()
    {
        return BEAN_FACTORY.getBeansOfType(AbstractStorageFacet.class).values();
    }
}
