package org.projects.shoppinglist;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by tobias on 10-05-2016.
 */
public class Product implements Parcelable{

    private String name;
    private int quantity;

    //add getters and setters for the properties so that Firebase can use them; otherwise you're
    // going to get an error about serialization
    public String getName(){
        return name;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setName(String name){
        this.name = name;
    }

    public void setQuantity(int quantity){
        this.quantity = quantity;
    }

    public Product() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeInt(quantity);
    }

    //creator
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator(){
        public Product createFromParcel(Parcel in){
            return new Product(in);
        }

        @Override
        public Object[] newArray(int size) {
            return new Object[0];
        }
    };

    public Product(String name, int quantity){
        this.name = name;
        this.quantity = quantity;
    }

    //de-parcel object
    public Product(Parcel in) {
        name = in.readString();
        quantity = in.readInt();
    }

    @Override
    public String toString() {
        if (quantity == 0){
            return " " + name;
        } else {
            return quantity + " " + name;
        }

    }
}
