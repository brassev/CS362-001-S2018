/** A JUnit test class to test the class ApptTest. */

package calendar;

import org.junit.Test;
import static org.junit.Assert.*;
import calendar.Appt;
import calendar.CalendarUtil;

import calendar.RefAppt;
public class ApptTest  {

  @Test(timeout = 4000)
  public void test00()  throws Throwable  {
      Appt appt0 = new Appt(15, 30, 9, 9, 2018, "Birthday Party", "This is my birthday party", "xyz@gmail.com");
      RefAppt appt1 = new RefAppt(15, 30, 9, 9, 2018, "Birthday Party", "This is my birthday party", "xyz@gmail.com");


       appt0.setValid();
      appt1.setValid();
      assertEquals("Both Appt should be Valid==True",appt0.getValid(),appt1.getValid());
  }
@Test(timeout = 4000)
 public void test01()  throws Throwable  {


}

}
