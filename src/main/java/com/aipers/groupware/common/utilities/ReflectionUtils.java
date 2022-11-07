package com.aipers.groupware.common.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class ReflectionUtils {

  private ReflectionUtils() {}

  /**
   * 주어진 클래스에 선언된 프로퍼티를 조회하여 반환합니다.
   * @param clazz
   * @param key
   * @return @Nullable Field
   */
  public static final Field getField(final Class<?> clazz, final String key) {
    Class<?> current = clazz;
    do {
      try {
        return current.getDeclaredField(key);
      } catch (NoSuchFieldException e) {}
    } while (null != (current = current.getSuperclass()));

    return null;
  }

  /**
   * 주어진 클래스에서 전달된 파라메터 모두를 파라메터로 갖는 key 이름의 메서드를 반환합니다.
   * @param clazz
   * @param key
   * @param paramTypes
   * @return @Nullable Method
   */
  public static final Method getMethod(
      final Class<?> clazz, final String key, final Class<?> ... paramTypes
  ) {
    try {
      return clazz.getMethod(key, paramTypes);
    } catch (NoSuchMethodException e) {
      return null;
    }
  }

}
