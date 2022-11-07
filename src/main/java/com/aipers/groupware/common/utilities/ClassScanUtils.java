package com.aipers.groupware.common.utilities;

import com.aipers.groupware.common.Constants;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class ClassScanUtils {

  private static final List<String> RESOURCE_STREAM_TYPES = Arrays
      .asList(ByteArrayInputStream.class.getSimpleName(), BufferedInputStream.class.getSimpleName());

  private ClassScanUtils() {}

  /**
   * packageName 하위의 모든 class 스캔하여 set 자료구조로 반환 합니다.
   * @param classLoader
   * @param packageName
   * @return
   */
  public static Set<Class> findAllClasses(final ClassLoader classLoader, final String packageName) {
    return getClasses(classLoader, packageName);
  }

  /**
   * 전달된 패키지의 하위에 존재하는 클래스를 재귀적으로 찾습니다
   * @param classLoader
   * @param packageName
   * @return
   */
  private static Set<Class> getClasses(final ClassLoader classLoader, final String packageName) {
    final String packagePath =
        packageName.replace(Constants.PACKAGE_SEPARATOR, Constants.PATH_SEPARATOR);

    try (
        InputStream stream = classLoader.getResourceAsStream(packagePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
    ) {
      if (!RESOURCE_STREAM_TYPES.contains(stream.getClass().getSimpleName())) return new HashSet<>();

      return reader.lines()
          .flatMap(line -> line.endsWith(".class")
              ? getClass(packageName, line)
              : getClasses(
                  classLoader,
                  String.format("%s%s%s", packageName, Constants.PACKAGE_SEPARATOR, line)
                ).stream()
          )
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new RuntimeException(String.format("[%s] Scan error", packageName), e);
    }
  }

  /**
   * 전달된 패키지의 하위에 전달된 클래스를 반환합니다
   * @param packageName
   * @param className
   * @return
   */
  private static Stream<Class> getClass(final String packageName, final String className) {
    final String target = String.format(
        "%s%s%s",
        packageName, Constants.PACKAGE_SEPARATOR, className.substring(0, className.lastIndexOf('.'))
    );

    try {
      return Stream.of(Class.forName(target));
    } catch (ClassNotFoundException e) {
      throw new RuntimeException(String.format("[%s] Class not found", target), e);
    }
  }

  public static <T> Constructor<T> getMaxArgsConstructor(final Class<T> clazz) {
    Constructor<T> constructor = null;

    for (Constructor<?> c : clazz.getConstructors()) {
      if (null == constructor || constructor.getParameterCount() < c.getParameterCount()) {
        constructor = (Constructor<T>) c;
      }
    }

    return constructor;
  }

  public static boolean isNamePresent(final Constructor constructor) {
    return Stream.of(constructor.getParameters())
        .findFirst()
        .map(parameter -> parameter.isNamePresent())
        .orElse(false);
  }

}
