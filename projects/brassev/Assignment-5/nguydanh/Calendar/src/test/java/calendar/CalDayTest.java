/** A JUnit test class to test the class CalDay. */

package calendar;

import org.junit.Test;

import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import java.util.LinkedList;


public class CalDayTest{
    private GregorianCalendar myBirthday;
    private CalDay nominal;
    private Appt party;
    private Appt electionDay;
    private CalDay invalid;
    public CalDayTest() {
        myBirthday = new GregorianCalendar(2018, 6, 24);
        party = new Appt(24, 6, 2018, "EB's BD", "Come Celebrate.", "brassev@oregonstate.edu");
        nominal = new CalDay(myBirthday);
        nominal.addAppt(party);

        electionDay = new Appt(2, 11, 2018, "Election Day", "VOTE!", "secretary@unitedstates.invalid");
        invalid = new CalDay();
    }

    @Test(timeout = 4000)
    public void invalidCalDay()  throws Throwable  {
        assertFalse(new CalDay().isValid());
    }

    @Test(timeout = 4000)
    public void sanity()  throws Throwable  {
        assertEquals(24, nominal.getDay());
        assertEquals(7, nominal.getMonth());
        assertEquals(2018, nominal.getYear());
    }

    @Test(timeout = 4000)
    public void bdIsValid()  throws Throwable  {
        assertTrue(nominal.isValid());
    }

    @Test(timeout = 4000)
    public void hasParty()  throws Throwable  {
        LinkedList<Appt> appts = nominal.getAppts();
        assertTrue(appts.contains(party));
    }

    @Test(timeout = 4000)
    public void noElectionDay()  throws Throwable  {
        // I can't remember what I was trying to test in adding an appt and then testing to make sure that it isn't in the CallDay.  Oh well.
        nominal.addAppt(electionDay);
        LinkedList<Appt> appts = nominal.getAppts();
        assertTrue(appts.contains(electionDay));
    }

    @Test(timeout = 4000)
    public void invalidAppt()  throws Throwable  {
        Appt invalid = new Appt(-1, 1, 0, "", "", "");
        invalid.setValid();
        nominal.addAppt(invalid);
        LinkedList<Appt> appts = nominal.getAppts();
        assertFalse(appts.contains(electionDay));
    }

    @Test(timeout = 4000)
    public void apptOrder()  throws Throwable  {
        Appt earlier = new Appt(4, 1, 24, 6, 2018, "", "", "");
        Appt later = new Appt(7, 1, 24, 6, 2018, "", "", "");
        nominal.addAppt(later);
        nominal.addAppt(earlier);
        assertEquals(party, nominal.getAppts().get(0));
        assertEquals(earlier, nominal.getAppts().get(1));
        assertEquals(later, nominal.getAppts().get(2));
    }

    @Test(timeout = 4000)
    public void iteratorOutput()  throws Throwable  {
        Appt earlier = new Appt(4, 1, 24, 6, 2018, "", "", "");
        Appt later = new Appt(7, 1, 24, 6, 2018, "", "", "");
        nominal.addAppt(later);
        nominal.addAppt(earlier);
        assertEquals(party, nominal.getAppts().get(0));
        assertEquals(earlier, nominal.getAppts().get(1));
        assertEquals(later, nominal.getAppts().get(2));
    }

    @Test(timeout = 4000)
    public void toString_test()  throws Throwable  {
        Appt earlier = new Appt(4, 1, 24, 6, 2018, "", "", "");
        Appt later = new Appt(7, 1, 24, 6, 2018, "", "", "");
        nominal.addAppt(later);
        nominal.addAppt(earlier);

        assertTrue(nominal.toString() != "");
    }

    @Test(timeout = 4000)
    public void toString_testInvalid()  throws Throwable  {
        assertEquals("", invalid.toString());
    }


    @Test(timeout = 4000)
    public void getFullInformationApp_test()  throws Throwable  {
        Appt earlier = new Appt(4, 1, 24, 6, 2018, "", "", "");
        Appt later = new Appt(13, 12, 24, 6, 2018, "", "", "");
        Appt noTime = new Appt(24, 6, 2018, "", "", "");
        Appt midnight = new Appt(0, 1, 24, 6, 2018, "", "", "");
        nominal.addAppt(later);
        nominal.addAppt(earlier);
        nominal.addAppt(noTime);
        nominal.addAppt(midnight);

        assertEquals("7-24-2018 \n\tEB's BD Come Celebrate. \n\t  \n\t12:01AM   \n\t4:01AM   \n\t1:12PM   ", nominal.getFullInfomrationApp(nominal));
    }

    @Test(timeout = 4000)
    public void getFullInformationApp_testInvalid()  throws Throwable  {
        assertEquals("7-24-2018 \n\tEB's BD Come Celebrate. ", invalid.getFullInfomrationApp(nominal));
    }
}
