package biblioteca.isbn;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;

public class DeleteTest {

    private WebTarget biblio;
    private JSONObject libro;
    
    public DeleteTest() {
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
    public void testGetOk() throws ParseException {
        // Eliminazione del libro
        Response rDelete = biblio.path((String) libro.get("isbn"))
                                .request()
                                .delete();
        
        // Tentativo di reperimento del libro eliminato
        Response rGet = biblio.path((String) libro.get("isbn"))
                                .request()
                                .delete();
        
        // Verifica che la risposta rDelete sia "200 Ok"
        assertEquals(Status.OK.getStatusCode(),rDelete.getStatus());
        // Verifica che la risposta rGet sia "404 Not Found"
        assertEquals(Status.NOT_FOUND.getStatusCode(),rGet.getStatus());
    }
    
    @Test 
    public void testGetNotFound() {
        // Eliminazione di un libro inesistente
        Response rDelete = biblio.path((String) libro.get("titolo"))
                                .request()
                                .delete();
        
        // Verifica che la risposta sia 404 Not Found
        assertEquals(Status.NOT_FOUND.getStatusCode(),rDelete.getStatus());
    }
    
}
