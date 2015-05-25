package project_sgcl.sgclapp;

import android.app.ListActivity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.Menu;
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


public class MainActivity extends Activity implements OnClickListener {

    private Button scanBtn, manualBarcode;;
    private TextView formatTxt, contentTxt;
    private TextView productsFound, productCode, productName;
    private EditText inputProduct;
    private EditText barcode_search;
    private String inputBarcode = null;
    List<Product> products;
    ListView lv;
    ProductAdapter productAdapter;

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


        products = new ArrayList<Product>();
        lv = (ListView) findViewById(R.id.listView);
        productAdapter = new ProductAdapter(this, products);
        lv.setAdapter(productAdapter);
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.app_prefs);
        }
    }


    /*private void prepareUI()
    {
        productsFound = (TextView) findViewById(R.id.productsFound);
        //productCode = (TextView) findViewById(R.id.productCode);
        //productName = (TextView) findViewById(R.id.productName);
        ///////////////////

        //inputProduct = (EditText) findViewById(R.id.input_product);
        productCode = (TextView) findViewById(R.id.productCode);
        productName = (TextView) findViewById(R.id.productName);
    }*/

    /*private void showProduct(ProductView product)
    {
        productCode.setText(product.getCode());
        productName.setText(product.getName());
    }*/



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                        Toast.LENGTH_SHORT
                );
                toast.show();
            }
            else
            {
                int lengthBarcode = inputBarcode.length();
                if(lengthBarcode==8 || lengthBarcode == 13 || lengthBarcode == 14)
                {
                    formatTxt.setText("");
                    contentTxt.setText("CODIGO: " + inputBarcode);
                    searchProduct(inputBarcode);
                }
                else
                {
                    Toast toast = Toast.makeText(
                            this,
                            "RECUERDA QUE LA LONGUITD DEL CÓDIGO DEBE SER 8, 13 o 14 dígitos",
                            Toast.LENGTH_SHORT
                    );
                    toast.show();
                }
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);

        String scanContent = scanningResult.getContents();
        String scanFormat = scanningResult.getFormatName();

        formatTxt.setText("TIPO DE CODIGO: " + scanFormat);
        contentTxt.setText("CODIGO: " + scanContent);

        //if (scanningResult != null)
        //{
            //String url = "http://10.250.1.113:8008/app_dev.php/api/products/8410010000181";
            //String url = "http://192.168.100.112:8008/app_dev.php/api/products/8410010000181";
            //String url = "http://192.168.100.112:8008/app_dev.php/api/products/" + scanContent;
            //new LoadProductTask().execute(url);
        //}
        searchProduct(scanContent);
    }

    public void searchProduct(String barcode)
    {
        //String IPserver = "192.168.43.234";
        //String IPserver = "192.168.100.112";
        String IPserver = "192.168.1.5";
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
                    //showProduct(products.get(0));
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
