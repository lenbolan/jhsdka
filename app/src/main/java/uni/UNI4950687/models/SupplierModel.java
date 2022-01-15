package uni.UNI4950687.models;

import java.io.Serializable;

public class SupplierModel implements Serializable {

    public String id;
    public String name;
    public String address;
    public String area;
    public String tel;
    public String delivery;

    @Override
    public String toString() {
        return "SupplierModel{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", area='" + area + '\'' +
                ", tel='" + tel + '\'' +
                ", delivery='" + delivery + '\'' +
                '}';
    }
}
