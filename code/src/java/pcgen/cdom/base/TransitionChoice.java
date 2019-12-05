/*
 * Copyright 2007, 2008 (C) Tom Parker <thpr@users.sourceforge.net>
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */
package pcgen.cdom.base;

import java.util.Collection;

import pcgen.base.formula.Formula;
import pcgen.core.PlayerCharacter;

/**
 * This is a transitional class from PCGen 5.15+ to the final CDOM core. It is
 * provided as convenience to hold a set of choices and the number of choices
 * allowed, prior to final implementation of the new choice system
 *
 * @param <T> The type of object that will be chosen when this TransitionChoice
 *            is used
 */
public interface TransitionChoice<T> extends BasicChoice<T>
{

    void allowStack(boolean allowStack);

    boolean allowsStacking();

    Collection<? extends T> driveChoice(PlayerCharacter apc);

    void setStackLimit(int limit);

    Integer getStackLimit();

    Formula getCount();

    void act(Collection<? extends T> name, CDOMObject owner, PlayerCharacter pc);

    void setRequired(boolean required);

    SelectableSet<? extends T> getChoices();

}
