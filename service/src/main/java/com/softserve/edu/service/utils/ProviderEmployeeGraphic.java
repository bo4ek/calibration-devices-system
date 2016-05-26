package com.softserve.edu.service.utils;

import java.util.List;

public class ProviderEmployeeGraphic {

    private String name;
    private  List<Double> data;
    private  List<String> listMonths;




    public ProviderEmployeeGraphic(double data) {

    }

    public ProviderEmployeeGraphic(String name, List<Double> data,List<String> listMonths) {
        this.data=data;
        this.name = name;
        this.listMonths=listMonths;
     }

    public List<Double> getData() {
        return data;
    }

    public void setData(List<Double> data) {
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getListMonths() {
        return listMonths;
    }

    public void setListMonths(List<String> listMonths) {
        this.listMonths = listMonths;
    }

    @Override
    public String toString() {
        return "ProviderEmployeeGraphic{" +
                "name='" + name + '\'' +
                ", data=" + data +
                ", listMonth=" + listMonths +
                '}';
    }
}
