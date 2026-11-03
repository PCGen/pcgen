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
 * pcgen.base.formula.visitor is a package of visitors that traverse the tree of
 * objects that make up a formula.
 * 
 * In general all classes in this package are implementing the visitor design
 * pattern against the tree of objects. This allows a wide variety of functions
 * to be performed.
 * 
 * Any visitor that is intending to modify the tree should be clearly identified
 * as such. Due to the risk of tree modification, it should be a rare situation,
 * used for compatibility/conversion or diagnosis, not in a normal runtime
 * scenario.
 * 
 * Implementation note: There are a number of visitors defined in this package
 * that ignore the data parameter to the FormulaParserVisitor interface, and
 * some others that accumulate results and are therefore passing an object
 * through that parameter that could be more specific than Object. The visitors
 * in this package intentionally "fall back" on FormulaParserVisitor even though
 * it is not ideal. The reason for this is that the FormulaParserVisitor
 * interface is automatically built by the parser (it is a derived file). As a
 * result, if the parser is changed sufficiently to add a node type, it
 * guarantees that these visitors will break. (Otherwise, we'd end up in a
 * runtime scenario where an infinite loop would occur, resulting in a
 * StackOverflow ... but that would only be caught in a scenario that uses the
 * new node type, so it requires testing to be caught, an undesired risk of
 * delay). So this takes on some slightly annoying code to rapidly catch new
 * features needed as a result of a (hopefully rare) parser change.
 */
package pcgen.base.formula.visitor;
