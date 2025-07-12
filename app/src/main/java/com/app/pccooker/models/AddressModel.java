package com.app.pccooker.models;

import android.os.Parcel;
import android.os.Parcelable;

public class AddressModel implements Parcelable {
    private String id;
    private String name;
    private String mobile;
    private String address;
    private String landmark;
    private String pincode;
    private String state;
    private String city;
    private String label;
    private boolean isDefault;

    public AddressModel() {} // Required by Firebase

    public AddressModel(String id, String name, String mobile, String address, String landmark, String pincode, String state, String city, String label, boolean isDefault) {
        this.id = id;
        this.name = name;
        this.mobile = mobile;
        this.address = address;
        this.landmark = landmark;
        this.pincode = pincode;
        this.state = state;
        this.city = city;
        this.label = label;
        this.isDefault = isDefault; // add in both constructor and Firestore mapping

    }

    protected AddressModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        mobile = in.readString();
        address = in.readString();
        landmark = in.readString();
        pincode = in.readString();
        state = in.readString();
        city = in.readString();
        label = in.readString();

    }



    public boolean isDefault() { return isDefault; }
    public void setDefault(boolean isDefault) { this.isDefault = isDefault; }

    public static final Creator<AddressModel> CREATOR = new Creator<AddressModel>() {
        @Override
        public AddressModel createFromParcel(Parcel in) {
            return new AddressModel(in);
        }

        @Override
        public AddressModel[] newArray(int size) {
            return new AddressModel[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(id);
        parcel.writeString(name);
        parcel.writeString(mobile);
        parcel.writeString(address);
        parcel.writeString(landmark);
        parcel.writeString(pincode);
        parcel.writeString(state);
        parcel.writeString(city);
        parcel.writeString(label);
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getMobile() { return mobile; }
    public String getAddress() { return address; }

    public String getLandmark() { return landmark; }
    public void setLandmark(String landmark) { this.landmark = landmark; }
    public String getPincode() { return pincode; }
    public String getState() { return state; }
    public String getCity() { return city; }
    public String getLabel() { return label; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMobile(String mobile) { this.mobile = mobile; }
    public void setAddress(String address) { this.address = address; }
    public void setPincode(String pincode) { this.pincode = pincode; }
    public void setState(String state) { this.state = state; }
    public void setCity(String city) { this.city = city; }
    public void setLabel(String label) { this.label = label; }
}