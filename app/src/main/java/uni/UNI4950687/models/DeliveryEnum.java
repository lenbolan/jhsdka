package uni.UNI4950687.models;

import androidx.annotation.NonNull;

public enum DeliveryEnum {

    Airlift,
    Train,
    Truck,
    Shipping;

    @NonNull
    @Override
    public String toString() {
        switch (this) {
            case Airlift:
                return "空运";
            case Train:
                return "火车";
            case Truck:
                return "货车";
            case Shipping:
                return "轮船";
            default:
                return "";
        }
    }
}
