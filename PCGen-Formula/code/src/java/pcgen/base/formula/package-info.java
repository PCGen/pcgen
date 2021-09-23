/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net>
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

/**
 * pcgen.base.formula is a package that is designed to read/write, validate, and
 * evaluate a formula. The formula may contain variables and functions,
 * including user-defined functions.
 * 
 * Note that the intent here is processing a SINGLE formula.
 * 
 * Other packages take on the responsibility of solving a system of formulas
 * (see pcgen.base.calculation and pcgen.base.solver).
 * 
 * Other packages also take on the responsibility of determining the current
 * value of a variable (this only uses a value, not how it is determined).
 * 
 * This formula system is designed to allow "integer-if-possible" mathematics.
 * If an integer is not possible, a Double is used, so this is not truly an
 * "arbitrary precision" system. It does mean that if the system is provided
 * "2+5" it will respond with 7 (the integer), not 7.0d (a Double). This also
 * means it has overflow characteristics related to that design, and MAKES NO
 * ATTEMPT to overflow from Integer to Long or Double.
 * 
 * Note that this cannot make that guarantee for any user-defined functions, as
 * they can return arbitrary values (generally any Number). This system will
 * accept any java.lang.Number that is provided, but only guarantees values work
 * within the calculating ability of Integer and Double as described above.
 */
package pcgen.base.formula;
