package uni.UNI4950687.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

import uni.UNI4950687.R;
import uni.UNI4950687.models.MeItem;

public class MeItemAdapter extends ArrayAdapter<MeItem> {

    private int resourceId;

    public MeItemAdapter(@NonNull Context context, int resource, @NonNull List<MeItem> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        MeItem meItem = getItem(position);

        View view;
        ViewHolder viewHolder;

        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.title = view.findViewById(R.id.txtTitle);

            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.icon.setImageResource(meItem.iconId);
        viewHolder.title.setText(meItem.title);

        return view;
    }

    class ViewHolder {
        ImageView icon;
        TextView title;
    }
}
