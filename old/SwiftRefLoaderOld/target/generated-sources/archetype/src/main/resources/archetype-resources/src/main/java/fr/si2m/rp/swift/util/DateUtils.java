#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {

	private static DateUtils	sSingleton	= null;
	private static boolean	sInit		= false;

	private String				mDateFormat	= null;

	private DateUtils(final String pDateFormat) {
		this.mDateFormat = pDateFormat;
	}

	public static boolean isInitialized() {
		return sInit;
	}

	public static synchronized void initialize(final String pDateFormat) {
		if (sSingleton == null) {
			sSingleton = new DateUtils(pDateFormat);
		}
		sInit = true;
	}

	public static DateUtils getInstance() {
		return sSingleton;
	}

	public Date stringToDate(final String pDate) throws ParseException {
		SimpleDateFormat lSdf = new SimpleDateFormat(this.mDateFormat);
		return lSdf.parse(pDate);
	}

	public String dateToString(final Date pDate) {
		SimpleDateFormat lDateFormat = new SimpleDateFormat(this.mDateFormat);
		return lDateFormat.format(pDate);
	}

	public static Date addDayMonthYear(final Date pDate, final int pDay, final int pMonth, final int pYear) {
		GregorianCalendar lCalendar = new GregorianCalendar();
		lCalendar.setTime(pDate);
		lCalendar.add(Calendar.DAY_OF_MONTH, pYear);
		lCalendar.add(Calendar.MONTH, pYear);
		lCalendar.add(Calendar.YEAR, pYear);
		return lCalendar.getTime();
	}

	public Date getCurrentDate() {
		GregorianCalendar lCalendar = new GregorianCalendar();
		Date lDate = lCalendar.getTime();
		return lDate;
	}


	public static void main(String[] args) {
		GregorianCalendar lCalendar = new GregorianCalendar();
		Date lDate = lCalendar.getTime();
		System.out.println(lDate.toString());

		DateUtils.initialize("ddMMyyyy");
		DateUtils aDateTest = DateUtils.getInstance();
		String aDateString = aDateTest.dateToString(lDate);
		System.out.println(aDateString);

		try {
			lDate = aDateTest.stringToDate(aDateString);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		System.out.println(lDate.toString());

		System.out.println(aDateTest.dateToString(lDate));
	}
}