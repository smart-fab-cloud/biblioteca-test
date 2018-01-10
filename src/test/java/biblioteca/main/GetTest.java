package biblioteca.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.junit.Test;
import static org.junit.Assert.*;

public class GetTest {
    
    private WebTarget biblio;
    
    public GetTest() { 
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        biblio = cli.target("http://localhost:50004/biblioteca");
    }    
    
    @Test
    public void testGetNotAllowed() {
        Response rGet = biblio.request().get();
        assertEquals(405, rGet.getStatus());
    }
}
