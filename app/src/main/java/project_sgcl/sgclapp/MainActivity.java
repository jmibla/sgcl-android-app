
/**
 * User: Joaquín & María * Date: 02/03/15
 * Time: 20:12
 */

package project_sgcl.sgclapp;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentTransaction;
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

import java.io.IOException;
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

    private Button scanBtn, manualBarcode;
    private TextView formatTxt, contentTxt;
    private TextView productsFound;
    private EditText barcode_search;
    private String inputBarcode = null;
    public static String ErrorConnectingServer = "errorConnectionServer";
    List<Product> products;
    ListView lv;
    ProductAdapter productAdapter;
    String IP_SERVER, PORT_SERVER;

    /**
     *
     * @param savedInstanceState
     */
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



        ActionBar actionBar = getActionBar();
        actionBar.setTitle("SGCL");

        products = new ArrayList<Product>();
        lv = (ListView) findViewById(R.id.listView);
        productAdapter = new ProductAdapter(this, products);
        lv.setAdapter(productAdapter);
    }

    /**
     *
     * @return boolean
     *
     * return true: if IP address and port server already configured
     */
    public boolean isAppConfigurated()
    {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());

        IP_SERVER = preferences.getString("IPServer", "0.0.0.0");
        PORT_SERVER =  preferences.getString("PortServer", "0");
        if(IP_SERVER.equals("0.0.0.0") || PORT_SERVER.equals("0"))
        {
            Toast toast = Toast.makeText(
                    this,
                    "Contracte con su ADMINISTRADOR para configurar la aplicación",
                    Toast.LENGTH_LONG
            );
            toast.show();
            return false;
        }
        else
        {
            //IP address and port server already configured
            return true;
        }
    }

    /**
     *
     */
    public void showErrorConnectionServer()
    {

        Toast toast = Toast.makeText(
                this,
                "ERROR AL CONECTAR CON EL SERVIDOR:" +
                        "\n compruebe la conexión WIFI." +
                        "\nEn caso necesario, contacte con su departamento de Sistemas.",
                Toast.LENGTH_LONG
        );
        toast.show();
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

            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new PrefsFragment());
            ft.commit();
        }
    }


    /**
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    /**
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            //Config: set IP and server port
            case R.id.action_settings:
                startActivity(new Intent(this, MyPreferenceActivity.class));
                return true;
            //About menu
            case R.id.aboutAppSGCL:
                Toast toast_aboutAppSGCL = Toast.makeText(
                        this,
                        "Esta aplicación móvil le permite buscar todos los productos del " +
                                "sistema identificados por con el código de barras empleado " +
                                "en la búsqueda.\n" +
                                "Puede introducir el código de barras de " +
                                "forma manual o automática (recuerde que debe tener " +
                                "instalado en su sistema la aplicación Barcoe_Scanner).",
                        Toast.LENGTH_LONG
                );
                toast_aboutAppSGCL.show();
                return true;
            case R.id.aboutConfiguration:
                Toast toast_aboutConfiguration = Toast.makeText(
                        this,
                        "Su ADMINISTRADOR de sistemas, debería haberle entregado " +
                                "la aplicación configurada con la IP y puerto del servidor.\n" +
                                "En caso contrario, contacte con su departamento de Sistemas.",
                        Toast.LENGTH_LONG
                );
                toast_aboutConfiguration.show();
                return true;
            //Close program
            case R.id.exit:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     *
     * @param v
     */
    public void onClick(View v)
    {
        if(isAppConfigurated())
        {
            if (v.getId() == R.id.scan_button) {
                IntentIntegrator scanIntegrator = new IntentIntegrator(this);
                scanIntegrator.initiateScan();
            }
            if (v.getId() == R.id.enter_manual_barcode) {
                inputBarcode = barcode_search.getText().toString();
                if (inputBarcode.isEmpty()) {
                    Toast toast = Toast.makeText(
                            this,
                            "RECUERDA INTRODUCIR EL CÓDIGO",
                            Toast.LENGTH_LONG
                    );
                    toast.show();
                }
                else {
                    int lengthBarcode = inputBarcode.length();
                    if (lengthBarcode == 12 || lengthBarcode == 13 || lengthBarcode == 14) {
                        formatTxt.setText("");
                        contentTxt.setText("CODIGO: " + inputBarcode);
                        searchProduct(inputBarcode);
                    } else {
                        Toast toast = Toast.makeText(
                                this,
                                "RECUERDA QUE LA LONGUITD DEL CÓDIGO DEBE SER 12, 13 o 14 dígitos",
                                Toast.LENGTH_LONG
                        );
                        toast.show();
                    }
                }
            }
        }
        else {
            Toast toast = Toast.makeText(
                    this,
                    "Contacte con su ADMINISTRADOR, es necesario configurar la aplicación",
                    Toast.LENGTH_LONG
            );
            toast.show();

        }
    }

    /**
     *
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(
                requestCode,
                resultCode,
                intent
        );

        String scanContent = scanningResult.getContents();
        String scanFormat = scanningResult.getFormatName();

        formatTxt.setText("TIPO DE CODIGO: " + scanFormat);
        contentTxt.setText("CODIGO: " + scanContent);

        searchProduct(scanContent);
    }

    /**
     *
     * @param barcode
     */
    public void searchProduct(String barcode)
    {
        String url = "http://"
                        + IP_SERVER + ":" + PORT_SERVER
                        + "/app_dev.php/api/products/"
                        + barcode;
        new LoadProductTask().execute(url);
    }

    public static final String TAG = "project_sgcl.sgclapp";

    private class LoadProductTask extends AsyncTask<String, Long, String>
    {
        /**
         *
         * @param urls
         * @return
         */
        @Override
        protected String doInBackground(String... urls)
        {
            try {
                return HttpRequest.get(urls[0]).accept("application/json").body();
            }
            catch(HttpRequest.HttpRequestException e) {
                return ErrorConnectingServer;
            }
        }

        /**
         *
         * @param response
         */
        @Override
        protected void onPostExecute(String response)
        {
            if(response == ErrorConnectingServer)
            {
                showErrorConnectionServer();
            }
            else {
                productAdapter.getItems().clear();
                products = prettyfyJSON(response);

                if (products.isEmpty()) {
                    productsFound.setText("Actualmente este código no está asignado "
                            + "a ninguno de nuestros productos");
                } else {
                    if (products.size() == 1) {
                        productsFound.setText("Se ha encontrado un solo producto en el sistema.");
                    } else {
                        productsFound.setText("Se han encontrado " + products.size()
                                + " productos en el sistema.");
                    }

                    productAdapter.getItems().addAll(products);
                    productAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    /**
     *
     * @param json
     * @return
     */
    private List<Product> prettyfyJSON(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<Product>>(){}.getType();
        return gson.fromJson(json, type);
    }
}
