package com.aipers.groupware.common.utilities;

import com.aipers.groupware.common.Constants;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class StringUtils {

  private StringUtils() {}

  /**
   * 주어진 문자열이 빈 문자인지 확인 합니다
   * @param value
   * @return
   */
  public static final boolean hasEmpty(final CharSequence value) {
    return null == value || Constants.EMPTY_STRING.equals(value.toString().trim());
  }

  /**
   * 주어진 문자열이 비었다면 기본값으로 전달된 값을 반환 합니다
   * @param obj
   * @param def
   * @return
   */
  public static final String nvl(final Object obj, final String def) {
    return CommonUtils.nvl(obj, def, String.class);
  }

  /**
   * 주어진 객체를 문자열로 반환합니다. 객체가 정의되지 않은 경우 빈 문자열을 반환합니다.
   * @param obj
   * @return
   */
  public static final String nvl(final Object obj) {
    return nvl(obj, Constants.EMPTY_STRING);
  }

  /**
   * 주어진 문자열에서 oldPart 찾아 newPart 변환한 후 반환합니다
   * @param source
   * @param oldPart
   * @param newPart
   * @return
   */
  public static final String replace(final String source, final String oldPart, final String newPart) {
    if(source == null) return "";
    if(oldPart == null || newPart == null) return source;

    StringBuilder sb = new StringBuilder();
    int last = 0;
    while (true) {
      int start = source.indexOf(oldPart, last);
      if (start >= 0) {
        sb.append(source.substring(last, start));
        sb.append(newPart);
        last = start + oldPart.length();
      }
      else {
        sb.append(source.substring(last));
        return sb.toString();
      }
    }
  }

  /**
   * 주어진 문자열의 제일 앞 하나의 문자열을 대문자로 변환하여 반환 합니다
   * @param source
   * @return
   */
  public static final String firstCapitalize(final String source) {
    return Optional.ofNullable(source)
        .map(value -> value.substring(0, 1).toUpperCase().concat(value.substring(1)))
        .orElse(null);
  }

  /**
   * 주어진 문자열에서 정규식에 매치되는 문자열을 전달된 replacer 함수를 호출하여 변환후 반환합니다
   * @param source
   * @param pattern
   * @param replacer
   * @return
   */
  public final static String replace(
      final String source, final Pattern pattern, final Function<Matcher, String> replacer
  ) {
    final Matcher matcher = pattern.matcher(source);
    final StringBuffer result = new StringBuffer();

    while(matcher.find()) {
      matcher.appendReplacement(result, replacer.apply(matcher));
    }
    return matcher.appendTail(result).toString();
  }

  /**
   * TAG 표시용 문자로 변환 합니다
   * @param str
   * @return
   */
  public final static String safeHTML(String str) {
    str	= replace(str, "&", "&amp;");
    str	= replace(str, "#", "&#35;");
    str	= replace(str, "<", "&lt;");
    str	= replace(str, ">", "&gt;");
    str	= replace(str, "(", "&#40;");
    str	= replace(str, ")", "&#41;");
    str	= replace(str, "\"", "&quot;");
    str	= replace(str, "\'", "&#39;");

    return str;
  }

  /**
   * TAG 표시용 문자를 복원 합니다
   * @param str
   * @return
   */
  public static final String deSafeHTML(String str) {
    if (CommonUtils.isEmpty(str)) return "";

    str	= replace(str, "&#39;", "'");
    str	= replace(str, "&quot;", "\"");
    str	= replace(str, "&#41;", ")");
    str	= replace(str, "&#40;", "(");
    str	= replace(str, "&gt;", ">");
    str	= replace(str, "&lt;", "<");
    str	= replace(str, "&#35;", "#");
    str	= replace(str, "&amp;", "&");

    return str;
  }

  /**
   * 중복된 줄바꿈을 하나로 변환합니다
   * @param text
   * @return
   */
  public static final String normalizeWhitespace(final String text) {
    return nvl(text).replaceAll("(\\r\\n|\\r|\\n)(\\s*(\\r\\n|\\r|\\n))+", "$1");
  }

  /**
   * 주어진 문자열을 URL safe 문자열로 변환합니다
   * @param text
   * @return
   */
  public static final String encodeUrl(final String text) {
    try {
      return URLEncoder.encode(text, Constants.DEFAULT_ENCODING).replaceAll("\\+", "%20");
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  /**
   * 주어진 문자열에서 limit 이상의 연속된 문자열이 있는지 찾습니다. (주로 비밀번호의 유효성 검증에 사용됩니다)
   * 연속되는지 확인하기 위해서 두자리의 문자열을 검색하기 때문에 limit 값은 최소 3 이상이 되어야 합니다
   * @param input
   * @param limit
   * @return
   */
  public static boolean isSuccessionAtLimitMore(final String input, final int limit) {
    if (null == input || limit > input.length()) return false;

    int prev = 0, prevDiff = 0, diff = 0, count = 0;

    for (int i = 0;i < input.length();i++) {
      final char letter = input.charAt(i);

      if (i > 0 && (diff = prev - letter) > -2 && (count += diff == prevDiff ? 1 : 0) > limit - 3)
        return true;

      prev = letter;
      prevDiff = diff;
    }

    return false;
  }

  /**
   * 주어진 URL safe 문자열을 복원합니다
   * @param text
   * @return
   */
  public static final String decodeUrl(final String text) {
    try {
      return URLDecoder.decode(text, Constants.DEFAULT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      return null;
    }
  }

  /**
   * 주어진 문자열을 Base64 문자열로 인코딩하여 반환 합니다
   * @param text
   * @return
   */
  public static final String encodeBase64(final String text) {
    return encodeBase64(nvl(text).getBytes(Constants.DEFAULT_CHARSET));
  }
  /**
   * 주어진 바이트 배열을 Base64 문자열로 인코딩하여 반환 합니다
   * @param source
   * @return
   */
  public static final String encodeBase64(final byte[] source) {
    return Base64.getEncoder().encodeToString(source);
  }

  /**
   * 주어진 문열이 Base64 형식 인코딩된 문자열이라면 복원하여 바이트 배열로 반환 합니다.
   * @param text
   * @return
   */
  public static final byte[] decodeBase64(final String text) {
    try {
      return Formatters.isBase64String(text)
          ? Base64.getDecoder().decode(text.getBytes(Constants.DEFAULT_ENCODING))
          : text.getBytes(Constants.DEFAULT_ENCODING); // throw new IllegalArgumentException();
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 주어진 문열이 Base64 형식 인코딩된 문자열이라면 복원하여 문자열로 반환 합니다
   * @param text
   * @return
   */
  public static final String decodeBase64String(final String text) {
    if (CommonUtils.isEmpty(text)) return Constants.EMPTY_STRING;
    if (!Formatters.isBase64String(text)) return text;

    try {
      return new String(decodeBase64(text), Constants.DEFAULT_ENCODING);
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * 주어진 문자열을 camel case 형태로 변환하여 반환 합니다
   * @param source
   * @return
   */
  public final static String toCamelCase(final String source) {
    return Formatters.CAMEL_PATTERN.matcher(source.toLowerCase()).find()
        ? replace(source.toLowerCase(), Formatters.CAMEL_PATTERN, (m) -> m.group(1).toUpperCase())
        : source;
  }

  /**
   * 주어진 문자열을 주어진 숫자만큼 반복하여 연결하여 반환합니다
   * @param in
   * @param count
   * @return
   */
  public final static String repeat(final String in, final int count) {
    return IntStream.range(0, count).mapToObj(i -> in).collect(Collectors.joining());
  }

  /**
   * 주어진 문자열을 주어진 횟수만큼 섞습니다
   * @param in
   * @return
   */
  public final static String shuffle(final String in) {
    return shuffle(in, 1);
  }
  public final static String shuffle(final String in, final int loop) {
    final List<String> characters = Arrays.asList(in.split("(?!^)"));
    IntStream.range(0, loop).forEach(i -> Collections.shuffle(characters));
    return characters.stream().collect(Collectors.joining());
  }

  /**
   * 랜덤한 숫자를 지정된 길이에 맞는 문자열로 반환합니다
   * @param length
   * @return
   */
  public final static String getRandomNumberString(final int length) {
    return shuffle(repeat(shuffle("1234567890"), 5), 5).substring(0, length);
  }

  /**
   * 랜덤한 영문/숫자를 지정된 길이에 맞는 문자열로 반환합니다 (특수문자 포함 옵션)
   * @param length
   * @param isSpecialChars
   * @return
   */
  public final static String getRandomString(final int length, final boolean isSpecialChars) {
    String str	= "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
        .concat((isSpecialChars ? "`~!@#$%^&*()_+<>,./" : ""));
    str	= shuffle(str);
    str	= repeat(str, 5);
    str	= shuffle(str);

    return str.substring(0, length);
  }

}
