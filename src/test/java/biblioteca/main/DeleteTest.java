package biblioteca.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import org.junit.Test;
import static org.junit.Assert.*;

public class DeleteTest {
    
    private WebTarget biblio;

    public DeleteTest()  { 
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        biblio = cli.target("http://localhost:50004/biblioteca");
    }    
    
    @Test
    public void testDeleteNotAllowed() {
        Response rDelete = biblio.request().delete();
        assertEquals(405, rDelete.getStatus());
    }
    
}
