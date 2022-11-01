package com.aipers.groupware.common.utilities;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public abstract class ReflectionUtils {

  public static final Field getField(final Class<?> clazz, final String key) {
    Class<?> current = clazz;
    do {
      try {
        return current.getDeclaredField(key);
      } catch (NoSuchFieldException e) {}
    } while (null != (current = current.getSuperclass()));

    return null;
  }

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
