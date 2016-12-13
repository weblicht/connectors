package eu.clarin.weblicht.connectors.cmditransform;

import eu.clarin.weblicht.bindings.CMDITemplateFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wqiu on 06/12/16.
 */
public class CMDIReaderInterceptor implements ReaderInterceptor{
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        final InputStream originalInputStream = context.getInputStream();
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            Result outputTarget = new StreamResult(outputStream);
            Templates templates = CMDITemplateFactory.getTemplates();
            Transformer transformer = templates.newTransformer();
            transformer.transform(new StreamSource(originalInputStream), outputTarget);
            InputStream is = new ByteArrayInputStream(outputStream.toByteArray());
            context.setInputStream(is);
            return context.proceed();
        } catch (TransformerConfigurationException e) {
            throw new WebApplicationException(e);
        } catch (TransformerException e) {
            throw new WebApplicationException(e);
        }
    }
}
