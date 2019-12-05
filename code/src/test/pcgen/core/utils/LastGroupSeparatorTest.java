package pcgen.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import pcgen.core.utils.LastGroupSeparator.GroupingMismatchException;

import org.junit.jupiter.api.Test;

public class LastGroupSeparatorTest
{

    @Test
    public void testNullConstructor()
    {
        try
        {
            new LastGroupSeparator(null);
            fail();
        } catch (NullPointerException | IllegalArgumentException e)
        {
            // OK
        }
    }

    @Test
    public void testCantDoThatYet()
    {
        LastGroupSeparator cs = new LastGroupSeparator("Test");
        try
        {
            cs.getRoot();
            fail();
        } catch (IllegalStateException e)
        {
            //OK
        }
    }

    @Test
    public void testSimple()
    {
        LastGroupSeparator cs = new LastGroupSeparator("Test");
        assertNull(cs.process());
        assertEquals("Test", cs.getRoot());
    }

    @Test
    public void testParenMismatch()
    {
        LastGroupSeparator cs = new LastGroupSeparator("Test(Open");
        try
        {
            cs.process();
            fail();
        } catch (GroupingMismatchException iae)
        {
            // OK
        }
        //Root undefined, don't test
    }

    @Test
    public void testSecondParenMismatch()
    {
        LastGroupSeparator cs = new LastGroupSeparator("Foo(Test(Open)");
        try
        {
            cs.process();
            fail();
        } catch (GroupingMismatchException iae)
        {
            // OK
        }
        //Root undefined, don't test
    }

    @Test
    public void testSecondMismatchParenClose()
    {
        LastGroupSeparator cs = new LastGroupSeparator("Test)Open");
        try
        {
            cs.process();
            fail();
        } catch (GroupingMismatchException iae)
        {
            // OK
        }
        //Root undefined, don't test
    }

    @Test
    public void testParenCloseBeforeOpen()
    {
        LastGroupSeparator cs = new LastGroupSeparator("Test)Open(");
        try
        {
            cs.process();
            fail();
        } catch (GroupingMismatchException iae)
        {
            // OK
        }
        //Root undefined, don't test
    }

    @Test
    public void testNormalParen()
    {
        LastGroupSeparator cs = new LastGroupSeparator(
                "Foo(Bar),Test(Goo,Free)");
        assertEquals("Goo,Free", cs.process());
        assertEquals("Foo(Bar),Test", cs.getRoot());
    }

    @Test
    public void testComplexMismatchParenOne()
    {
        LastGroupSeparator cs = new LastGroupSeparator(
                "Foo(BarWhee)),Test(Goo,Free)");
        try
        {
            cs.process();
            fail();
        } catch (GroupingMismatchException iae)
        {
            // OK
        }
        //Root undefined, don't test
    }

    @Test
    public void testComplexMismatchParenTwo()
    {
        LastGroupSeparator cs = new LastGroupSeparator(
                "Foo(Bar(Whee),Test(Goo,Free)");
        try
        {
            cs.process();
            fail();
        } catch (GroupingMismatchException iae)
        {
            // OK
        }
        //Root undefined, don't test
    }

    @Test
    public void testComplexOne()
    {
        LastGroupSeparator cs = new LastGroupSeparator(
                "Foo(Bar(Wheel),Har),Test(Goo,Free)");
        assertEquals("Goo,Free", cs.process());
        assertEquals("Foo(Bar(Wheel),Har),Test", cs.getRoot());
    }

    @Test
    public void testEmptyParenSimple()
    {
        LastGroupSeparator cs = new LastGroupSeparator("Test()");
        assertEquals("", cs.process());
        assertEquals("Test", cs.getRoot());
    }

    @Test
    public void testEmptyParenComplex()
    {
        LastGroupSeparator cs = new LastGroupSeparator(
                "Foo(Bar(Wheel),Har),Test()");
        assertEquals("", cs.process());
        assertEquals("Foo(Bar(Wheel),Har),Test", cs.getRoot());
    }

    @Test
    public void testComplexTwo()
    {
        LastGroupSeparator cs = new LastGroupSeparator(
                "Test(Goo,Free) (Bar(Wheel,Deal))");
        assertEquals("Bar(Wheel,Deal)", cs.process());
        assertEquals("Test(Goo,Free) ", cs.getRoot());
    }

    @Test
    public void testNotEndParen()
    {
        LastGroupSeparator cs = new LastGroupSeparator(
                "Test(Goo,Free) (Bar(Wheel,Deal)) Greatness");
        assertNull(cs.process());
        assertEquals("Test(Goo,Free) (Bar(Wheel,Deal)) Greatness", cs.getRoot());
    }
}
