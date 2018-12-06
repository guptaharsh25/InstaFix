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


    // Deliverable 4 Test Cases below

    public void testGetSPID(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        assertEquals("Check the SP id of Appointment after constructor", "sp", a.getSpID());
    }

    public void testGetClientID(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        assertEquals("Check the Client id of Appointment after constructor", "cl", a.getClientID());
    }

    public void testGetDay(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        assertEquals("Check the Day of Week of Appointment after constructor", Appointment.DayOfWeek.MONDAY, a.getDay());
    }

    public void testGetStartTime(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        assertEquals("Check the start time of Appointment after constructor", 0, a.getStartTime());
    }

    public void testGetEndTime(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        assertEquals("Check the end time of Appointment after constructor", 1, a.getEndTime());
    }

    public void testSetSPID(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        a.setSpID("s");
        assertEquals("Check the SP id of Appointment after constructor", "s", a.getSpID());
    }

    public void testSetClientID(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        a.setClientID("c");
        assertEquals("Check the Client id of Appointment after constructor", "c", a.getClientID());
    }

    public void testSetDay(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        a.setDay(Appointment.DayOfWeek.TUESDAY);
        assertEquals("Check the Day of Week of Appointment after constructor", Appointment.DayOfWeek.TUESDAY, a.getDay());
    }

    public void testSetStartTime(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        a.setStartTime(1);
        assertEquals("Check the start time of Appointment after constructor", 1, a.getStartTime());
    }

    public void testSetEndTime(){
        Appointment a = new Appointment("sp", "cl", Appointment.DayOfWeek.MONDAY, 0);
        a.setStartTime(1);
        assertEquals("Check the end time of Appointment after constructor", 2, a.getEndTime());
    }

    // Deliverable 3 Test Cases below
    /*
    public void testGetDate(){
        Availability a = new Availability("November 20, 2018", "9:00AM", "5:00PM");
        assertEquals("Check the date of the availability after constructor", "November 20, 2018", a.getDate());
    }

    public void testGetStartTime(){
        Availability a = new Availability("November 20, 2018", "9:00AM", "5:00PM");
        assertEquals("Check the start time of the availability after constructor", "9:00AM", a.getTimeStart());
    }
    */
    // Deliverable 2 Test Cases below
    /*
    public void testGetID(){
        Service s = new Service("efkwefwe", "service", 40);
        assertEquals("Check the id of the service after constructor", "efkwefwe", s.getId());
    }

    public void testGetName(){
        Service s = new Service("efkwefwe", "service", 40);
        assertEquals("Check the name of the service after constructor", "service", s.getName());
    }

    public void testGetRate(){
        Service s = new Service("efkwefwe", "service", 40);
        Double a = new Double((double) s.getRate());
        String actual = a.toString();
        assertEquals("Check the rate of the service after constructor", "40.0", actual);
    }

    public void testSetID(){
        Service s = new Service("efkwefwe", "service", 40);
        s.setId("newID");
        assertEquals("Check the id of the service after setting", "newID", s.getId());
    }

    public void testSetName(){
        Service s = new Service("efkwefwe", "service", 40);
        s.setName("newName");
        assertEquals("Check the name of the service after setting", "newName", s.getName());
    }
    */
}