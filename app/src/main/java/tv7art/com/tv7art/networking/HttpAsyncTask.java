package tv7art.com.tv7art.networking;

import android.os.AsyncTask;
import android.util.Log;
import java.io.IOException;

/**
 * Created by geovanny on 15/06/16.
 */

public class HttpAsyncTask extends AsyncTask<String, Integer, String> {

        public static final int GET = 0;
        public static final int POST = 1;
        public static final int PUT = 2;
        public static final int DELETE = 3;

        public interface OnHttpResponse{
            void onResponse(String response);
        }

        int method;
        OnHttpResponse response;

        public HttpAsyncTask(int method, OnHttpResponse response) {
            this.method = method;
            this.response = response;
        }

        @Override
        protected String doInBackground(String... params) {
            HttpConnection con = new HttpConnection();

            String rta=null;
            try{
                switch (method){
                    case GET:
                        rta = con.get(params[0]);
                        break;
                    case POST:
                        rta = con.post(params[0], params[1]);
                        break;
                    case PUT:
                        rta = con.put(params[0], params[1]);
                        break;
                    case DELETE:
                        rta = con.delete(params[0], params[1]);
                        break;
                }
            }catch(IOException e)
            {
                Log.i("error:", e.getMessage());
            }
            return rta;
        }

        @Override
        protected void onPostExecute(String s) {
            response.onResponse(s);
        }
}

