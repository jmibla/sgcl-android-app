package project_sgcl.sgclapp;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

/**
 * Created by maria on 15/5/15.
 */
public class ProductAdapter extends BaseAdapter
{
    private Context context;
    private List<Product> items;

    public ProductAdapter(Context context, List<Product> items)
    {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public List<Product> getItems() {
        return items;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View rowView = convertView;

        if(convertView == null)
        {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE
            );
            rowView = inflater.inflate(R.layout.activity_product, parent, false);
        }

        TextView productCode = (TextView) rowView.findViewById(R.id.productCode);
        TextView productName = (TextView) rowView.findViewById(R.id.productName);
        TextView productNConsumerUnit = (TextView) rowView.findViewById(
                R.id.productNumberConsumerUnit
        );

        Product item = this.items.get(position);
        productCode.setText(item.getCode());
        productName.setText(item.getName());
        productNConsumerUnit.setText(
                "Agrupación: " + item.getNumberConsumerUnit() + " Unidades de Consumo"
        );

        return rowView;
    }

}
