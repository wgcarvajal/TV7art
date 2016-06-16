package tv7art.com.tv7art.networking;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;
import java.net.SocketTimeoutException;

/**
 * Created by geovanny on 15/06/16.
 */

public class HttpAsyncTask extends AsyncTask<String, Void, Response>
{

    public static final int GET = 0;
    public static final int POST = 1;
    public static final int PUT = 2;
    public static final int DELETE = 3;

    public interface OnHttpResponse
    {
        void onResponse(Response response);
    }

    int method;
    OnHttpResponse response;
    Context context;

    public HttpAsyncTask(int method, OnHttpResponse response , Context context)
    {
        this.method = method;
        this.response = response;
        this.context = context;
    }

    @Override
    protected Response doInBackground(String... params)
    {
        HttpConnection con = new HttpConnection();

        Response response=null;

        if(isConnected())
        {
            try {
                switch (method) {
                    case GET:
                        response = con.get(params[0]);
                        break;
                    case POST:
                        response = con.post(params[0],params[1]);
                        break;
                    case PUT:
                        response = con.put(params[0],params[1]);
                        break;
                    case DELETE:
                        response = con.delete(params[0],params[1]);
                        break;
                }
                response.setError(HttpError.NO_ERROR);

            }catch (SocketTimeoutException e){
                response = new Response(HttpError.TIMEOUT);
            }catch(IOException e){
                response = new Response(HttpError.SERVER);
            }
        }
        else
        {
            response = new Response(HttpError.NO_INTERNET);
        }

        return response;
    }

    @Override
    protected void onPostExecute(Response s) {
        this.response.onResponse(s);
    }

    private boolean isConnected(){
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        if(activeNetwork!=null)        {

            return activeNetwork.isConnectedOrConnecting();
        }
        return false;

    }
}

