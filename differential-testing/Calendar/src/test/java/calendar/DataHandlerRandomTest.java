package calendar;

import java.util.Calendar;
import java.util.Random;
import java.util.LinkedList;
import java.util.GregorianCalendar;


import static org.junit.Assert.*;
import org.junit.Test;


/**
 * Random Test Generator  for DataHandler class.
 */

public class DataHandlerRandomTest {		 
		 




	private static final long TestTimeout = 60 * 500 * 1; /* Timeout at 30 seconds */
	private static final int NUM_TESTS=100;

	/**
	 * Return a randomly selected method to be tests !.
	 */
    public static String RandomSelectMethod(Random random){
        String[] methodArray = new String[] {"deleteAppt", "getApptRange"};// The list of the of methods to be tested in the Appt class

    	int n = random.nextInt(methodArray.length);// get a random number between 0 (inclusive) and  methodArray.length (exclusive)
    	            
        return methodArray[n] ; // return the method name 
        }
	/**
	 * Return a randomly selected appointments to recur Weekly,Monthly, or Yearly !.
	 */
    public static int RandomSelectRecur(Random random){
        int[] RecurArray = new int[] {Appt.RECUR_BY_WEEKLY,Appt.RECUR_BY_MONTHLY,Appt.RECUR_BY_YEARLY};// The list of the of setting appointments to recur Weekly,Monthly, or Yearly

    	int n = random.nextInt(RecurArray.length);// get a random number between 0 (inclusive) and  RecurArray.length (exclusive)
        return RecurArray[n] ; // return the value of the  appointments to recur 
        }	
	/**
	 * Return a randomly selected appointments to recur forever or Never recur  !.
	 */
    public static int RandomSelectRecurForEverNever(Random random){
        int[] RecurArray = new int[] {Appt.RECUR_NUMBER_FOREVER,Appt.RECUR_NUMBER_NEVER};// The list of the of setting appointments to recur RECUR_NUMBER_FOREVER, or RECUR_NUMBER_NEVER

    	int n = random.nextInt(RecurArray.length);// get a random number between 0 (inclusive) and  RecurArray.length (exclusive)
        return RecurArray[n] ; // return appointments to recur forever or Never recur 
        }	
    /**
     * Generate Random Tests that tests DataHandler Class.
     */
 
    
	 @Test
	public void randomtest() throws Throwable {

		long startTime = Calendar.getInstance().getTimeInMillis();
		long elapsed = Calendar.getInstance().getTimeInMillis() - startTime;

		System.out.println("Start testing...");

		for (int iteration = 0; elapsed < TestTimeout; iteration++) {

			try {
				long randomseed = System.currentTimeMillis(); // 10
				// System.out.println(" Seed:"+randomseed );
				Random random = new Random(randomseed); // randomseed

				// Creates a new DataHandler with the default filename and
				// auto-save setting
				DataHandler datahandler = new DataHandler();

				// Creates a specified number of Appointments.

				int numberSavedApp = ValuesGenerator.getRandomIntBetween(random, 1, 10);

				for (int k = 0; k < numberSavedApp; k++) {
					int startHour = ValuesGenerator.getRandomIntBetween(random,0, 23);
					int startMinute = ValuesGenerator.getRandomIntBetween(random, 0, 59);
					int startDay = ValuesGenerator.getRandomIntBetween(random,1, 31);
					int startMonth = ValuesGenerator.getRandomIntBetween(random, 1, 12);
					int startYear = ValuesGenerator.getRandomIntBetween(random,1, 2018);
					String title = "Birthday Party";
					String description = "This is my birthday party.";
					String emailAddress = "xyz@gmail.com";

					// Construct a new Appointment object with the initial data
					// Construct a new Appointment object with the initial data
					Appt appt = new Appt(startHour, startMinute, startDay,startMonth, startYear, title, description,emailAddress);
					int sizeArray = ValuesGenerator.getRandomIntBetween(random,0, 8);
					int[] recurDays = ValuesGenerator.generateRandomArray(random, sizeArray);
					int recur = DataHandlerRandomTest.RandomSelectRecur(random);
					int recurIncrement = ValuesGenerator.RandInt(random);
					int recurNumber = DataHandlerRandomTest.RandomSelectRecurForEverNever(random);
					appt.setRecurrence(recurDays, recur, recurIncrement,recurNumber);

					appt.setValid();

					// Saves an appointment's information to the XML data tree.
					datahandler.saveAppt(appt);
				}
				for (int i = 0; i < NUM_TESTS; i++) {
					try {
						LinkedList<CalDay> calDays = new LinkedList<CalDay>();

						int day = ValuesGenerator.getRandomIntBetween(random,1, 31);// rightnow.get(Calendar.DAY_OF_MONTH);

						int month = ValuesGenerator.getRandomIntBetween(random,1, 12);// rightnow.get(Calendar.MONTH);
						int year = ValuesGenerator.getRandomIntBetween(random,1, 2018);// rightnow.get(Calendar.YEAR);

						GregorianCalendar firstDay = new GregorianCalendar(year, month, day);
						GregorianCalendar lastDay = new GregorianCalendar(year,month, day);

						lastDay.add(firstDay.DAY_OF_MONTH, ValuesGenerator.getRandomIntBetween(random, 1, 10));

						calDays = (LinkedList<CalDay>) datahandler.getApptRange(firstDay, lastDay);

					} catch (DateOutOfRangeException e) {
						System.out.println("DateOutofRangeException " + e);
					}
					elapsed = (Calendar.getInstance().getTimeInMillis() - startTime);
					if ((i % 10) == 0 && i != 0 && elapsed < TestTimeout)
						System.out.println("elapsed time: " + elapsed + " of "+ TestTimeout);

				}

			} catch (NullPointerException e) {

				System.out.println("NullPointerException " + e);

			}

			elapsed = (Calendar.getInstance().getTimeInMillis() - startTime);
			if ((iteration % 10) == 0 && iteration != 0	&& elapsed < TestTimeout)
				System.out.println("elapsed time: " + elapsed + " of "+ TestTimeout);

		}
		// }

		System.out.println("Done testing...");
	} 
	 


	
}
