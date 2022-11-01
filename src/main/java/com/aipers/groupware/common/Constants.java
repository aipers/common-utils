package com.aipers.groupware.common;

import java.io.File;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Locale;
import java.util.TimeZone;

public abstract class Constants {

  public static final Locale DEFAULT_LOCALE = Locale.KOREA;
  public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
  public static final String DEFAULT_ENCODING = DEFAULT_CHARSET.toString();

  public static final ZoneId DEFAULT_ZONE = ZoneId.of("UTC");
  public static final ZoneOffset DEFAULT_OFFSET = ZoneOffset.UTC;
  public static final TimeZone DEFAULT_TIMEZONE = TimeZone.getTimeZone(DEFAULT_OFFSET);

  public static final String EMPTY_STRING = "";
  public static final String TOKEN_SPLIT_CHAR = " ";

  public static final char PACKAGE_SEPARATOR = '.';
  public static final char PATH_SEPARATOR = File.separatorChar;

  public static final int EMPTY_NUMBER = 0;
  public static final int MAX_PROMISE_THREAD_COUNT = 20;

  public static abstract class Paging {
    public static final int PAGE_SIZE = 15;
  }

}
