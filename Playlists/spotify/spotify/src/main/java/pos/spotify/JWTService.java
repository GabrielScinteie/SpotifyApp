package pos.spotify;

import org.apache.tomcat.websocket.AuthenticationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.xml.bind.JAXBException;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Objects;

import static java.lang.Integer.parseInt;

@Service
public class JWTService {

    @Autowired
    private SoapConfig config;
    public String validateJWT(String jwt){
        String response = "";
        try{
            response = config.soapClient(config.marshaller()).AuthorizeUser(jwt);

            if(response.contains("Eroare")){
                switch (response){
                    case "Eroare, JWT se afla in blocked list!" -> throw new AuthenticationException("Blocked JWT");
                    case "Eroare, JWT semnatura invalida!" -> throw new AuthenticationException("Invalid signature");
                    case "Eroare, invalid user_id!" -> throw new AuthenticationException("Invalid user id");
                    case "Eroare, rol pretins invalid!" -> throw new AuthenticationException("Invalid roles");
                    default -> {}
                }
            }
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        } catch (AuthenticationException e) {
            System.out.println(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }

        return response;
    }

    public Boolean isContentManager(String response){
        String[] splitted = response.split("\\|\\|");
        if(Objects.equals(splitted[1], "content manager"))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public String getUserId(String response)
    {
        String[] splitted = response.split("\\|\\|");
        return splitted[0];
    }

    public Boolean isArtist(String response){
        String[] splitted = response.split("\\|\\|");
        if(Objects.equals(splitted[1], "artist"))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public Boolean isClient(String response){
        String[] splitted = response.split("\\|\\|");
        if(Objects.equals(splitted[1], "client"))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }

    public Boolean isAdmin(String response){
        String[] splitted = response.split("\\|\\|");
        if(Objects.equals(splitted[1], "administrator"))
            return Boolean.TRUE;
        return Boolean.FALSE;
    }
}



