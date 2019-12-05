package pcgen.core;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class ClassTypeTest
{
    /**
     * test clone.
     */
    @Test
    public void testClone()
    {
        final ClassType expected = new ClassType();
        final ClassType actual = expected.clone();

        assertEquals(expected.getCRFormula(), actual.getCRFormula());
        assertEquals(expected.getXPPenalty(), actual.getXPPenalty());
        assertEquals(expected.isMonster(), actual.isMonster());
    }
}
