package biblioteca.isbn;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class PostTest {
    
    private WebTarget biblio;
    private JSONObject libro;
    
    public PostTest() {
        // Creazione del client e connessione al servizio
        Client cli = ClientBuilder.newClient();
        biblio = cli.target("http://localhost:50004/biblioteca");
        // e inizializzazione dati test
        libro = new JSONObject();
        libro.put("isbn","8841809558");
        libro.put("titolo", "Eneide");
        JSONArray autori = new JSONArray();
        autori.add("Virgilio");
        libro.put("autori", autori);
        libro.put("editore", "De Agostini");
        libro.put("descrizione", "Narra di Roma e di Troia.");
    }
    
    @Before
    public void aggiuntaLibro() {
        biblio.request().post(Entity.entity(
                            libro.toJSONString(), 
                            MediaType.APPLICATION_JSON)
                         );
    }
    
    @Test
    public void testPostNotAllowed() {
        Response rPost = biblio.path((String) libro.get("isbn"))
                            .request()
                            .post(Entity.entity("", MediaType.TEXT_PLAIN));
        
        assertEquals(405, rPost.getStatus());
    }
    
    @After
    public void eliminazioneLibro() {
        biblio.path((String) libro.get("isbn")).request().delete();
    }       
}
