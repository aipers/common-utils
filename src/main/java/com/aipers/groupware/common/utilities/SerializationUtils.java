package com.aipers.groupware.common.utilities;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public abstract class SerializationUtils {

  /**
   * 객체를 직렬화 합니다
   * @param data
   * @return
   * @throws IOException
   */
  public static final String serialized(final Object data) throws IOException {
    try (
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(os)
    ) {
      oos.writeObject(data);

      return StringUtils.encodeBase64(os.toByteArray());
    } catch(IOException e) {
      throw e;
    }
  }

  /**
   * 역직렬화하여 객체로 변환합니다
   * @param serialized
   * @param type
   * @return
   * @param <T>
   * @throws IOException
   */
  public static final <T> T deserialized(final String serialized, final Class<T> type) throws IOException {
    try (
        final ByteArrayInputStream is = new ByteArrayInputStream(StringUtils.decodeBase64(serialized));
        final ObjectInputStream ois = new ObjectInputStream(is)
    ) {
      return type.cast(ois.readObject());
    } catch (IOException e) {
      throw e;
    } catch (ClassNotFoundException e) {
      throw new IOException(e);
    }
  }

}
