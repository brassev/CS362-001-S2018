
/** A JUnit test class to test the class DataHandler. */


package calendar;

import org.junit.Test;

import java.util.GregorianCalendar;
import java.util.Random;
import static org.junit.Assert.*;

public class DataHandlerTest{
    private DataHandler fullDefault;
    private DataHandler empty;
    private DataHandler existing;
    private DataHandler noAuto;

    public DataHandlerTest() throws Throwable {
        Random rand = new Random();
        fullDefault = new DataHandler();
        empty = new DataHandler("Testing-" + rand.nextInt());

        genSampleData();
        existing = new DataHandler("existing.xml");
        noAuto = new DataHandler("existing.xml", false);
    }

    private void genSampleData() throws Throwable {
        DataHandler preExisting = new DataHandler("existing.xml", false);
        preExisting.saveAppt(new Appt(24, 6, 2018, "My Birthday", "Come Celebrate!", ""));
        preExisting.saveAppt(new Appt(25, 6, 2018, "The Twenty Fifth", "Howdy", ""));
        Appt repetative = new Appt(17, 30, 5, 7, 2018, "Weekly Meeting", "weekly for 6 events", "");
        repetative.setRecurrence(new int[]{1,3}, Appt.RECUR_BY_WEEKLY, 1, 6);
        preExisting.saveAppt(repetative);

        Appt monthly = new Appt(17, 30, 5, 7, 2018, "Weekly Meeting", "weekly for 6 events", "");
        monthly.setRecurrence(null, Appt.RECUR_BY_MONTHLY, 1, 6);
        preExisting.saveAppt(monthly);


        Appt yearly = new Appt(17, 30, 5, 7, 2018, "Weekly Meeting", "weekly for 6 events", "");
        yearly.setRecurrence(null, Appt.RECUR_BY_YEARLY, 1, 6);
        preExisting.saveAppt(yearly);

        preExisting.save();
    }

    @Test(timeout = 4000)
    public void apptRange_noRecurrence() throws Throwable  {
        GregorianCalendar low = new GregorianCalendar(2018, 6, 23);
        GregorianCalendar high = new GregorianCalendar(2018, 6, 26);
        assertEquals(2, existing.getApptRange(low, high).size());
    }

    @Test(timeout = 4000)
    public void apptRange_recurrence() throws Throwable  {
        GregorianCalendar low = new GregorianCalendar(2018, 7, 1);
        GregorianCalendar high = new GregorianCalendar(2018, 9, 1);
        assertEquals(6, existing.getApptRange(low, high).size());
    }

    @Test(timeout = 4000, expected = DateOutOfRangeException.class)
    public void apptRange_badDateRange() throws Throwable  {
        GregorianCalendar low = new GregorianCalendar(2018, 6, 23);
        GregorianCalendar high = new GregorianCalendar(2018, 6, 25);
        fullDefault.getApptRange(high, low);
    }

    @Test(timeout = 4000)
    public void deleteAppt() throws Throwable  {
        GregorianCalendar low = new GregorianCalendar(2018, 6, 23);
        GregorianCalendar high = new GregorianCalendar(2018, 6, 26);
        Appt deleteMe = new Appt(25, 3, 2018, "", "", "");
        existing.saveAppt(deleteMe);
        existing.deleteAppt(deleteMe);
        assertEquals(2, existing.getApptRange(low, high).size());
    }

    @Test(timeout = 4000)
    public void deleteAppt_autoOff() throws Throwable  {
        GregorianCalendar low = new GregorianCalendar(2018, 6, 23);
        GregorianCalendar high = new GregorianCalendar(2018, 6, 26);
        Appt deleteMe = new Appt(25, 3, 2018, "", "", "");
        noAuto.saveAppt(deleteMe);
        noAuto.deleteAppt(deleteMe);
        assertEquals(2, noAuto.getApptRange(low, high).size());
    }

    @Test(timeout = 4000)
    public void deleteAppt_invalid() throws Throwable  {
        GregorianCalendar low = new GregorianCalendar(2018, 6, 23);
        GregorianCalendar high = new GregorianCalendar(2018, 6, 26);
        Appt invalid = new Appt(70, 1, 2018, "", "", "");
        invalid.setValid();
        existing.deleteAppt(invalid);
        assertEquals(2, existing.getApptRange(low, high).size());
    }
}
