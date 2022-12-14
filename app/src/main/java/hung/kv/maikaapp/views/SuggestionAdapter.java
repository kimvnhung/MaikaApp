package hung.kv.maikaapp.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import hung.kv.maikaapp.R;

public class SuggestionAdapter extends BaseAdapter {
    Context context;
    ArrayList<String> datas;

    public SuggestionAdapter(Context context, ArrayList<String> datas) {
        this.context = context;
        this.datas = datas;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int i) {
        return datas.get(i);
    }

    @Override
    public long getItemId(int i) {
        return datas.indexOf(getItem(i));
    }

    private class ViewHolder{
        TextView suggestion;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = null;

        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView==null){
            convertView = layoutInflater.inflate(R.layout.custom_listview_item,null);

            holder = new ViewHolder();

            holder.suggestion = (TextView) convertView.findViewById(R.id.item_tv);

            String row_pos = datas.get(position);

            holder.suggestion.setText(row_pos);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        return convertView;
    }
}
