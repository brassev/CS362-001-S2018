/** A JUnit test class to test the class ApptTest. */

package calendar;

import org.junit.Test;
import static org.junit.Assert.*;
import calendar.Appt;
import calendar.CalendarUtil;

public class ApptTest  {
    private Appt nominal_time;
    private Appt nominal;
    private Appt badTime;
    private Appt badDate;

    public ApptTest() {
        badDate = new Appt(31, 1, 2017, "Title", "Description", "sample@web.invalid");
        nominal_time = new Appt(3, 1, 1, 2,  2018, "Nominal", "Howdy", "");
        nominal = new Appt(1, 2,  2018, "Nominal", "Howdy", "");
        badTime = new Appt(-1, 1, 1, 2,  2018, "Invalid Time", "Howdy", "");
    }

    @Test(timeout = 4000)
    public void TrackingBug() throws Throwable {
        Appt test = new Appt(5, 40, 10, 6, 2018, "Title", "Description", "EmailAddress");
        test.setValid();
        assertTrue(test.getValid());
    }

    @Test(timeout = 4000)
    public void TrackingBug2() throws Throwable {
        Appt test = new Appt(0, 30, 29, 2, 400, "Title", "Description", "EmailAddress");
        test.setValid();
        assertFalse(test.getValid());
    }

	@Test(timeout = 4000)
	public void test00() throws Throwable {
		Appt appt0 = new Appt(15, 30, 9, 14, 2018, "Birthday Party", "This is my birthday party", "xyz@gmail.com");
		String string0 = appt0.toString();
		assertEquals(2, appt0.getRecurBy());
		assertFalse(appt0.isRecurring());
		assertEquals("\t14/9/2018 at 3:30pm ,Birthday Party, This is my birthday party\n", string0);
		assertEquals(0, appt0.getRecurIncrement());
		appt0.setValid();
	}

	@Test(timeout = 4000)
	 public void BadDateAtCreation()  throws Throwable {
        assertTrue(badDate.getValid());
	}

	@Test(timeout = 4000)
	public void BadTimeAtCreation() throws Throwable {
	    assertTrue(badTime.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadMonth() throws Throwable {
        nominal_time.setStartMonth(0);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadMonth_high() throws Throwable {
        nominal_time.setStartMonth(13);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadHour() throws Throwable {
        nominal_time.setStartHour(-1);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadHour_high() throws Throwable {
        nominal_time.setStartHour(24);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadMinute() throws Throwable {
        nominal_time.setStartMinute(-1);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadMinute_high() throws Throwable {
        nominal_time.setStartMinute(60);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadYear() throws Throwable {
        nominal_time.setStartYear(-1);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadDay() throws Throwable {
        nominal_time.setStartMonth(0);
        nominal_time.setStartDay(31);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void setValid_BadDay_low() throws Throwable {
        nominal_time.setStartDay(-1);
        nominal_time.setValid();
        assertFalse(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void BadTimeSetValid() throws Throwable {
        badTime.setValid();
        assertFalse(badTime.getValid());
    }

    @Test(timeout = 4000)
    public void nominal_SetValid() throws Throwable {
        nominal_time.setValid();
        assertTrue(nominal_time.getValid());
    }

    @Test(timeout = 4000)
    public void startHour() throws Throwable {
        badTime.setStartHour(1);
        assertEquals(1, badTime.getStartHour());
        badTime.setStartHour(2);
        assertEquals(2, badTime.getStartHour());
    }

    @Test(timeout = 4000)
    public void nullDescription() throws Throwable {
        nominal.setDescription(null);
        assertEquals("", nominal.getDescription());
    }

    @Test(timeout = 4000)
    public void filledDescription() throws Throwable {
        nominal.setDescription("Hello World");
        assertEquals("Hello World", nominal.getDescription());
    }

    @Test(timeout = 4000)
    public void nullTitle() throws Throwable {
        nominal.setTitle(null);
        assertEquals("", nominal.getTitle());
    }

    @Test(timeout = 4000)
    public void filledTitle() throws Throwable {
        nominal.setTitle("Hello World");
        assertEquals("Hello World", nominal.getTitle());
    }

    @Test(timeout = 4000)
    public void hasTime_nominal() throws Throwable {
        assertEquals(nominal.hasTimeSet(), false);
    }

    @Test(timeout = 4000)
    public void hasTime_nominal_time() throws Throwable {
        assertEquals(nominal_time.hasTimeSet(), true);
    }

    @Test(timeout = 4000)
    public void isOn_StartDate() throws Throwable {
        assertTrue(nominal.isOn(nominal.getStartDay(), nominal.getStartMonth(), nominal.getStartYear()));
    }

    @Test(timeout = 4000)
    public void isOn_wrongDay() throws Throwable {
        assertFalse(nominal.isOn(nominal.getStartDay() + 1, nominal.getStartMonth(), nominal.getStartYear()));
    }

    @Test(timeout = 4000)
    public void isOn_wrongMonth() throws Throwable {
        assertFalse(nominal.isOn(nominal.getStartDay(), nominal.getStartMonth() + 1, nominal.getStartYear()));
    }

    @Test(timeout = 4000)
    public void isOn_wrongYear() throws Throwable {
        assertFalse(nominal.isOn(nominal.getStartDay(), nominal.getStartMonth(), nominal.getStartYear() + 1));
    }

    @Test(timeout = 4000)
    public void isOn_weekly_wrongDay() throws Throwable {
        nominal.setRecurrence(null,Appt.RECUR_BY_WEEKLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay() + 1, nominal.getStartMonth(), nominal.getStartYear()));
    }

    @Test(timeout = 4000)
    public void isOn_weekly_wrongMonth() throws Throwable {
        nominal.setRecurrence(null,Appt.RECUR_BY_WEEKLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay(), nominal.getStartMonth() + 1, nominal.getStartYear()));
    }

    @Test(timeout = 4000)
    public void isOn_weekly_wrongYear() throws Throwable {
        nominal.setRecurrence(null,Appt.RECUR_BY_WEEKLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay(), nominal.getStartMonth(), nominal.getStartYear() + 1));
    }

    @Test(timeout = 4000)
    public void RecurrenceWeeklyMW_3Times_TestNextM() throws Throwable {
        int[] days = new int[2];
        days[0] = 0;
        days[1] = 2;
        nominal.setRecurrence(days, Appt.RECUR_BY_WEEKLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay() + 7, nominal.getStartMonth(), nominal.getStartYear()));
    }

    @Test(timeout = 4000)
    public void RecurrenceWeeklyMW_3Times_TestNextW() throws Throwable {
        int[] days = new int[2];
        days[0] = 0;
        days[1] = 2;
        nominal.setRecurrence(days, Appt.RECUR_BY_WEEKLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay() + 9, nominal.getStartMonth(), nominal.getStartYear()));
    }

    @Test(timeout = 4000)
    public void RecurrenceNoRecurringDays_TestWeekLater() throws Throwable {
        nominal.setRecurrence(null, Appt.RECUR_BY_WEEKLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay() + 7, nominal.getStartMonth(), nominal.getStartYear()));
    }

    @Test(timeout = 4000)
    public void isOn_Yearly_oneYearLater() throws Throwable {
        nominal.setRecurrence(null, Appt.RECUR_BY_YEARLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay(), nominal.getStartMonth(), nominal.getStartYear() + 1));
    }

    @Test(timeout = 4000)
    public void isOn_Yearly_afterRecurNumber() throws Throwable {
        nominal.setRecurrence(null, Appt.RECUR_BY_YEARLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay(), nominal.getStartMonth(), nominal.getStartYear() + 4));
    }

    @Test(timeout = 4000)
    public void isOn_Yearly_wrongDay() throws Throwable {
        nominal.setRecurrence(null, Appt.RECUR_BY_YEARLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay() + 1, nominal.getStartMonth(), nominal.getStartYear() + 1));
    }

    @Test(timeout = 4000)
    public void isOn_Yearly_wrongMonth() throws Throwable {
        nominal.setRecurrence(null, Appt.RECUR_BY_YEARLY, 1, 3);
        assertFalse(nominal.isOn(nominal.getStartDay(), nominal.getStartMonth() + 1, nominal.getStartYear() + 1));
    }

    @Test(timeout = 4000)
    public void toString_nominal_time() throws Throwable {
        // I know that this is not the same as the string produced, however, I don't think that the toString function in Appt was implemented as intended.  There are many bugs there.
        String correctStr = "\t2/1/2018 at -1:-1am ,Nominal, Howdy\n";
        assertEquals(correctStr, nominal.toString());
    }


    @Test(timeout = 4000)
    public void toString_invalid() throws Throwable {
        assertEquals("\t2/1/2018 at -1:1am ,Invalid Time, Howdy\n", badTime.toString());
    }
}
