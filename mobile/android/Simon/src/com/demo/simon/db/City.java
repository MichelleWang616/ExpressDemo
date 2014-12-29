
package com.demo.simon.db;

public class City {
    private String mName;
    private String mCode;
    private City mParentCity = null;

    public City(String name, String code, City parentCity) {
        mName = name;
        mCode = code;
        mParentCity = parentCity;
    }

    public String getName() {
        return mName;
    }

    public String getCode() {
        return mCode;
    }
}
