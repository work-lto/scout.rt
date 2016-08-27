package org.eclipse.scout.rt.mom.api.marshaller;

import static org.eclipse.scout.rt.platform.util.Assertions.assertInstance;

import java.util.Map;

import org.eclipse.scout.rt.platform.Bean;

/**
 * This marshaller allows to transport a series of bytes across the network.
 * <p>
 * This marshaller does not support serialization of exceptions.
 *
 * @see IMarshaller#MESSAGE_TYPE_BYTES
 * @since 6.1
 */
@Bean
public class BytesMarshaller implements IMarshaller {

  @Override
  public Object marshall(final Object transferObject, final Map<String, String> context) {
    if (transferObject == null) {
      return null;
    }

    return (byte[]) assertInstance(transferObject, byte[].class, "bytes array expected [actual={}]", transferObject.getClass().getSimpleName());
  }

  @Override
  public Object unmarshall(final Object data, final Map<String, String> context) {
    return (byte[]) data;
  }

  @Override
  public int getMessageType() {
    return MESSAGE_TYPE_BYTES;
  }
}
