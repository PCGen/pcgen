/*
 * SpringHelper.java
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
 *
 * Created on 16/10/2009 2:38:00 PM
 *
 * $Id$
 */

package pcgen.cdom.helper;

import java.util.Collection;

import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import pcgen.cdom.facet.base.AbstractStorageFacet;

/**
 * The Class {@code SpringHelper} is a simple helper for
 * integrating the Spring framework into PCGen.
 *
 * <br>
 * 
 * @author James Dempsey &lt;jdempsey@users.sourceforge.net&gt;
 */
public final class SpringHelper
{
	
	private SpringHelper()
	{
		//Do not instantiate Utility Class
	}

	private static XmlBeanFactory beanFactory = null;

	/**
	 * Initialise the Spring resources. May be called multiple times but 
	 * only the first call will have any effect.
	 */
	private static synchronized void initSpring()
	{
		if (beanFactory != null)
		{
			return;
		}
		
		ClassPathResource res = new ClassPathResource("applicationContext.xml");
		beanFactory = new XmlBeanFactory(res);
	}
	
	/**
	 * Retrieve a Spring bean based on the class that it implements. Where multiple 
	 * beans implement a class, the first will be returned. 
	 * 
	 * @param cl The type of bean to be retrieved.
	 * @return The bean, or null if none exists.
	 */
	public static <T extends Object> T getBean(Class<T> cl)
	{
		if (beanFactory == null)
		{
			initSpring();
		}

		String[] beanNamesForType = beanFactory.getBeanNamesForType(cl);
		if (beanNamesForType.length ==0) 
		{
			return null;
		}
		return beanFactory.getBean(beanNamesForType[0], cl);
	}
	
	public static Collection<AbstractStorageFacet> getStorageBeans()
	{
		if (beanFactory == null)
		{
			initSpring();
		}

		return beanFactory.getBeansOfType(AbstractStorageFacet.class).values();
	}
}
