/*
 * Copyright 2002 (C) Jonas Karlsson
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
package pcgen.util.chooser;

import java.util.Stack;

import pcgen.facade.core.UIDelegate;

/**
 * This factory class returns a Chooser of the appropriate type. This is intended
 * to reduce the core/gui interdependence. Much more work is needed on this...
 * Currently only a SwingChooser has been implemented.
 */
public final class ChooserFactory
{
	private static UIDelegate delegate;
	private static final Stack<String> interfaceClassNameStack = new Stack<>();

	/**
	 * Deliberately private so it can't be instantiated.
	 */
	private ChooserFactory()
	{
		// Empty Constructor
	}

	/**
	 * Retrieve an optional handler for making choices. If no handler is 
	 * currently registered, it is expected that the UI class caller will 
	 * display an interactive dialog. If multiple handlers are currently 
	 * registered the most recently registered (LIFO) will be returned.
	 * 
	 * @return The most recently registered ChoiceHandler, if any.
	 */
	public static ChoiceHandler getChoiceHandler()
	{
		if (interfaceClassNameStack.isEmpty())
		{
			return null;
		}
		String className = interfaceClassNameStack.peek();
		try
		{
			Class<?> c = Class.forName(className);
			ChoiceHandler ci = (ChoiceHandler) c.newInstance();
			return ci;
		}
		catch (ClassNotFoundException | IllegalAccessException | InstantiationException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Add a chooser class name to the top of the stack of class names. 
	 * Existing chooser class names will be preserved but will not 
	 * be used until this one is popped off the stack.
	 * 
	 * @param chooserClassname The chooser class name to add.  
	 */
	public static void pushChooserClassname(String chooserClassname)
	{
		ChooserFactory.interfaceClassNameStack.push(chooserClassname);
	}

	/**
	 * Remove a name from the top of the stack of chooser class names. This 
	 * will then expose the next newest class name, or empty the stack. 
	 * @return The class name that was removed.
	 */
	public static String popChooserClassname()
	{
		if (ChooserFactory.interfaceClassNameStack.isEmpty())
		{
			return null;
		}
		return ChooserFactory.interfaceClassNameStack.pop();
	}

	/**
	 * @return the delegate
	 */
	public static UIDelegate getDelegate()
	{
		return delegate;
	}

	/**
	 * @param delegate the delgate to set
	 */
	public static void setDelegate(UIDelegate delegate)
	{
		ChooserFactory.delegate = delegate;
	}
}
