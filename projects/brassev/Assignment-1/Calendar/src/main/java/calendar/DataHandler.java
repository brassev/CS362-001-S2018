/**
* DataHandler.java
*
**/

package calendar;

import java.util.*;
import java.io.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;
import org.xml.sax.*;


import org.w3c.dom.*;

/**
* This handles all data that is read and written from disk.
**/
public class DataHandler {
    
    /** Default file name **/
    private static String DEFAULT_FILE_NAME = "calendar.xml";
    
    /** Default auto-save setting after each change to an appointment **/
    private static boolean DEFAULT_AUTO_SAVE = true;
    
    /** Very top of the XML document **/
    private Document document;
    
    /** Stores whether or not the datahandler was initialized **/
    private boolean valid;
    
    /** The file name (with the path) to read and write XML to **/
    private String fileName;
    
    /** The auto-save setting to use **/
    private boolean autoSave;
    
    /**
     * Default constructor
     * Creates a new DataHandler with the default filename and auto-save setting
     **/
    public DataHandler() throws IOException {
        //Call the other constructor using the default file name.
        //The default file is stored in the user's home directory.
        this(System.getProperty("user.dir") +
        System.getProperty("file.separator") + DEFAULT_FILE_NAME);
    }
    
    /**
     * Creates a new data handler with the specified filename.
     * Uses the default auto-save setting.
     **/
    public DataHandler(String fileName) throws IOException {
        this(fileName, DEFAULT_AUTO_SAVE);
    }
    
    /**
     * Creates a new data handler with the specified filename and auto-save
     * setting
     **/
    public DataHandler(String fileName, boolean autoSave) throws IOException {
        //Record the line separator for later use
        String separator = System.getProperty("line.separator");
        
        //The data handler isn't valid unless everything is set up correctly
        valid = false;
        
        //Record the auto-save setting and file name
        this.autoSave = autoSave;
        setFileName(fileName);
        
        //Read the XML file...
        //Step 1. Set up document builder factory and its settings
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setValidating(false);
        dbf.setIgnoringComments(false);
        dbf.setIgnoringElementContentWhitespace(false);
        dbf.setCoalescing(false);
        dbf.setExpandEntityReferences(true);
        
        //Step 2. Create a document builder from the factory
        DocumentBuilder db = null;
        try {
            db = dbf.newDocumentBuilder();
        }
        catch (ParserConfigurationException pce) {
            throw new IOException("Invalid parser configuration." + separator +
            pce.getMessage());
        }
        db.setErrorHandler(new XmlParserErrorHandler());
        
        //Step 3. Parse the file now (or a default string if it doesn't exist)
        Document doc = null;
        try {
            File file = new File(fileName);
            if (file.isFile()) {
                doc = db.parse(file);
            }
            else {
                String newFile = "<?xml version=\"1.0\"?><calendar/>";
                InputSource is = new InputSource(new StringReader(newFile));
                doc = db.parse(is);
            }
        }
        catch (SAXException se) {
            throw new IOException("Unable to read document." + separator +
            se.getMessage());
        }
        
        //Set the document node now
        setDocument(doc);
        valid = true;
    }
    
    /**
     * Retrieves a range of appointments between two dates.
     * @return A list of all of the CalDays between firstDate (inclusive)
     *  and lastDate (exclusive) with their respective appointments. If the 
     * data handler has not been initialized correctly, null is returned.
     * @throws DateOutOfRangeException If any of the days constructed by the
     *  given values are invalid, or if date 2 is not after date 1.
     **/
    public List<CalDay> getApptRange(GregorianCalendar firstDay, 
            GregorianCalendar lastDay) throws DateOutOfRangeException {

            //Internal Diagnositic Messages turned on when true
            boolean diagnose = false;
            
            //If the data handler isn't initialized return null
            if (isValid() == false) {
                return null;
            }
            
            //Make sure that the first day is before the last day
            if (!firstDay.before(lastDay)) {
                throw new DateOutOfRangeException("Second date specified is not " +
                    "before the first date specified.");
            }
            
            //Create a linked list of calendar days to return
            LinkedList<CalDay> calDays = new LinkedList<CalDay>();
            
            //Create the first CalDay object with the starting date and add to list
            GregorianCalendar nextDay = (GregorianCalendar) firstDay.clone();
            while (nextDay.before(lastDay)) {
                calDays.add(new CalDay(nextDay));
                nextDay.add(nextDay.DAY_OF_MONTH, 1);
            }
            
            if (diagnose) {
                System.out.println("=======================================");
                System.out.println("DEBUGGING GETTING OF APPOINTMENTS      ");
            }
            
            //Retrieve the root node - <calendar>
            Document doc = getDocument();
            Element root = doc.getDocumentElement();
            
            if (diagnose) {
                System.out.println("Root node: " + root.getTagName());
                System.out.println("All following nodes should be appt nodes.");
            }
            
            //Retrieve the root's children - <appt> nodes
            NodeList appts = root.getChildNodes();
            for (int i = 0; i < appts.getLength(); i++) {
                Element currentAppt = (Element) appts.item(i);
                
                if (diagnose) {
                    System.out.println("Nodes under the root: " + 
                        currentAppt.getTagName());
                }
                
                //For this appointment, get the values of all fields
                NodeList fieldNodes = currentAppt.getChildNodes();
                Hashtable<String, String> fields = new Hashtable<String, String>();
                if (diagnose) {
                    System.out.println("Preparing to read each field for the appt");
                }
                for (int j = 0; j < fieldNodes.getLength(); j++) {
                    Element currentField = (Element) fieldNodes.item(j);
                    String fieldName = currentField.getTagName();
                    if (diagnose) {
                        System.out.println("Reading field: " + fieldName);
                    }
                    String fieldValue = "";
                    NodeList fieldValueNodes = currentField.getChildNodes();
                    for (int k = 0; k < fieldValueNodes.getLength(); k++) {
                        Text text = (Text)fieldValueNodes.item(k);
                        fieldValue += text.getData();
                    }
                    if (diagnose) {
                        System.out.println("Reading field's value: " + fieldValue);
                    }
                    
                    fields.put(fieldName, fieldValue);
                }
                
                //Construct a new Appointment object with the data found
                Appt appt = new Appt( 
                            Integer.parseInt((String)fields.get("startHour")),
                            Integer.parseInt((String)fields.get("startMinute")),
                            Integer.parseInt((String)fields.get("startDay")),
                            Integer.parseInt((String)fields.get("startMonth")),
                            Integer.parseInt((String)fields.get("startYear")),
                            (String)fields.get("title"),
                            (String)fields.get("description"),
                            (String)fields.get("location"));
                LinkedList<String> recurDaysList = new LinkedList<String>();
                StringTokenizer stk = 
                    new StringTokenizer((String)fields.get("recurDays"));
                while (stk.hasMoreTokens()) {
                    recurDaysList.add(stk.nextToken(","));
                }
                int[] recurDaysArr = new int[recurDaysList.size()];
                for (int j = 0; j < recurDaysList.size(); j++) {
                    recurDaysArr[j] = Integer.parseInt((String)recurDaysList.get(j));
                }
                appt.setRecurrence(recurDaysArr, 
                            Integer.parseInt((String)fields.get("recurBy")),
                            Integer.parseInt((String)fields.get("recurIncrement")),
                            Integer.parseInt((String)fields.get("recurNumber")));
                //**When changing these later, remember to check for NULL ***/
                
                if (diagnose) {
                    System.out.println("Calculating appointment occurrences.");
                }
                //Figure out which days the appointment occurs on
                LinkedList<GregorianCalendar>  apptOccursOnDays = 
                    getApptOccurences(appt, firstDay, lastDay);
                
                if (diagnose) { 
                    System.out.println("This appointment occurs on: ");
                }
                
                //For each day in the list, calculate the difference between the
                //first day and the day of occurrence and add the appointment to 
                //the correct CalDay
                int daysDifference = 0;
                nextDay = (GregorianCalendar)firstDay.clone();
                Iterator itr = apptOccursOnDays.iterator();
                while (itr.hasNext()) {
                    GregorianCalendar apptOccursOn = (GregorianCalendar)itr.next();
                    
                    if (diagnose) {
                        System.out.println("\t" + apptOccursOn);
                    }
                    
                    while(nextDay.before(apptOccursOn)) {
                        daysDifference++;
                        nextDay.add(nextDay.DAY_OF_MONTH, 1);
                    }
                    
                    CalDay calDayOfAppt = (CalDay)calDays.get(daysDifference);
                    calDayOfAppt.addAppt(appt);
                            
                }
                
                //This appointment has been added to all CalDays
                if (diagnose) {
                    System.out.println("This appointment is done.");
                }
            } //for nodelist
            return calDays;
        }

    
    /**
     * This takes the given appointment and constructs a linked list of 
     * GregorianCalendar's, each of which represent a day when the appointment
     * occurs. The days are guaranteed to be between firstDay (inclusive) and
     * lastDay (exclusive). They are guaranteed to be in order.
     **/
    private static LinkedList<GregorianCalendar> getApptOccurences(Appt appt, 
        GregorianCalendar firstDay, GregorianCalendar lastDay) {
        
        LinkedList<GregorianCalendar> result = new LinkedList<GregorianCalendar>();
        
        //Make sure that the firstDay is before the last day
        if (!firstDay.before(lastDay)) {
            return result;
        }
        
        //Get the first recurrence taken care of
        GregorianCalendar occurrenceDay = 
                new GregorianCalendar(appt.getStartYear(), appt.getStartMonth()-1, 
                    appt.getStartDay());
        
        //If the first occurrence is after the last day, then it doesn't matter
        //when it recurs because those dates must be after the last day too
        if (!occurrenceDay.before(lastDay)) {
            return result;
        }
        
        //Make sure that there is a limited number of recurrences
        for (int i = 0; i < appt.getRecurNumber()+1; i++) {
            
            //Add the day of occurrence to the list if it is after the first day
            if (!occurrenceDay.before(firstDay)) {
                result.add(occurrenceDay);
            }
            
            //Calculate the next recurrence day
            occurrenceDay = getNextApptOccurrence(appt, occurrenceDay);
            if (occurrenceDay == null) {
                break;
            }
                        
            //Keep cycling while the occurence day is in range
            if (!occurrenceDay.before(lastDay)) {
                break;
            }
        }
        
        return result;
    }
    
    /**
     * Calculates the next recurring day in the given appointment. If the 
     * appointment does not recur it returns null. If the date cannot be 
     * calculated for some reason it returns null.
     **/
    private static GregorianCalendar getNextApptOccurrence(Appt appt, 
            GregorianCalendar day) {
        //If the appointment does not recur then return null
        if (!appt.isRecurring()) {
            return null;
        }
        
        //Leave the original day untouched.
        GregorianCalendar nextDay = (GregorianCalendar)day.clone();
        
        //This depends on the recurrence settings
        switch (appt.getRecurBy()) {
            case Appt.RECUR_BY_WEEKLY:
                int[] recurDays = appt.getRecurDays();
                
                //If the user specified weekly recurrence and didn't specify
                //which week days, then assume it is the same week day of the
                //first occurrence
                if (recurDays.length == 0) {
                    //Add 7 days and return that by default
                    nextDay.add(nextDay.DAY_OF_MONTH, 7);
                    return nextDay;
                }
                
                //The user did specify weekly recurrence, so increment the
                //day until it falls on a weekday the user specified
                for (int k = 0; k < 7; k++) {
                    nextDay.add(nextDay.DAY_OF_MONTH, 1);
                    int newDayOfWeek = nextDay.get(nextDay.DAY_OF_WEEK);
                
                    for (int i = 0; i < recurDays.length; i++) {
                        //If the calendar is set to a day of the week that the
                        //appt recurs on then return that day.
                        if (recurDays[i] == newDayOfWeek) {
                            return nextDay;
                        }
                    }
                }
                
                //The loop above should have found a day. If the program 
                //execution is here then the weekdays specified are not in the
                //range of valid Gregorian Calendar Days. Return null here.
                return null;
            case Appt.RECUR_BY_MONTHLY:
                //Just increment the month and return the day. Not sure what
                //happens when the day is 31 and the next month has 30 days...
                nextDay.add(nextDay.MONTH, 1);
                return nextDay;
            case Appt.RECUR_BY_YEARLY:
                //Just increment the year. The only possible problem is an 
                //appointment that recurs on February 29.
                nextDay.add(nextDay.YEAR, 1);
                return nextDay;
        }
        return null;
    }
    
    /**
     * Saves an appointment's information to the XML data tree. Does not
     * write a new XML file to disk.
     * @return True if the appointment was saved correctly
     **/
    public boolean saveAppt(Appt appt) {
        
        //First things first - Do not save invalid appointments
        if (!appt.getValid()) {
            return false;
        }
        
        //Add a new appt element node to the XML tree
        Document doc = getDocument();
        Element root = doc.getDocumentElement();
        Element apptElement = doc.createElement("appt");
        root.appendChild(apptElement);
        
         /* Take the appointment's data and put it in XML tree of elements.
          * Unfortunately, this is a little sloppy because the work needs to be
          * done someplace. I am explaining one of the entries. The others
          * are similar. **/
        //Step 1 - Create a new element node that will store the start hour
        Element startHourElement = doc.createElement("startHour");
        
        //Step 2 - Create a new text node that stores the data (these aren't
        //  elements) The appt data is an integer and it is converted to string
        Text startHourText = doc.createTextNode(
        Integer.toString(appt.getStartHour()));
        
        //Step 3 - The text node is a child of the element node
        startHourElement.appendChild(startHourText);
        
        //Step 4 - The element is a child of the appt node
        apptElement.appendChild(startHourElement);
        
        //The above is repeated for each element
        Element startMinuteElement = doc.createElement("startMinute");
        Text startMinuteText = doc.createTextNode(
            Integer.toString(appt.getStartMinute()));
        startMinuteElement.appendChild(startMinuteText);
        apptElement.appendChild(startMinuteElement);
        
        Element startDayElement = doc.createElement("startDay");
        Text startDayText = doc.createTextNode(
            Integer.toString(appt.getStartDay()));
        startDayElement.appendChild(startDayText);
        apptElement.appendChild(startDayElement);
        
        Element startMonthElement = doc.createElement("startMonth");
        Text startMonthText = doc.createTextNode(
            Integer.toString(appt.getStartMonth()));
        startMonthElement.appendChild(startMonthText);
        apptElement.appendChild(startMonthElement);
        
        Element startYearElement = doc.createElement("startYear");
        Text startYearText = doc.createTextNode(
        Integer.toString(appt.getStartYear()));
        startYearElement.appendChild(startYearText);
        apptElement.appendChild(startYearElement);
        
        
        Element titleElement = doc.createElement("title");
        Text titleText = doc.createTextNode(appt.getTitle());
        titleElement.appendChild(titleText);
        apptElement.appendChild(titleElement);
        
        Element descriptionElement = doc.createElement("description");
        Text descriptionText = doc.createTextNode(appt.getDescription());
        descriptionElement.appendChild(descriptionText);
        apptElement.appendChild(descriptionElement);
        
        
        Element emailAddressElement = doc.createElement("emailAddress");
        Text emailAddressText = doc.createTextNode(appt.getEmailAddress());
        emailAddressElement.appendChild(emailAddressText);
        apptElement.appendChild(emailAddressElement);
        
        Element recurDaysElement = doc.createElement("recurDays");
        String recurDaysString = "";
        for (int i = 0; i < appt.getRecurDays().length; i++) {
            recurDaysString += appt.getRecurDays()[i] + ",";
        }
        Text recurDaysText = doc.createTextNode(recurDaysString);
        recurDaysElement.appendChild(recurDaysText);
        apptElement.appendChild(recurDaysElement);
        
        Element recurByElement = doc.createElement("recurBy");
        Text recurByText = doc.createTextNode(
            Integer.toString(appt.getRecurBy()));
        recurByElement.appendChild(recurByText);
        apptElement.appendChild(recurByElement);
        
        Element recurIncrementElement = doc.createElement("recurIncrement");
        Text recurIncrementText = doc.createTextNode(
            Integer.toString(appt.getRecurIncrement()));
        recurIncrementElement.appendChild(recurIncrementText);
        apptElement.appendChild(recurIncrementElement);
        
        Element recurNumberElement = doc.createElement("recurNumber");
        Text recurNumberText = doc.createTextNode(
            Integer.toString(appt.getRecurNumber()));
        recurNumberElement.appendChild(recurNumberText);
        apptElement.appendChild(recurNumberElement);
        
        //Let the appointment know where it is stored in the XML tree
        ((Appt) appt).setXmlElement(apptElement);
        
        //Handle the auto saving option
        if (isAutoSave()) {
            return save();
        }
        else {
            return true;
        }
    }
    
    /**
     * Deletes the appointment's information from the XML data tree. Does not
     * write a new XML file to disk.
     * @return True if the appointment is deleted successfully.
     **/
    public boolean deleteAppt(Appt appt) {
        //Do not do anything to invalid appointments
        if (!appt.getValid()) {
            return false;
        }
        
        //Remove the appointment from the XML tree if applicable
        Element apptElement = appt.getXmlElement();
        if (apptElement == null) {
            return false;
        }
        Node parentNode = apptElement.getParentNode();
        parentNode.removeChild(apptElement);
        appt.setXmlElement(null);
        
        if (isAutoSave()) {
            return save();
        }
        else {
            return true;
        }
    }
    
    /**
     * Writes a new XML file to disk.
     **/
    public boolean save() {
        
        try {
            //Create a transformer. The transformer is not really transforming
            //much, it is just outputting XML
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer transformer = tFactory.newTransformer();
            
            DOMSource source = new DOMSource(getDocument());
            File outputFile = new File(getFileName());
            StreamResult result = new StreamResult(outputFile);
            transformer.transform(source, result);
        }
        //These exceptions should never be thrown because the structure of
        //the XML document is preset and they are simple, any problems
        //should be covered in testing. Therefore, they just return false.
        catch (TransformerConfigurationException tce) {
            return false;
        }
        catch (TransformerException te) {
            return false;
        }
        
        return true;
    }
    
    /**
     * @return True if autoSave is set
     **/
    private boolean isAutoSave() {
        return autoSave;
    }
    
    /** 
     * @return True if the dataHandler is initialized correctly
     **/
    private boolean isValid() {
        return valid;
    }
    
    /** Sets parent **/
    private void setDocument(Document document) {
        this.document = document;
    }
    
    /** Sets fileName **/
    private void setFileName(String fileName) {
        this.fileName = fileName;
    }
    
    /** Gets parent **/
    private Document getDocument() {
        return document;
    }
    
    /** Gets filename **/
    private String getFileName() {
        return fileName;
    }
    
    

}