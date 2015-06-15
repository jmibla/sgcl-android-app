package project_sgcl.sgclapp;

import android.app.ActionBar;
import android.app.ListActivity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import com.github.kevinsawicki.http.*;

import java.lang.*;
import java.util.List;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.prefs.*;


import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import android.support.v7.app.AppCompatActivity;

import android.app.ActionBar;

public class MainActivity extends Activity implements OnClickListener {

    private Button scanBtn, manualBarcode, PrefServerBtn;
    private TextView formatTxt, contentTxt;
    private TextView productsFound, productCode, productName, productNConsumerunit;
    private EditText inputProduct, inputIP_SERVER, inputPort_SERVER;
    private EditText barcode_search;
    private String inputBarcode = null;
    List<Product> products;
    ListView lv;
    ProductAdapter productAdapter;
    String IP_SERVER, PORT_SERVER;
    private String Pref_AppSGCL = "Pref_AppSGCL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        manualBarcode = (Button) findViewById(R.id.enter_manual_barcode);
        scanBtn = (Button)findViewById(R.id.scan_button);
        formatTxt = (TextView)findViewById(R.id.scan_format);
        contentTxt = (TextView)findViewById(R.id.scan_content);

        barcode_search = (EditText) findViewById(R.id.barcode_search);
        inputBarcode = barcode_search.getText().toString();
        manualBarcode.setOnClickListener(this);
        scanBtn.setOnClickListener(this);
        productsFound = (TextView) findViewById(R.id.productsFound);


        //SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        SharedPreferences preferences = getSharedPreferences(Pref_AppSGCL, Context.MODE_PRIVATE);
        String IP_new = preferences.getString("IP_SERVER", "0.0.0.0");
        String Port_new =  preferences.getString("PORT_SERVER", "80");
        if(IP_new.equals("0.0.0.0") || Port_new.equals("80"))
        {
            Toast toast = Toast.makeText(
                    this,
                    "CONFIGURE IP Y PUERTO-",
                    Toast.LENGTH_LONG
            );
            toast.show();
        }
        else
        {
            Toast toast = Toast.makeText(
                    this,
                    "- cambiannnnn -",
                    Toast.LENGTH_LONG
            );
            toast.show();
        }


        products = new ArrayList<Product>();
        lv = (ListView) findViewById(R.id.listView);
        productAdapter = new ProductAdapter(this, products);
        lv.setAdapter(productAdapter);
    }


    public static class PrefsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.app_prefs);
        }
    }

    public class SettingsActivity extends Activity
    {
        @Override
        protected  void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        //MenuInflater inflater = getMenuInflater();
        //menu.clear();
        //inflater.inflate(R.menu.menu_main, menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId())
        {
            //case R.id.config_IP_server:
            case R.id.action_settings:
                //inputIP_SERVER = (EditText) findViewById(R.id.config_IP_server);
                //IP_SERVER = inputIP_SERVER.getText().toString();
                //REVISARRRRRRRRRRRRRRRRRRRRRRRRRRRRR
                ////////////IPServerBtn = (Button) findViewById(R.id.action_settings);
                //IPServerBtn.setOnClickListener();

                //serverConfiguration();

                getFragmentManager().beginTransaction().replace(android.R.id.content, new PrefsFragment(), "Configuracionnnn").commit();

                Toast toast = Toast.makeText(
                        this,
                        "- estoy en IPSERVER -",
                        Toast.LENGTH_LONG
                );
                toast.show();
                return true;
            case R.id.about:
                //showAbout(); programarrrrrrrrrrrrrrrrr
                Toast toast2 = Toast.makeText(
                        this,
                        "- estoy en ABOUT -",
                        Toast.LENGTH_LONG
                );
                toast2.show();
                return true;
            case R.id.exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showAbout()
    {
    }

    public void serverConfiguration()
    {
        setContentView(R.layout.serverconfig);

        inputIP_SERVER = (EditText) findViewById(R.id.editIPServer);
        inputPort_SERVER = (EditText) findViewById(R.id.editPortServer);
        IP_SERVER = inputIP_SERVER.getText().toString();
        PORT_SERVER = inputPort_SERVER.getText().toString();

        PrefServerBtn = (Button) findViewById(R.id.prefServerBtn);
        PrefServerBtn.setOnClickListener(this);

        Toast toast = Toast.makeText(
                this,
                "introducisteeeee -> " + IP_SERVER + PORT_SERVER,
                Toast.LENGTH_LONG
        );
        toast.show();

    }


    public void onClick(View v)
    {
        if(v.getId() == R.id.scan_button)
        {
            IntentIntegrator scanIntegrator = new IntentIntegrator(this);
            scanIntegrator.initiateScan();
        }
        if (v.getId() == R.id.enter_manual_barcode)
        {
            inputBarcode = barcode_search.getText().toString();
            if (inputBarcode.isEmpty()) {
                Toast toast = Toast.makeText(
                        this,
                        "RECUERDA INTRODUCIR EL CÓDIGO",
                        Toast.LENGTH_LONG
                );
                toast.show();
            }
            else
            {
                int lengthBarcode = inputBarcode.length();
                if(lengthBarcode==12 || lengthBarcode == 13 || lengthBarcode == 14)
                {
                    formatTxt.setText("");
                    contentTxt.setText("CODIGO: " + inputBarcode);
                    searchProduct(inputBarcode);
                }
                else
                {
                    Toast toast = Toast.makeText(
                            this,
                            "RECUERDA QUE LA LONGUITD DEL CÓDIGO DEBE SER 12, 13 o 14 dígitos",
                            Toast.LENGTH_LONG
                    );
                    toast.show();
                }
            }
        }
        if(v.getId() == R.id.prefServerBtn){
            SharedPreferences preferences = getSharedPreferences(Pref_AppSGCL, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("IP_SERVER", IP_SERVER);
            editor.putString("PORT_SERVER", PORT_SERVER);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        String scanContent = scanningResult.getContents();
        String scanFormat = scanningResult.getFormatName();

        formatTxt.setText("TIPO DE CODIGO: " + scanFormat);
        contentTxt.setText("CODIGO: " + scanContent);

        searchProduct(scanContent);
    }

    public void searchProduct(String barcode)
    {
        //String IPserver = "192.168.43.234";
        String IPserver = "192.168.100.112";
        //String IPserver = "192.168.1.5";
        String url = "http://" + IPserver + ":8008/app_dev.php/api/products/" + barcode;
        new LoadProductTask().execute(url);
    }

    public static final String TAG = "project_sgcl.sgclapp";

    private class LoadProductTask extends AsyncTask<String, Long, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            try {
                return HttpRequest.get(urls[0]).accept("application/json").body();
            }
            catch(HttpRequest.HttpRequestException e) {
                //throw new RuntimeException("ERROR AL CONECTAR CON EL SERVIDOR: " + e.toString());
                return null;
            }
        }

        @Override
        protected void onPostExecute(String response)
        {
            products = prettyfyJSON(response);

            if (products.isEmpty())
            {
                productsFound.setText("Actualmente este código no está asignado "
                        + "a ninguno de nuestros productos");
            }
            else
            {
                if (products.size() == 1)
                {
                    productsFound.setText("Se ha encontrado un solo producto en el sistema.");
                }
                else
                {
                    productsFound.setText("Se han encontrado " + products.size()
                            + " productos en el sistema.");
                }

                productAdapter.getItems().clear();
                productAdapter.getItems().addAll(products);
                productAdapter.notifyDataSetChanged();
            }
        }
    }


    private List<Product> prettyfyJSON(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
