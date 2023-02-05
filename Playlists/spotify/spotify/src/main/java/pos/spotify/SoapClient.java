package pos.spotify;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;
import wsdl.Authorize;
import wsdl.AuthorizeResponse;
import wsdl.ObjectFactory;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class SoapClient extends WebServiceGatewaySupport{
    public String AuthorizeUser(String token) throws JAXBException{
        JAXBContext context = JAXBContext.newInstance(Authorize.class);
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        Authorize request = new Authorize();

        ObjectFactory objectFactory = new ObjectFactory();

        JAXBElement<String> jaxbElement = objectFactory.createAuthorizeMyJwt(token);
        request.setMyJwt(jaxbElement);
        JAXBElement<Authorize> auth = objectFactory.createAuthorize(request);

        JAXBElement<AuthorizeResponse> authorizeResponse = (JAXBElement<AuthorizeResponse>) getWebServiceTemplate().marshalSendAndReceive("http://127.0.0.1:7999", auth, null);

        return authorizeResponse.getValue().getAuthorizeResult().getValue();
    }
}