package com.whl.wako.kernel.util;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;


public class DateUtils  {
	public final static String DATE_FORMAT_NORMAL = "yyyyMMddHHmmss";
	public final static String DATE_FORMAT_SEQ = "yyyyMMddHHmmssSSS";
	public final static String DATE_FORMAT_SHORT_SEQ = "HHmmssSSS";
	public final static String DATE_FORMAT_DEFAULT = "yyyy-MM-dd";
	public final static String DATE_FORMAT_SOLIDUS = "yyyy/MM/dd";
	public final static String DATE_FORMAT_YEAR = "yyyy";
	public final static String DATE_FORMAT_MON = "yyyyMM";
	public final static String DATE_FORMAT_COMPACT = "yyyyMMdd";
	public final static String DATE_FORMAT_HOUR = "yyyyMMddHH";
	public final static String DATE_FORMAT_UTC_DEFAULT = "MM-dd-yyyy";
	public final static String DATE_FORMAT_UTC_SOLIDUS = "MM/dd/yyyy";
	public final static String DATE_FORMAT_CHINESE = "yyyy年MM月dd日";
	public final static String DATE_FORMAT_CHINESE_TO_MIN = "yyyy年MM月dd日 hh:mm";
	public final static String DATE_TIME_FORMAT_CHINESE = "yyyy年MM月dd日 HH时mm分ss秒";
    public static final String MON_FORMAT_CHINESE = "yyyy年MM月";
	public final static String DATE_TIME_FORMAT_DEFAULT = "yyyy-MM-dd HH:mm:ss";
	public final static String DATE_TIME_FORMAT_DEFAULT_SEQ = "yyyy-MM-dd HH:mm:ss.SSS";
	public final static String DATE_TIME_FORMAT_SOLIDUS = "yyyy/MM/dd HH:mm:ss";
	public final static String DATE_TIME_FORMAT_SOLIDUS_SEQ = "yyyy/MM/dd HH:mm:ss.SSS";
	public final static String DATE_TIME_FORMAT_UTC_DEFAULT = "MM-dd-yyyy HH:mm:ss";
	public final static String DATE_TIME_FORMAT_UTC_SOLIDUS = "MM/dd/yyyy HH:mm:ss";
	public static final String TIME_FORMAT = "HH:mm:ss";
	public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
	public static final String CMS_DRAW_SEQUENCE_FORMAT = "yyyyMMddhhmmss";
	public final static int   SECONDS_PER_DAY  = 60*60*24;
	public final static int   SECONDS_PER_HOUR = 60*60;
	public final static int   SECONDS_PER_HALF_HOUR = 60*30;
	public final static int   SECONDS_PER_TEN_MIN = 60*10;
	public final static int   SECONDS_PER_FIVE_MIN = 60*6;
	public final static int   SECONDS_PER_MIN  = 60;
	private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);
	private static final Map<String, Pattern> dateFormatRegisterMap = Maps.newLinkedHashMap();
	private static final Map<String, SimpleDateFormat> dateFormatMap = new HashMap<String, SimpleDateFormat>();

	static {
		dateFormatRegisterMap.put(DATE_TIME_FORMAT_DEFAULT, Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}$"));
		dateFormatRegisterMap.put(DATE_FORMAT_NORMAL, Pattern.compile("^\\d{14}$"));
		dateFormatRegisterMap.put(DATE_FORMAT_COMPACT, Pattern.compile("^\\d{8}$"));
		dateFormatRegisterMap.put(DATE_FORMAT_SEQ, Pattern.compile("^\\d{17}$"));
		dateFormatRegisterMap.put(DATE_FORMAT_DEFAULT, Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}$"));
		dateFormatRegisterMap.put(DATE_FORMAT_SOLIDUS, Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}$"));
		dateFormatRegisterMap.put(DATE_FORMAT_UTC_DEFAULT, Pattern.compile("^\\d{1,2}-\\d{1,2}-\\d{4}$"));
		dateFormatRegisterMap.put(DATE_FORMAT_UTC_SOLIDUS, Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}$"));
		dateFormatRegisterMap.put(DATE_TIME_FORMAT_SOLIDUS, Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}$"));
		dateFormatRegisterMap.put(DATE_TIME_FORMAT_UTC_DEFAULT,Pattern.compile("^\\d{1,2}-\\d{1,2}-\\d{4}\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}$"));
		dateFormatRegisterMap.put(DATE_TIME_FORMAT_UTC_SOLIDUS,Pattern.compile("^\\d{1,2}/\\d{1,2}/\\d{4}\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}$"));
		dateFormatRegisterMap.put(DATE_TIME_FORMAT_DEFAULT_SEQ,Pattern.compile("^\\d{4}-\\d{1,2}-\\d{1,2}\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}$"));
		dateFormatRegisterMap.put(DATE_TIME_FORMAT_SOLIDUS_SEQ,Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}\\s*\\d{1,2}:\\d{1,2}:\\d{1,2}.\\d{1,3}$"));

		dateFormatMap.put(DATE_FORMAT_DEFAULT, new SimpleDateFormat(DATE_FORMAT_DEFAULT));
		dateFormatMap.put(DATE_FORMAT_SOLIDUS, new SimpleDateFormat(DATE_FORMAT_SOLIDUS));
		dateFormatMap.put(DATE_FORMAT_COMPACT, new SimpleDateFormat(DATE_FORMAT_COMPACT));
		dateFormatMap.put(DATE_FORMAT_SEQ, new SimpleDateFormat(DATE_FORMAT_SEQ));
		dateFormatMap.put(DATE_FORMAT_NORMAL, new SimpleDateFormat(DATE_FORMAT_NORMAL));
		dateFormatMap.put(DATE_FORMAT_UTC_DEFAULT, new SimpleDateFormat(DATE_FORMAT_UTC_DEFAULT));
		dateFormatMap.put(DATE_FORMAT_UTC_SOLIDUS, new SimpleDateFormat(DATE_FORMAT_UTC_SOLIDUS));
		dateFormatMap.put(DATE_TIME_FORMAT_DEFAULT, new SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT));
		dateFormatMap.put(DATE_TIME_FORMAT_DEFAULT_SEQ, new SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT_SEQ));
		dateFormatMap.put(DATE_TIME_FORMAT_SOLIDUS, new SimpleDateFormat(DATE_TIME_FORMAT_SOLIDUS));
		dateFormatMap.put(DATE_TIME_FORMAT_SOLIDUS_SEQ, new SimpleDateFormat(DATE_TIME_FORMAT_SOLIDUS_SEQ));
		dateFormatMap.put(DATE_TIME_FORMAT_UTC_DEFAULT, new SimpleDateFormat(DATE_TIME_FORMAT_UTC_DEFAULT));
		dateFormatMap.put(DATE_TIME_FORMAT_UTC_SOLIDUS, new SimpleDateFormat(DATE_TIME_FORMAT_UTC_SOLIDUS));
	}

	public static String format(String formatString, Date date) {
		return new SimpleDateFormat(formatString).format(date);
	}

	public static Date parseDate(long millis) {
		 return new Date(millis);
	}

	public static Date GMT() {
		return parseDate("1971-01-01", DATE_FORMAT_DEFAULT);
	}

	public static Date trimNull(Date date) {
		if (null!=date) {
			return date;
		}
		return GMT();
	}

	public static Date parseDate(String src) {
		if (BeanUtils.isEmpty(src)) {
			return null;
		}

		return parseDate(src, DATE_FORMAT_DEFAULT);
	}

	public static Date parseDateAdapt(String src) {
		if (BeanUtils.isEmpty(src)) {
			return null;
		}
		for(Map.Entry<String,Pattern> entry:dateFormatRegisterMap.entrySet()){
			if(entry.getValue().matcher(src).matches()){
				try {
				 return dateFormatMap.get(entry.getKey()).parse(src);
				}catch (Exception e){
					throw new RuntimeException(String.format("parseDateAdapt unsupported date template:%s", src), e);
				}
			}
		}
		return null;
	}

	public static Date parseDateGMT8Time(String src){
		try {
			if (StringUtils.isBlank(src)) {
				return null;
			}
			DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT);
			df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			return df.parse(src.trim());
		}catch (Exception e){
			logger.error("parseDateGMT8Time exception src"+src,e);
			return null;
		}
	}

	public static String formatDateGMT8Time(Date date){
		try {
			if (null==date) {
				return org.apache.commons.lang3.StringUtils.EMPTY;
			}
			DateFormat df = new SimpleDateFormat(DATE_TIME_FORMAT_DEFAULT);
			df.setTimeZone(TimeZone.getTimeZone("GMT+8"));
			return df.format(date);
		}catch (Exception e){
			logger.error("formatDateGMT8Time exception date"+date,e);
			return org.apache.commons.lang3.StringUtils.EMPTY;
		}

	}

	public static Date parseDateNormal(String src) {
		if (BeanUtils.isEmpty(src)) {
			return null;
		}

		return parseDate(src.trim(), DATE_FORMAT_NORMAL);
	}

	public static Date parseDateCompact(String src) {
		if (BeanUtils.isEmpty(src)) {
			return null;
		}

		return parseDate(src.trim(), DATE_FORMAT_COMPACT);
	}

	public static Date parseTimeMillis(String src){
		if (BeanUtils.isEmpty(src) || !StringUtils.isNumeric(src)) {
			return null;
		}
		return new Date(Long.valueOf(src));

	}

   	public static String formatDateSeq(Date date) {
		return formatDate(date, DATE_FORMAT_SEQ);
	}

   	public static String formatDateHour(Date date) {
		return formatDate(date, DATE_FORMAT_HOUR);
	}

   	public static String formatShortDateSeq(Date date) {
		return formatDate(date, DATE_FORMAT_SHORT_SEQ);
	}

    public static String formatTimeDateDefault(Date date) {
        if (date == null) {
            return "";
        }
        return formatDate(date, DATE_TIME_FORMAT_DEFAULT);
	}

    public static String formatTimeDefault(Date date) {
        if (date == null) {
            return "";
        }
        return formatDate(date, TIME_FORMAT);
	}

	public static Date parseDateDefault(String src) {
		if (BeanUtils.isEmpty(src)) {
			return null;
		}

		return parseDate(src.trim(), DATE_TIME_FORMAT_DEFAULT);
	}


    public static String curTimeDateDefault() {
    	return formatDate(new Date(), DATE_TIME_FORMAT_DEFAULT);
    }

    public static Integer curDate() {//yyyyMMdd
    	return Integer.parseInt(DateUtils.formatDateCompact(new Date()));
    }

	public static String formatDateSolidus(Date date) {
		return formatDate(date, DATE_FORMAT_SOLIDUS);
	}

	public static String formatDateOfYear(Date date) {
		return formatDate(date, DATE_FORMAT_YEAR);
	}

	public static String formatDateOfMon(Date date) {
		return formatDate(date, DATE_FORMAT_MON);
	}

	public static String formatMonToChinese(Date date) {
		return DateUtils.formatDate(date, MON_FORMAT_CHINESE);
	}


	public static Date parseDateSolidus(String dateString) {
		return parseDate(dateString, DATE_FORMAT_SOLIDUS);
	}

	public static Date parseDate(String src, String dateTemplate) {
		if (BeanUtils.isEmpty(src)) {
			return null;
		}

		try {
			return getSimpleDateFormat(dateTemplate).parse(src);
		} catch (Exception e) {
			throw new RuntimeException(String.format("unsupported date template:%s", src), e);
		}
	}

	public static <T> T parseDate(String src, Class<T> dateClazz) {

		if (BeanUtils.isEmpty(src)) {
			return null;
		}

		return convertDate(parseDate(src), dateClazz);
	}

	public static <T> T parseDate(String src, String dateTemplate, Class<T> dateClazz) {

		if (BeanUtils.isEmpty(src)) {
			return null;
		}

		return convertDate(parseDate(src, dateTemplate), dateClazz);
	}




	public static String formatDate(long mills) {
		Date date = parseDate(mills);
		return formatTimeDateDefault(date);
	}

	public static String formatDate(Date date) {

		if (BeanUtils.isEmpty(date)) {
			return null;
		}

		return formatDate(date, DATE_FORMAT_DEFAULT);
	}

	public static String formatDateCompact(Date date) {

		if (BeanUtils.isEmpty(date)) {
			return null;
		}

		return formatDate(date, DATE_FORMAT_COMPACT);
	}

	public static String formatDate(Date date, String dateTemplate) {
		if (BeanUtils.isEmpty(date) || BeanUtils.isEmpty(dateTemplate)) {
			return null;
		}
		return getSimpleDateFormat(dateTemplate).format(date);
	}

	public static <T> T convertDate(Date src, Class<T> dateClazz) {

		if (BeanUtils.isEmpty(src)) {
			return null;
		}

		try {

			return dateClazz.getConstructor(long.class).newInstance(src.getTime());
		} catch (Exception e) {
			String errorMessage = String.format("unsupported date type:%s", dateClazz.getName());
			logger.error(errorMessage,e);
			throw new RuntimeException(errorMessage, e);
		}
	}



	public static SimpleDateFormat getSimpleDateFormat(String dateTemplate) {
		return new SimpleDateFormat(dateTemplate);
	}

	private static long render(long i, int j, int k) {
		return (i + (i > 0 ? j : -j)) / k;
	}

	public static long diffSecond(Date start, Date end) {
		return render(end.getTime() - start.getTime(), 999, 1000);
	}
	public static long diffMillis(Date start, Date end) {
		return end.getTime() - start.getTime();
	}

	public static long diffMinute(Date end) {
		return diffMinute(new Date(System.currentTimeMillis()), end);
	}

	public static long diffMinute(Date start, Date end) {
		return render(diffSecond(start, end), 59, 60);
	}

	public static long diffHour(Date start, Date end) {
		return render(diffMinute(start, end), 59, 60);
	}

	public static long diffDay(Date start, Date end) {
		if(start==null|| end==null){
			return 0L;
		}
		return offset(start, end, Calendar.DAY_OF_YEAR);
	}

	public static long diffMonth(Date start, Date end) {
		return offset(start, end, Calendar.MONTH) + diffYear(start, end);
	}

	public static long diffYear(Date start, Date end) {
		Calendar s = Calendar.getInstance();
		Calendar e = Calendar.getInstance();

		s.setTime(start);
		e.setTime(end);

		return e.get(Calendar.YEAR) - s.get(Calendar.YEAR);
	}

	private static long offset(Date start, Date end, int offsetCalendarField) {

		boolean bool = start.before(end);
		long rtn = 0;
		Calendar s = Calendar.getInstance();
		Calendar e = Calendar.getInstance();

		s.setTime(bool ? start : end);
		e.setTime(bool ? end : start);

		rtn -= s.get(offsetCalendarField);
		rtn += e.get(offsetCalendarField);

		while (s.get(Calendar.YEAR) < e.get(Calendar.YEAR)) {
			rtn += s.getActualMaximum(offsetCalendarField);
			s.add(Calendar.YEAR, 1);
		}

		return bool ? rtn : -rtn;
	}

	public static Date add(Date date, int n, int calendarField) {
		if(null!=date){
			Calendar c = Calendar.getInstance();
			c.setTime(date);
			c.add(calendarField, n);
			return c.getTime();
		}
		return null;
	}

	public static Date addSec(Date date,int n){
		if(date == null){
			return null;
		}
		return add(date, n, Calendar.SECOND);
	}
	public static Date addMin(Date date,int n){
		if(date == null){
			return null;
		}
		return add(date, n, Calendar.MINUTE);
	}
	public static Date addHour(Date date,int n){
		if(date == null){
			return null;
		}
		return add(date, n, Calendar.HOUR);
	}
	public static Date addDay(Date date,int n){
		if(date == null){
			return null;
		}
		return add(date, n, Calendar.DAY_OF_MONTH);
	}

	public static Date addMonth(Date date,int n){
		if(date == null){
			return null;
		}
		return add(date, n, Calendar.MONTH);
	}
	public static Date addYear(Date date,int n){
		if(date == null){
			return null;
		}
		return add(date, n, Calendar.YEAR);
	}

	public static String formatDateAsCmsDrawSequence(Date date) {
		return formatDate(date, CMS_DRAW_SEQUENCE_FORMAT);
	}

	public static Date startOfFuture() {
		return DateUtils.parseDate("2999-01-01");
	}

	public static String formatDateTime(Date date) {
		return (date == null) ? null : formatDate(date, DATE_TIME_FORMAT);
	}

	public static Date parseDateTime(String date) {
		return parseDate(date, DATE_TIME_FORMAT);
	}

	public static Date parseTime(String time) {
		return parseDate(time, TIME_FORMAT);
	}

	public static Date parseCMSDateTime(String date) {
		return parseDate(date, CMS_DRAW_SEQUENCE_FORMAT);
	}

	public static boolean before(Date startAt,Date endAt) {
		return startAt!=null && endAt!=null && startAt.before(endAt);
	}


	public static Date endOfDaySecond(Date date) {
        return parseDateNormal(formatDateCompact(date)+"235959");
	}
	public static Date endOfToDaySecond() {
        return endOfDaySecond(new Date());
	}


    public static boolean isInRangeCurDate(Date startAt, Date endAt) {
		 Date compare = new Date();
		return compare.after(startAt) && compare.before(endAt);
	}

    public static boolean isInRange(Date startAt, Date endAt,Date compare) {
		return compare.after(startAt) && compare.before(endAt);
	}

    public static boolean isOnLastDay(Date date,int day) {
    	 if(date==null){
    		 return false;
    	 }
    	 Date future = addDay(date, day);
    	 return getMonth(future)>getMonth(date);
    }
    public static boolean isOnSameMonth(String mon,Date date) {
    	 if(date==null || StringUtils.isBlank(mon)){
    		 return false;
    	 }
    	return mon.equals(formatDateOfMon(date));
    }

	public static boolean compareTillSecond(Date oneDate, Date anotherDate) {
		if (oneDate == null || anotherDate == null) {
			return false;
		}
		return format(DATE_TIME_FORMAT_DEFAULT, oneDate).equals(format(DATE_TIME_FORMAT_DEFAULT, anotherDate));
	}

	public static boolean isSameDay(Date date, Date other) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String dateString = format.format(date);
		String otherString = format.format(other);
		return dateString.equals(otherString);
	}

	public static boolean isSameYear(Date date, Date other) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy");
		String dateString = format.format(date);
		String otherString = format.format(other);
		return dateString.equals(otherString);
	}

	public static String formatDateToChineseToMin(Date date) {
		return DateUtils.formatDate(date, DATE_FORMAT_CHINESE_TO_MIN);
	}
	public static Date parseChineseToMin(String src) {
		if (BeanUtils.isEmpty(src)) {
			return null;
		}
		try {
			// 兼容非法字符全角/半角 2017年03月10日 17:31，2017年03月10日 &nbsp; 17:31
			src = src.replaceAll("(?<=\\d{4}年\\d{2}月\\d{2}日).*?(?=\\d{2}:\\d{2})"," ");
			return parseDate(src.trim(), DATE_FORMAT_CHINESE_TO_MIN);
		}catch (Exception e){
			logger.error("unable to formatDateToChineseToMin src:" + src, e);
			return null;
		}
	}

	public static String formatDateToChinese(Date date) {
		return DateUtils.formatDate(date, DATE_FORMAT_CHINESE);
	}

	public static int getAge(Date birthdate) {
		return getAge(birthdate, Calendar.getInstance().getTime());
	}

	public static int getAge(Date birthdate, Date current) {
		Calendar calBirthDate = Calendar.getInstance();
		calBirthDate.setTime(birthdate);

		Calendar calCurrent = Calendar.getInstance();
		calCurrent.setTime(current);

		int age = calCurrent.get(Calendar.YEAR) - calBirthDate.get(Calendar.YEAR);
		int monthDiff = calCurrent.get(Calendar.MONTH) - calBirthDate.get(Calendar.MONTH);
		int dateDiff = calCurrent.get(Calendar.DATE) - calBirthDate.get(Calendar.DATE);

		if (monthDiff < 0) {
			age--;
		} else if (monthDiff == 0 && dateDiff < 0) {
			age--;
		}

		return age;
	}

	public static Integer getDigit(Date date,int type){
		if(date==null){
			return null;
		}
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		switch (type){
			case Calendar.YEAR:
				return calendar.get(Calendar.YEAR);
			case Calendar.MONTH:
				return calendar.get(Calendar.MONTH)+1;
			case Calendar.DAY_OF_MONTH:
				return calendar.get(Calendar.DAY_OF_MONTH);
			case Calendar.HOUR_OF_DAY:
				return calendar.get(Calendar.HOUR_OF_DAY);
			case Calendar.MINUTE:
				return calendar.get(Calendar.MINUTE);
			case Calendar.SECOND:
				return calendar.get(Calendar.SECOND);
		}
		throw new RuntimeException("getDigit unsupport type:"+type);

	}


	public static Long getMonth(Date date){
		if(date!=null){
			 return Long.parseLong(format("yyyyMM", date));
		}
	    return null;
	}

	public static Integer getMonthBy(Date date){
		if(date!=null){
			 return Integer.parseInt(format("yyyyMM", date));
		}
	    return null;
	}


	public static Long getLastMonth(Date date){
		if(date!=null){
			Date lastMon = add(date, -1, Calendar.MONTH);
			 return Long.parseLong(format("yyyyMM", lastMon));
		}
		return null;
	}
	public static String getLastMonth(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		return format("yyyyMM", c.getTime());
	}
	public static Date getLastMonthFirstDay(){
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		c.set(Calendar.DATE, 1);
		return c.getTime();
	}
	public static Date getCurMonthFirstDay(){
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DATE, 1);
		return c.getTime();
	}

	public static Date getFirstDayByMon(String mon){
		if(isMon(mon)){
			return parseDate(mon+"01","yyyyMMdd");
		}
		return null;
	}

	public static boolean isMon(String mon){
		if(StringUtils.isNotBlank(mon) && mon.matches("\\d{6}")){
			return true;
		}
		return false;
	}

	public static boolean isDateWithLine(String date){
		if(StringUtils.isBlank(date)){
			return false;
		}
		return dateFormatRegisterMap.get(DATE_FORMAT_DEFAULT).matcher(date).matches();
	}

	public static Timestamp conver2Timestamp(Date date){
		if(date!=null){
			return new Timestamp(date.getTime());
		}
		return null;
	}

	//Date
	public static String formateDigit(String date,int num){
			String digit="";
			if(StringUtils.isNotBlank(date)){
				for(int i=0;i<date.length();i++){
					char x = date.charAt(i);
					if(Character.isDigit(x)){
						digit+=x;
						if(--num<=0){
							break;
						}
					}
				}
			}
			return digit;
	}

	/**
	 * 获取给定日期年份的第一天
	 * @param date
	 * @return
	 */
	public static Date getYearFirst(Date date,int n){
		Calendar calendar=Calendar.getInstance();
		calendar.clear();
		calendar.setTime(date);
		int year = calendar.get(Calendar.YEAR);
		return getYearFirst(year+n);

	}

	/**
	 * 获取某年第一天日期
	 * @param year 年份
	 * @return Date
	 */
	public static Date getYearFirst(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		Date yearFirst = calendar.getTime();
		return yearFirst;
	}

	/**
	 * 获取某年最后一天日期
	 * @param year 年份
	 * @return Date
	 */
	public static Date getYearLast(int year){
		Calendar calendar = Calendar.getInstance();
		calendar.clear();
		calendar.set(Calendar.YEAR, year);
		calendar.roll(Calendar.DAY_OF_YEAR, -1);
		Date currYearLast = calendar.getTime();

		return currYearLast;
	}

	/**
	 * 后年年初
	 * @return
	 */
	public static Date getYearAfterYearFirst(){
		return getYearFirst(new Date(),2);
	}

		/**
		 * 后年年初
		 * @param date
		 * @return
		 */
	public static Date getYearAfterYearFirst(Date date){
		if(date == null){
			date = new Date();
		}
		return getYearFirst(date,2);
	}

	/**
	 * 取时间交集
	 * @param startAt1,endAt1,startAt2,endAt2
	 * @return
	 */
	public static Date[] intersection(Date startAt1,Date endAt1,Date startAt2,Date endAt2){
		startAt1 = startAt1==null?DateUtils.parseDate("2017-01-01"):startAt1;
		startAt2 = startAt2==null?DateUtils.parseDate("2017-01-01"):startAt2;
		endAt1 = endAt1==null?DateUtils.parseDate("2999-01-01"):endAt1;
		endAt2 = endAt2==null?DateUtils.parseDate("2999-01-01"):endAt2;
		Date[] dates = new Date[2];
		dates[0]= startAt1.before(startAt2)?startAt2:startAt1;
		dates[1]= endAt1.before(endAt2)?endAt1:endAt2;
		return dates;
	}


}
