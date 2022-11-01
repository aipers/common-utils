package com.aipers.groupware.common.utilities;

import com.aipers.groupware.common.Constants;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public abstract class AsyncUtils {

  public static final ExecutorService executor;

  static {
    executor = CommonUtils.isTrue(System.getenv("use.future"))
        ? Executors.newFixedThreadPool(Constants.MAX_PROMISE_THREAD_COUNT)
        : null;
  }

  /**
   * ExecutorService 이용 하여 동시 처리할 명령 집합 실행 Function 만들어 리턴 한다.
   * @param suppliers
   * @return
   */
  public static final CompletableFuture[] getSuppliers(final Supplier<Object>... suppliers) {
    if (null == executor) {
      throw new RuntimeException("Future Promise not supported. Check the settings options.");
    }

    return IntStream
        .range(0, suppliers.length)
        .mapToObj(idx -> CompletableFuture.supplyAsync(() -> {
              try {
                return suppliers[idx].get();
              } catch(Exception e) {
                return null;
              }
            }, executor)
        )
        .toArray(CompletableFuture[]::new);
  }

  /**
   * 메서드 모음을 병렬로 실행 한다.
   * @param futures
   */
  public static void executeSupplier(final CompletableFuture[] futures) {
    CompletableFuture
        .allOf(futures)
        .exceptionally(throwable -> null)
        .join();
  }

  /**
   * 동시에 처리할 메서드를 전달 받아 병령처리 후 결과 값을 List 형태로 전달한다.
   * using) AsyncUtils.promiseAll(() -> methodCall1, () -> methodCall2, () -> methodCall3 ...)
   * @param suppliers
   * @return
   */
  public static List<Object> promiseAll(final Supplier<Object>... suppliers) {
    final CompletableFuture[] futures = getSuppliers(suppliers);

    executeSupplier(futures);

    return Arrays.stream(futures)
        .map(AsyncUtils::getFuture)
        .collect(Collectors.toList());
  }

  private static Object getFuture(final CompletableFuture future) {
    try {
      return future.get();
    } catch(ExecutionException | InterruptedException e) {
      return null;
    }
  }

}
