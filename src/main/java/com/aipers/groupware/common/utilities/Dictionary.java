package com.aipers.groupware.common.utilities;

import com.aipers.groupware.common.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Map 객체를 조금 편하게 사용하기 위한 유틸리티 클래스 입니다.
 *   set 사용시 chaining 으로 사용가능
 *   자주 사용되는 자료형으로 값을 변환 처리하기 용이함
 *   put 메서드를 사용하여 값 주입시 키는 camel case 처리됨
 */
public class Dictionary extends HashMap<String, Object> {

  private final static long serialVersionUID = 2135268622818763181L;

  public Dictionary() {
    super();
  }

  public Dictionary(Object o) {
    super();

    this.putAll(o instanceof Map ? (Map) o : new ObjectMapper().convertValue(o, Map.class));
  }

  public Dictionary(final Map<String,?> map) {
    super();
    if(map != null) this.putAll(map);
  }

  public Dictionary(final String key, final Object value) {
    super();
    super.put(key, value);
  }

  public int length() {
    return super.entrySet().size();
  }

  public Dictionary copy() {
    return (Dictionary) super.clone();
  }

  @Override
  public Object put(final String key, final Object value) {
    return super.put(StringUtils.toCamelCase(key), value);
  }

  public Dictionary set(final String column, final Clob value) {
    final StringBuilder text = new StringBuilder();

    try (
        final Reader reader = value.getCharacterStream();
        final BufferedReader buffer = new BufferedReader(reader)
    ) {
      Stream.of(buffer)
          .forEach(buf -> {
            try {
              text.append(buf.readLine()).append("\n");
            } catch(IOException e) {}
          });
    } catch (SQLException | IOException e) {}

    set(column, text.toString());
    return this;
  }

  public Dictionary set(final String column, final String value) {
    super.put(column, value);
    return this;
  }

  public Dictionary set(final String column, final Object value) {
    super.put(column, value);
    return this;
  }

  public Dictionary set(final String column, final int value) {
    super.put(column, Integer.toString(value));
    return this;
  }

  public Dictionary set(final String column, final long value) {
    super.put(column, Long.toString(value));
    return this;
  }

  public Dictionary set(final String column, final float value) {
    super.put(column, Float.toString(value));
    return this;
  }

  public Dictionary set(final String column, final double value) {
    super.put(column, Double.toString(value));
    return this;
  }

  public Dictionary set(final String column, final List<Dictionary> value) {
    super.put(column, value);
    return this;
  }

  public Dictionary set(final String column, final String[] value) {
    super.put(column, value);
    return this;
  }

  public Dictionary set(final String column, final InputStream value) {
    super.put(column, value);
    return this;
  }

  public int getInt(final String column) {
    return getInt(column, 0);
  }

  public int getInt(final String column, final int v_default) {
    if (null != super.get(column)) {
      try {
        return super.get(column) instanceof Number
            ? ((Number) super.get(column)).intValue()
            : Integer.parseInt(String.valueOf(super.get(column)));
      } catch (NumberFormatException e) {}
    }

    return v_default;
  }

  public long getLong(final String column) {
    return getLong(column, 0);
  }

  public long getLong(final String column, final long v_default) {
    if (null != super.get(column)) {
      try {
        if (super.get(column) instanceof Timestamp)
          return ((Timestamp) super.get(column)).getTime();

        return super.get(column) instanceof Number
            ? ((Number) super.get(column)).longValue()
            : Long.parseLong(super.get(column).toString());
      } catch (NumberFormatException e) {}
    }

    return v_default;
  }

  public Object take(final String key) {
    return super.get(key);
  }
  public <T> T take(final String key, final Class<T> type) {
    if (null == super.get(key)) return CommonUtils.getEmptyInstance(type);

    try {
      return type.cast(super.get(key));
    } catch (Exception e) {
      return CommonUtils.getEmptyInstance(type);
    }
  }

  public String get(final String key) {
    if (super.get(key) instanceof String[]) {
      final String[] arr = (String[]) super.get(key);
      return null == arr || 0 == arr.length ? "" : arr[0];
    }

    return StringUtils.nvl(super.get(key));
  }

  public String get(final String key, final int value) {
    return CommonUtils.isEmpty(super.get(key))
        ? Integer.toString(value)
        : StringUtils.nvl(super.get(key));
  }

  public String get(final String key, final String value) {
    return StringUtils.nvl(super.get(key), value);
  }

  public List getList(final String key) {
    return getList(key, new ArrayList());
  }

  public List getList(final String key, final List value) {
    if (null == super.get(key)) return value;

    return (List) super.get(key);
  }

  public String getString(final String column) {
    return getString(column, "");
  }

  public String getString(final String column, final String v_default) {
    return StringUtils.nvl(super.get(column), v_default);
  }

  public byte[] getByte(final String key) {
    if (null != super.get(key)) {
      final Object obj = super.get(key);
      if (obj instanceof byte[]) return (byte[]) obj;
      else {
        try {
          return String.valueOf(obj).getBytes();
        } catch(Exception e) {}
      }
    }

    return new byte[0];
  }

  public String[] getValues(final String key){
    if (null != super.get(key)) {
      try{
        final Object obj = super.get(key);
        if (obj instanceof ArrayList) {
          return ((ArrayList<?>) obj).toArray(new String[] {});
        } else if (obj instanceof String) {
          return new String[] { (String) obj };
        } else {
          return (String[]) obj;
        }
      } catch(Exception e) {
        return new String[] { get(key) };
      }
    }

    return new String[0];
  }

  public float getFloat(String column) {
    return getFloat(column, 0);
  }

  public float getFloat(final String column, final float v_default) {
    if (null != super.get(column)) {
      try {
        return super.get(column) instanceof Number
            ? ((Number) super.get(column)).floatValue()
            : Float.parseFloat(get(column));
      } catch (NumberFormatException e) {}
    }

    return v_default;
  }

  public double getDouble(final String column) {
    return getDouble(column, 0);
  }

  public double getDouble(final String column, final double v_default) {
    if (null != super.get(column)) {
      try {
        return super.get(column) instanceof Number
            ? ((Number) super.get(column)).doubleValue()
            : Double.parseDouble(get(column));
      } catch (NumberFormatException e) {}
    }

    return v_default;
  }

  public BigDecimal getDecimal(final String column) {
    return getDecimal(column, 0D);
  }

  public BigDecimal getDecimal(final String column, final double v_default) {
    final BigDecimal val = BigDecimal.valueOf(v_default);

    if (null != super.get(column)) {
      try {
        Object	obj	= super.get(column);

        if (obj instanceof BigDecimal) return (BigDecimal) super.get(column);
        else if (obj instanceof Double) return BigDecimal.valueOf((Double) super.get(column));
        else return BigDecimal.valueOf(Double.parseDouble(get(column)));
      } catch(Exception e) {}
    }

    return val;
  }

  public boolean getBoolean(final String column) {
    return super.get(column) instanceof Boolean
        ? (Boolean) super.get(column)
        : !isNull(column);
  }

  public Dictionary getDictionary(final String column) {
    return super.get(column) instanceof Dictionary
        ? (Dictionary) super.get(column)
        : new Dictionary().set(column, super.get(column));
  }

  public File getFile(final String column) {
    return super.get(column) instanceof File ? (File) super.get(column) : null;
  }

  public int getLength(final String column) {
    return getString(column).length();
  }

  public Dictionary queryToParam(final String key) {
    String queryString = this.get(key);

    if (Formatters.isBase64String(queryString))
      queryString = StringUtils.decodeBase64String(queryString);

    return new Dictionary(
        Arrays.stream(
                (queryString.startsWith("?")
                    ? queryString.substring(1)
                    : queryString
                ).split("&")
            )
            .map(v -> v.split("="))
            .collect(Collectors.toMap(v -> v[0], v -> v[1]))
    );
  }

  public byte[] toByte() {
    try (
        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        final ObjectOutputStream out = new ObjectOutputStream(bos)
    ) {
      out.writeObject(this);
      out.flush();

      return bos.toByteArray();
    } catch(Exception e) {
      return String.valueOf(UUID.randomUUID().hashCode()).getBytes();
    }
  }

  public boolean isNull(final String key) {
    return null == super.get(key);
  }

  public boolean isMatched(final String pattern, final String... keys) {
    return Stream.of(keys)
        .allMatch(key -> Pattern.compile(pattern).matcher(getString(key)).matches());
  }

  public boolean isEmpty(final String key) {
    return CommonUtils.isEmpty(key);
  }
  public boolean anyEmpty(final String ... keys) {
    return CommonUtils.isAnyEmpty(Arrays.asList(keys).toArray(new Object[] {}));
  }
  public boolean allEmpty(final String ... keys) {
    return CommonUtils.isAllEmpty(Arrays.asList(keys).toArray(new Object[] {}));
  }

  public boolean isList(final String key) {
    return super.get(key) instanceof List;
  }

  public boolean isBoolean(final String key) {
    return super.get(key) instanceof Boolean;
  }

  public boolean isDictionary(final String key) {
    return super.get(key) instanceof Dictionary;
  }

  @Override
  public Dictionary clone() {
    return (Dictionary) super.clone();
  }

  public Dictionary pick(final String ... args) {
    final Dictionary newDict = new Dictionary();

    for (final String key : args) {
      if (!isNull(key)) newDict.put(key, super.get(key));
    }

    return newDict;
  }

  public Dictionary omit(final String ... args) {
    return this.copy().delete(args);
  }

  public Dictionary delete(final String ... keys) {
    Arrays.stream(keys).forEach(super::remove);

    return this;
  }

  public void clearEmpty() {
    delete(
        this.keySet().stream()
            .filter(key -> Constants.EMPTY_STRING.equals(StringUtils.nvl(get(key))))
            .map(String::valueOf)
            .toArray(String[]::new)
    );
  }

  public Object getObject(final String key) {
    return super.get(key);
  }

  public String toQueryString() {
    return StringUtils.replace(
        this.entrySet().stream()
            .map(entry -> String.format("%s=%s", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining("&")),
        "null", ""
    );
  }

  public String toJson() {
    try {
      return new ObjectMapper().writeValueAsString(this);
    } catch(Exception e) {
      return toString();
    }
  }

  @Override
  public String toString() {
    return "{" +
        this.entrySet().stream()
            .map(entry -> String.format("\"%s\":\"%s\"", entry.getKey(), entry.getValue()))
            .collect(Collectors.joining(", "))
        + "}";
  }

}

