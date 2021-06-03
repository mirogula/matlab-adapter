package sk.stuba.fei.jlab.matlabadapter.rest;

import java.beans.XMLEncoder;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Map;

import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

@Provider
@Produces(MediaType.APPLICATION_XML)
public class MapToXmlMessageBodyWriter implements MessageBodyWriter<Map<?, ?>> {

	@Override
	public long getSize(Map<?, ?> map, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		// deprecated by JAX-RS 2.0 
		return 0;
	}

	@Override
	public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
		return Map.class.equals(type);
	}

	@Override
	public void writeTo(Map<?, ?> map, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
			MultivaluedMap<String, Object> headers, OutputStream entityStream) throws IOException, WebApplicationException {
		XMLEncoder encoder = new XMLEncoder(entityStream);
		encoder.writeObject(map);
		encoder.flush();
	}

}
