/*
 * Copyright 2014 (C) Tom Parker <thpr@users.sourceforge.net> Copyright (c)
 * andrew wilson, 2010
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
package pcgen.base.formula.parse;

/**
 * The Operator enum is the list of legal one and two-argument operators that
 * can appear in a Formula.
 * 
 * Each implementation of an operator has a String representation (used to
 * reconstruct a Formula). These are dependent upon an OperatorAction to
 * actually perform the calculation.
 */
public enum Operator
{

	NOT("!"),
	MINUS("-"),

	EQ("=="),
	NEQ("!="),
	LT("<"),
	GT(">"),
	LE("<="),
	GE(">="),
	ADD("+"),
	SUB("-"),
	MUL("*"),
	DIV("/"),
	AND("&&"),
	OR("||"),
	EXP("^"),
	REM("%");

	private final String symbol;

	public String getSymbol()
	{
		return symbol;
	}

	private Operator(String op)
	{
		symbol = op;
	}
}
