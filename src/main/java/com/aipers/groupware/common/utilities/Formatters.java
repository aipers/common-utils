package com.aipers.groupware.common.utilities;

import java.util.regex.Pattern;

public abstract class Formatters {

  public static final Pattern CAMEL_PATTERN = Pattern.compile("[\\W|_]+(.)");
  public static final Pattern QUERY_PARAM_PATTERN = Pattern.compile("([^=]+)=(.*)?");
  public static final Pattern REPEAT_CHAR_PATTERN = Pattern.compile("(.)\\1\\1");

  public static final String NUMBER_PATTERN = "(\\d)(?=(?:\\d{3})+(?!\\d))";
  public static final String BUSINESS_NO_PATTERN = "^(\\d{3})(\\d{2})(\\d+)$";
  public static final String CARD_NO_PATTERN = "^(\\d{4})(\\d{4})(\\d{4})(\\d+)$";

  public static final Pattern ISO_DATE_PATTERN =
      Pattern.compile("(\\d){4}\\-(\\d){2}\\-(\\d){2}T(\\d){2}:(\\d){2}:(\\d){2}\\.(\\d){3}Z");
  public static final Pattern SQL_DATE_PATTERN =
      Pattern.compile("(\\d){4}\\-(\\d){2}\\-(\\d){2}T(\\d){2}:(\\d){2}:(\\d){2}\\.(\\d){3}[\\+|\\-](\\d){4}");

  public static final Pattern BASE64_PATTERN = Pattern.compile(
      "^([A-Za-z0-9+/\\-_]{4})*([A-Za-z0-9+/\\-_]{3}=?|[A-Za-z0-9+/\\-_]{2}=?=?)?$");
  public static final Pattern HTTP_URL_PATTERN = Pattern.compile(
      "^(https?):\\/\\/([^:\\/\\s]+)(:([^\\/]*))?(www\\.)?((?:[a-z0-9-]+)(\\.[a-z]{2,})?)");
  public static final Pattern EMAIL_PATTERN =
      Pattern.compile(
          "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-z0-9-]+\\.)+[a-z]{2,6}$",
          Pattern.CASE_INSENSITIVE
      );

  public static final Pattern RATIONAL_NUMBER_PATTERN = Pattern.compile("^[\\-|+]?[\\d]+(\\.)?(\\d+)?$");

  private Formatters() {}

  /**
   * 주어진 문자열이 숫자형태의 문자열인지 확인 합니다
   * @param value
   * @return
   */
  public final static boolean isValidNumber(final String value) {
    return RATIONAL_NUMBER_PATTERN.matcher(value).matches();
  }

  /**
   * 주어진 문자열이 Base64 형태로 인코딩된 문자열인지 확인 합니다
   * @param txt
   * @return
   */
  public final static boolean isBase64String(final String txt) {
    return BASE64_PATTERN.matcher(txt).matches();
  }

  /**
   * 주어진 문자열이 이메일 형식이 맞는지 확인 합니다.
   * @param email
   * @return
   */
  public final static boolean isEmail(final String email) {
    return EMAIL_PATTERN.matcher(email).matches();
  }

  /**
   * 양 끝의 두자리를 제외하고 모든 문자를 마스킹 (*) 하여 반환 합니다
   * @param origin
   * @return
   */
  public final static String maskingBetweenChars(final String origin) {
    return origin.replaceAll("(?<!^.?).(?!.?$)", "*");
  }

  /**
   * 전화번호 포맷으로 변환합니다
   * @param str
   * @return
   */
  public static String phone(String str) {
    str = StringUtils.nvl(str).trim().replaceAll("[^\\d]", "");

    return 8 == str.length()
        ? str.replaceAll("(\\d{4})(\\d{4})", "$1-$2")
        : str.replaceAll("(^02.{0}|^01\\d{1}|\\d{3})(\\d+)(\\d{4})", "$1-$2-$3");
  }

  /**
   * 자릿수 단위 포맷으로 변환합니다
   * @param str
   * @return
   */
  public static String number(final String str) {
    final String[] parts = str.split("\\.");
    return StringUtils.nvl(parts[0]).trim()
        .replaceAll("[^\\d|\\-]", "")
        .replaceFirst("^0+(?!$)", "")
        .replaceAll(NUMBER_PATTERN, "$1,") +
        (1 < parts.length ? ".".concat(parts[1]) : "");
  }

  /**
   * 사업자 번호 포맷으로 변환합니다
   * @param str
   * @return
   */
  public static String biz(final String str) {
    return StringUtils.nvl(str).trim()
        .replaceAll("[^\\d]", "")
        .replaceAll(BUSINESS_NO_PATTERN, "$1-$2-$3");
  }

  /**
   * 카드 번호 포맷으로 변환합니다
   * @param str
   * @return
   */
  public static String card(final String str) {
    return StringUtils.nvl(str).trim()
        .replaceAll("\\D", "")
        .replaceAll(CARD_NO_PATTERN, "$1-$2-$3-$4");
  }

  /**
   * 주어진 문자열에서 특정 문자열이 3회이상 반복되는지 확인 합니다. (주로 비밀번호의 유효성 검증을 위해 사용됩니다)
   * @param value
   * @return
   */
  public boolean isRepeatedAtThreeTimeMore(final String value) {
    return REPEAT_CHAR_PATTERN.matcher(value).find();
  }

}
