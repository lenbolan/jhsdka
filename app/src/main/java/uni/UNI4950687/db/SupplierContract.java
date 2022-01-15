package uni.UNI4950687.db;

import android.provider.BaseColumns;

public final class SupplierContract {

    private SupplierContract() {

    }

    public static class SupplierEntry implements BaseColumns {
        public static final String TABLE_NAME = "Supplier";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_AREA = "Area";
        public static final String COLUMN_NAME_ADDRESS = "Address";
        public static final String COLUMN_NAME_TEL = "Tel";
        public static final String COLUMN_NAME_DELIVERY = "Delivery";
    }

}
