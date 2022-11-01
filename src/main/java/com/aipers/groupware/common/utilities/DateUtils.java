package com.aipers.groupware.common.utilities;

import com.aipers.groupware.common.Constants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Date;

public abstract class DateUtils {

  /**
   * 현재시간을 주어진 포맷으로 변환하여 문자열로 반환합니다
   * @param format
   * @return
   */
  public static final String getTimestampString(final String format) {
    return getTimestampString(LocalDateTime.now(), format);
  }

  /**
   * 전달된 날짜를 주어진 포맷으로 변환하여 문자열로 반환합니다.
   * @param date
   * @param format
   * @return
   */
  public static final String getTimestampString(final Date date, final String format) {
    return getTimestampString(
        date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(), format);
  }

  /**
   * 전달된 LocalDateTime 주어진 포맷으로 변환하여 문자열로 반환합니다
   * @param date
   * @param format
   * @return
   */
  public static final String getTimestampString(final LocalDateTime date, final String format) {
    return date.format(DateTimeFormatter.ofPattern(format));
  }

  /**
   * 전달된 ZonedDateTime 주어진 포맷으로 변환하여 문자열로 반환합니다.
   * @param date
   * @param format
   * @return
   */
  public static final String getTimeStampString(final ZonedDateTime date, final String format) {
    return date.format(DateTimeFormatter.ofPattern(format));
  }

  /**
   * 현시간에 타임존 offset 만큼의 시간을 추가해 Date 형태로 반환합니다
   * @param offset
   * @return
   */
  public static final Date getOffsetDate(final int offset) {
    return Date.from(OffsetDateTime.now(ZoneOffset.ofTotalSeconds(offset * 60)).toInstant());
  }

  /**
   * 전달된 문자열을 날짜로 변환합니다
   * @param date
   * @return
   * @throws ParseException
   */
  public static final Date stringToDate(String date) throws ParseException {
    if(StringUtils.hasEmpty(date)) return null;

    date	= date.replaceAll("[\\D]", "");

    if(14 < date.length()) date = date.substring(0, 14);
    switch(date.length()) {
      case 14: break;
      case 12: date += "00"; break;
      case 10: date += "0000"; break;
      case 8:  date += "000000"; break;
      case 6:  date += "01000000"; break;
      case 4:  date += "0101000000"; break;
      default: return null;
    }

    final java.text.SimpleDateFormat tmpFormat =
        new java.text.SimpleDateFormat("yyyyMMddHHmmss", java.util.Locale.KOREA);
    tmpFormat.setLenient(true);

    return tmpFormat.parse(date);
  }

  /**
   * 주어진 문자열을 ZonedDateTime 변환하여 반환합니다
   * @param date
   * @return
   * @throws ParseException
   */
  public final static ZonedDateTime stringToZonedDatetime(final String date) throws ParseException {
    ZonedDateTime zonedDateTime;

    if (Formatters.ISO_DATE_PATTERN.matcher(date).find()) {
      zonedDateTime = ZonedDateTime.parse(date);
    } else if(Formatters.SQL_DATE_PATTERN.matcher(date).find()) {
      zonedDateTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
          .parse(date)
          .toInstant().atZone(ZoneId.systemDefault());
    } else {
      zonedDateTime =
          ZonedDateTime.ofInstant(stringToDate(date).toInstant(), Constants.DEFAULT_ZONE);
    }

    return zonedDateTime;
  }

  /**
   * 전달된 날짜에 n개월 이후의 날짜를 반환합니다
   * @param current
   * @param after
   * @return
   */
  public static final Date getAfterMonth(final Date current, final int after) {
    final Calendar cal = Calendar.getInstance();
    cal.setTime(current);
    cal.add(Calendar.MONTH, after);

    return cal.getTime();
  }

  /**
   * 전달된 문자열이 날짜 형식이 맞는지 체크합니다
   * @param ymd
   * @return
   */
  public final static boolean hasValidDate(final String ymd) {
    if (!ymd.matches("^(\\d{4}).?(\\d{2}).?(\\d{2})$")) return false;

    try {
      LocalDate.parse(ymd.replaceAll("[^\\d]", ""), DateTimeFormatter.BASIC_ISO_DATE);
      return true;
    } catch (DateTimeParseException e) {
      // do nothing
    }

    return false;
  }

}
