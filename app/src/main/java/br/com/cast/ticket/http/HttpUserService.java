package br.com.cast.ticket.http;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import br.com.cast.ticket.entity.User;
import br.com.cast.ticket.util.Mask;

/**
 * Created by guilherme on 22/07/15.
 */
public class HttpUserService {

    private static final String API_URL = "http://api-poctickets.herokuapp.com/api/v1.0/users";

    public static void post(User user) {
        try {
            URL urlCon = new URL(API_URL);
            HttpURLConnection conn = (HttpURLConnection) urlCon.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.addRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();
            os.write(userToJsonBytes(user));
            os.flush();
            os.close();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_CREATED) {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }
            conn.disconnect();
        } catch (Exception e) {
            throw new RuntimeException("Error opening connection " + e.getMessage());
        }
    }

    private static byte[] userToJsonBytes(User user) {
        try {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("first_name", user.getFirstName());
            jsonUser.put("last_name", user.getLastName());
            jsonUser.put("cell_phone", Mask.unmask(user.getCellPhone()));
            jsonUser.put("email", user.getEmail());
            jsonUser.put("password", user.getPassword());

            return jsonUser.toString().getBytes();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
