package ca.harshgupta.seg2105_project;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    public void testGetID(){
        Service s = new Service("efkwefwe", "service", 40);
        assertEquals("Check the name of the product", "efkwefwe", s.getId());
    }

    public void testGetName(){
        Service s = new Service("efkwefwe", "service", 40);
        assertEquals("Check the name of the product", "service", s.getName());
    }

    public void testGetRate(){
        Service s = new Service("efkwefwe", "service", 40);
        Double a = new Double((double) s.getRate());
        String actual = a.toString();
        assertEquals("Check the price of the product", "40.0", actual);
    }
}