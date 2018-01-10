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

public class PutTest {

    private WebTarget biblio;
    private JSONObject libro;
    
    public PutTest() {
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
    public void testPutOk() throws ParseException {
        String vecchioIsbn = (String) libro.get("isbn");
        // Cambio di isbn e titolo in "libro"
        libro.remove("isbn");
        libro.put("isbn", "nuovo-isbn");
        libro.remove("titolo");
        libro.put("titolo", "nuovo-titolo");
        
        //  Aggiornamento della descrizione di "libro"
        System.out.println(vecchioIsbn);
        System.out.println(biblio.path(vecchioIsbn).getUri());
        System.out.println("$JSON$ - " + libro.toJSONString());
        Response rPut = biblio.path(vecchioIsbn)
                            .request()
                            .put(Entity.entity(libro.toJSONString(),MediaType.APPLICATION_JSON));
        
        // Reperimento della descrizione aggiornata
        Response rGet = biblio.path((String) libro.get("isbn"))
                            .request()
                            .get();
        
        // Verifica che la risposta sia 200 Ok
        assertEquals(Status.OK.getStatusCode(),rPut.getStatus());
        
        // Verifica che i dati del libro siano corretti
        JSONParser p = new JSONParser();
        JSONObject libroRest = (JSONObject) p.parse(rGet.readEntity(String.class));
        assertEquals(libro.get("titolo"), libroRest.get("titolo"));
        assertEquals(libro.get("autori"), libroRest.get("autori"));
        assertEquals(libro.get("descrizione"), libroRest.get("descrizione"));
    }
    
    @Test 
    public void testPutNotFound() {
        // Aggiornamento di un libro inesistente
        Response rPut = biblio.path((String) libro.get("titolo"))
                            .request()
                            .put(Entity.entity(libro.toJSONString(),MediaType.APPLICATION_JSON));
        
        // Verifica che la risposta sia 404 Not Found
        assertEquals(Status.NOT_FOUND.getStatusCode(),rPut.getStatus());
    }
    
    @After
    public void eliminazioneLibro() {
        biblio.path((String) libro.get("isbn")).request().delete();
    }       
}
