package kenneth.jf.siaapp;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

/**
 * Created by Kenneth on 12/10/2016.
 */

public class ConnectionInformation {

    public ArrayList<Ticket> getTicketList() {
        return ticketList;
    }

    public void setTicketList(ArrayList<Ticket> ticketList) {
        this.ticketList = ticketList;
    }

    public ArrayList<String> getNumList() {
        return numList;
    }

    public void setNumList(ArrayList<String> numList) {
        this.numList = numList;
    }

    public ArrayList<Ticket> ticketList;
    public ArrayList<String> numList;

/*    protected Long buildingId;

    public Long getBuildingId() {
        return buildingId;
    }

    public void setBuildingId(Long buildingId) {
        this.buildingId = buildingId;
    }*/

    protected String data;
    protected String url;
    protected RestTemplate restTemplate;
    protected boolean isAuthenticated;
    protected HttpHeaders headers = new HttpHeaders();
    public void setHeaders(HttpHeaders h){
        this.headers = h;
    }
    public HttpHeaders getHeaders(){
        return this.headers;
    }
    public void setIsAuthenticated(boolean t){
        this.isAuthenticated = t;
    }
    public boolean getAuthenticated(){

        return isAuthenticated;
    }
    public String getData() {
        return data;
    }
    public RestTemplate getRestTemplate() {

        return restTemplate;
    }
    public void setData(String data) {
        this.data = data;
    }
    public void setRestTemplate(RestTemplate rest){

        this.restTemplate = rest;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    private static final ConnectionInformation holder = new ConnectionInformation();
    public static ConnectionInformation getInstance() {return holder;}
}
