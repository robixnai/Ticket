package br.com.cast.ticket.http;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.cast.ticket.entity.BusTicket;
import br.com.cast.ticket.entity.Ticket;

/**
 * Ticket singleton service for HTTP calls.
 *
 * @author falvojr
 */
public class HttpTicketService {

    private static final String API_URL_BUS = "http://api-poctickets.herokuapp.com/api/v1.0/tickets/bus";
    private static final String API_URL_SUBWAY = "http://api-poctickets.herokuapp.com/api/v1.0/tickets/subway";

    /**
     * Private singleton constructor.
     */
    private HttpTicketService() {
        super();
    }

    /**
     * Lazy holder for singleton with Bill Pugh's solution.
     */
    private static class LazyHolder {
        private static final HttpTicketService INSTANCE = new HttpTicketService();
    }

    /**
     * get the {@link HttpTicketService} singleton instance using Bill Pugh's solution.
     *
     * @return {@link HttpTicketService} instance.
     */
    public static HttpTicketService getInstance() {
        return LazyHolder.INSTANCE;
    }

    public static void postTicket(Ticket ticket) {
        HttpURLConnection conn = null;

        try {
            URL urlCon;

            if (ticket.getData() instanceof BusTicket) {
                urlCon = new URL("https://api-poctickets.herokuapp.com/api/v1.0/tickets/bus");
            } else {
                urlCon = new URL("https://api-poctickets.herokuapp.com/api/v1.0/tickets/subway");
            }

            conn = (HttpURLConnection) urlCon.openConnection();
            //conn.setRequestProperty("Connection", "close");
            conn.setRequestMethod("POST");
            //conn.setDoInput(true);
            conn.addRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(ticketToJsonBytes(ticket));
            os.flush();
            os.close();


            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            conn.disconnect();
        } catch (Exception e) {
            throw new RuntimeException("Error opening connection " + e.getMessage());
        }finally {
            if(conn != null){
                conn.disconnect();
            }
        }
    }

    private static byte[] ticketToJsonBytes(Ticket ticket) {
        try {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("alias", ticket.getAlias());
            jsonUser.put("passenger_type", ticket.getData().getPassengerType().getId());
            //jsonUser.put("line_type",ticket.getData().getLine());
            jsonUser.put("ticket_tax_type", ticket.getTaxType().getId());
            jsonUser.put("quantity", ticket.getData().getQuantity());
            jsonUser.put("unit_tax", ticket.getData().getUnitTax());
            jsonUser.put("qr_code", ticket.getData().getQrCode());
            jsonUser.put("expiration", ticket.getData().getExpiration());
            jsonUser.put("user_id", 1);

            return jsonUser.toString().getBytes();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }


}
