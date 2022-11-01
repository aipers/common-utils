package com.aipers.groupware.common.utilities;

import java.util.Arrays;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class StreamUtils {

  /**
   * 주어진 객체를 Stream 으로 변환합니다
   * @param o
   * @return
   */
  public final static Stream stream(final Object o) {
    if (!CommonUtils.isIterable(o)) return null;

    return Stream.class.isAssignableFrom(o.getClass())
        ? (Stream) o
        : Iterable.class.isAssignableFrom(o.getClass())
            ? iterableAsStream((Iterable) o)
            : o.getClass().isArray()
                ? Arrays.stream((Object[]) o)
                : Enumeration.class.isAssignableFrom(o.getClass())
                    ? enumerationAsStream((Enumeration) o)
                    : iteratorAsStream((Iterator) o);
  }

  /**
   * Iterable 객체를 Stream 형태로 반환합니다
   * @param e
   * @return
   * @param <T>
   */
  public final static <T> Stream<T> iterableAsStream(final Iterable<T> e) {
    return StreamSupport.stream(e.spliterator(), false);
  }

  /**
   * Iterator 객체를 Stream 형태로 반환합니다
   * @param it
   * @return
   * @param <T>
   */
  public final static <T> Stream<T> iteratorAsStream(final Iterator<T> it) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(it, Spliterator.ORDERED),
        false
    );
  }

  /**
   * Enumeration 객체를 Stream 형태로 반환합니다
   * @param e
   * @return
   * @param <T>
   */
  public final static <T> Stream<T> enumerationAsStream(final Enumeration<T> e) {
    return StreamSupport.stream(
        Spliterators.spliteratorUnknownSize(
            new Iterator<T>() {
              @Override
              public T next() {
                return e.nextElement();
              }

              @Override
              public boolean hasNext() {
                return e.hasMoreElements();
              }
            },
            Spliterator.ORDERED
        ), false
    );
  }

  /**
   * 주어진 리스트의 객체를 다른 유형의 객체로 변환한 새 리스트를 반환 합니다
   * @param from
   * @param mapper
   * @return
   * @param <T>
   * @param <U>
   */
  public final static <T, U> List<U> convertList(final List<T> from, final Function<T, U> mapper) {
    if (null == from) return null;

    return from.stream().map(mapper).collect(Collectors.toList());
  }

  /**
   * 주어진 배열의 객체를 다른 유형의 객체로 변환한 새 배열을 반환 합니다
   * @param from
   * @param mapper
   * @param generator
   * @return
   * @param <T>
   * @param <U>
   */
  public final static <T, U> U[] convertArray(
      final List<T> from, final Function<T, U> mapper, final IntFunction<U[]> generator
  ) {
    if (null == from) return null;

    return from.stream().map(mapper).filter(Objects::nonNull).toArray(generator);
  }

}
