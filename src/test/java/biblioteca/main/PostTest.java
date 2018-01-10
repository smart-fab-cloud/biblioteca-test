package biblioteca.main;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import static junit.framework.Assert.assertEquals;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import static org.junit.Assert.*;

public class PostTest {
    
    private WebTarget biblio;
    
    public PostTest() { 
        // Creazione del client e sua connessione a "punteggi"
        Client cli = ClientBuilder.newClient();
        this.biblio = cli.target("http://localhost:50004/biblioteca");
    }
    
    private JSONObject creaLibroDefault() {
        JSONObject libro = new JSONObject();
        libro.put("isbn","8841809558");
        libro.put("titolo", "Eneide");
        JSONArray autori = new JSONArray();
        autori.add("Virgilio");
        libro.put("autori", autori);
        libro.put("descrizione", "Narra di Roma e di Troia.");
        return libro;
    }
    
    @Test
    public void testPostCreated() throws ParseException {
        JSONObject libro = creaLibroDefault();
        
        // Richiesta di aggiunta di "libro"
        Response rPost = biblio.request().post(Entity.entity(
                            libro.toJSONString(), 
                            MediaType.APPLICATION_JSON)
                         );

        // Reperimento del "libro" aggiunto
        String isbn = (String) libro.get("isbn");
        Response rGet = biblio.path(isbn)
                            .request()
                            .get();
        // Rimozione del punteggio aggiunto
        // (per ripristinare lo stato precedente al test)
        biblio.path(isbn).request().delete();
        
        // Verifica che la risposta rPost ottenuta sia "201 Created"
        assertEquals(Status.CREATED.getStatusCode(), rPost.getStatus());
        // Verifica che il record sia stato creato
        assertEquals(Status.OK.getStatusCode(), rGet.getStatus());
        // Verifica che il record aggiunto sia corretto
        JSONParser parser = new JSONParser();
        JSONObject p = (JSONObject) parser.parse(rGet.readEntity(String.class));
        assertEquals(libro.get("isbn"), p.get("isbn"));
        assertEquals(libro.get("titolo"), p.get("titolo"));
        assertEquals(libro.get("autori"), p.get("autori"));
        assertEquals(libro.get("descrizione"), p.get("descrizione"));
    }    
    
    @Test
    public void testPostBadRequest() {
        // creazione di un libro con isbn vuoto
        JSONObject libro = creaLibroDefault();
        libro.remove("isbn");
        libro.put("isbn", "");
        
        // Tentativo di post del libro con isbn vuoto
        Response rPost = biblio.request().post(Entity.entity(
                            libro.toJSONString(), 
                            MediaType.APPLICATION_JSON)
                         );
        
        // Verifica che la risposta ottenuta sia "404 Bad Request"
        assertEquals(Status.BAD_REQUEST.getStatusCode(), rPost.getStatus());
    }
    
    @Test
    public void testPostConflict() {
        JSONObject libro = creaLibroDefault();
        
        // Aggiunta di un libro
        Response rPost = biblio.request().post(Entity.entity(
                            libro.toJSONString(), 
                            MediaType.APPLICATION_JSON)
                         );

        // Tentativo di aggiunta dello stesso libro
        Response rPost2 = biblio.request().post(Entity.entity(
                            libro.toJSONString(), 
                            MediaType.APPLICATION_JSON)
                         );
        
        // Rimozione del libro aggiunto
        biblio.path((String) libro.get("isbn")).request().delete();
  
        // Verifica che la risposta ottenuta sia "409 Conflict"
        assertEquals(Status.CONFLICT.getStatusCode(), rPost2.getStatus());
    }
}
