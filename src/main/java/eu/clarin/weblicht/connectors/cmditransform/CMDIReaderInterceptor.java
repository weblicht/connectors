package eu.clarin.weblicht.connectors.cmditransform;

import eu.clarin.weblicht.bindings.CenterProfileHelper;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.ext.ReaderInterceptor;
import javax.ws.rs.ext.ReaderInterceptorContext;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by wqiu on 06/12/16.
 */
public class CMDIReaderInterceptor implements ReaderInterceptor {
    @Override
    public Object aroundReadFrom(ReaderInterceptorContext context) throws IOException, WebApplicationException {
        final InputStream cmdi11 = context.getInputStream();
        try {
            InputStream cmdi12 = CenterProfileHelper.updateToCmdi12AndCenterProfile2(cmdi11);
            context.setInputStream(cmdi12);
            return context.proceed();
        } catch (TransformerConfigurationException e) {
            throw new WebApplicationException(e);
        } catch (TransformerException e) {
            throw new WebApplicationException(e);
        }
    }

//    InputStream debugXml(InputStream input) {
//        byte[] data = new byte[]{};
//        try {
//            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//            input.transferTo(outputStream);
//            data = outputStream.toByteArray();
//
//            System.out.println(new String(data));
//
//            JAXBContext ex = JAXBContext.newInstance(CenterProfileCMD.class);
//            Unmarshaller unmarshaller = ex.createUnmarshaller();
//            unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());
//            unmarshaller.unmarshal(new ByteArrayInputStream(data));
//        } catch (IOException e) {
//            throw new WebApplicationException(e);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
//        return new ByteArrayInputStream(data);
//    }
}
