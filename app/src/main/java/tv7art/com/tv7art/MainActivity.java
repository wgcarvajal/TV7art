package tv7art.com.tv7art;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import tv7art.com.tv7art.networking.HttpAsyncTask;

public class MainActivity extends AppCompatActivity implements HttpAsyncTask.OnHttpResponse  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResponse(String response)
    {


    }
}
