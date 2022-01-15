package uni.UNI4950687.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import uni.UNI4950687.R;
import uni.UNI4950687.models.SupplierModel;
import uni.UNI4950687.interfaces.IMyListListener;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private static final String TAG = "ItemAdapter";

    private ArrayList<SupplierModel> mDataSet;

    private IMyListListener onClickDelete;
    private IMyListListener onClickEdit;

    public void setOnClickDelete(IMyListListener listener) {
        onClickDelete = listener;
    }

    public void setOnClickEdit(IMyListListener listener) {
        onClickEdit = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_supplier, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d(TAG, "Element " + position + " set.");
        SupplierModel model = mDataSet.get(position);
        holder.setData(model);
        if (onClickDelete != null) {
            holder.imageDelete.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickDelete.onClick(holder.imageDelete, holder.getAdapterPosition());
                }
            });
        }
        if (onClickEdit != null) {
            holder.imageEdit.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickEdit.onClick(holder.imageEdit, holder.getAdapterPosition());
                }
            });
        }
    }

    public ItemAdapter(ArrayList<SupplierModel> dataSet) {
        mDataSet = dataSet;
    }

    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView txtID;
        private final TextView txtName;
        private final TextView txtAddress;
        private final TextView txtArea;
        private final TextView txtPhone;
        private final TextView txtDelivery;
        private final ImageView imageDelete;
        private final ImageView imageEdit;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtID = itemView.findViewById(R.id.txtID);
            txtName = itemView.findViewById(R.id.txtSearch);
            txtAddress = itemView.findViewById(R.id.txtAddress);
            txtArea = itemView.findViewById(R.id.txtArea);
            txtPhone = itemView.findViewById(R.id.txtPhone);
            txtDelivery = itemView.findViewById(R.id.txtDelivery);
            imageDelete = itemView.findViewById(R.id.imageDelete);
            imageEdit = itemView.findViewById(R.id.imageEdit);
        }

        public void setData(SupplierModel model) {
            txtID.setText("ID: "+model.id);
            txtName.setText(model.name);
            txtAddress.setText(model.address);
            txtArea.setText(model.area);
            txtPhone.setText("Tel: "+model.tel);
            txtDelivery.setText("配送: "+model.delivery);
        }

        public ImageView imgDelete() {
            return imageDelete;
        }

        public ImageView imgEdit() {
            return imageEdit;
        }

    }

}
