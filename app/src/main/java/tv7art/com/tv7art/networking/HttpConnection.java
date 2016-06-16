package tv7art.com.tv7art.networking;


import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by geovanny on 15/06/16.
 */
public class HttpConnection {

    public Response get(String url) throws IOException {
        URL u = new URL(url);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();

        con.setRequestMethod("GET");
        con.setDoInput(true);

        con.connect();

        InputStream in = con.getInputStream();
        Response response =  new Response(streamToString(in), con.getResponseCode());
        return response;
    }

    public Response post(String url, String json) throws IOException {
        return request("POST", url, json);
    }

    public Response put(String url, String json) throws IOException {
        return request("PUT", url, json);
    }

    public Response delete(String url, String json) throws IOException {
        return request("DELETE", url, json);
    }

    private Response request(String method, String url, String json) throws IOException {

        URL u = new URL(url);
        HttpURLConnection con = (HttpURLConnection) u.openConnection();

        con.setRequestMethod(method);
        con.setDoInput(true);

        if(json!=null)
            con.setDoOutput(true);

        con.setRequestProperty("Content-Type","application/json");
        con.connect();

        if(json!=null){
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(json.getBytes());
            out.flush();
            out.close();
        }

        InputStream in = con.getInputStream();
        Response response =  new Response(streamToString(in), con.getResponseCode());
        return response;
    }

    private String streamToString(InputStream in) throws  IOException
    {
        InputStreamReader reader = new InputStreamReader(in);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int ch;
        while ((ch= reader.read())!= -1)
        {
            out.write(ch);
        }

        String msg = new String(out.toByteArray());
        in.close();
        reader.close();
        out.close();

        return msg;
    }
}