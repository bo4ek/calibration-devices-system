package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.entity.verification.Verification;

import java.util.Comparator;

/**
 * Created by Vasyl on 07.10.2015.
 */
public class DistrictAndStreetComparator implements Comparator<Verification> {

    @Override
    public int compare(Verification o1, Verification o2) {
        String district1 = o1.getClientData().getClientAddress().getDistrict();
        String district2 = o2.getClientData().getClientAddress().getDistrict();
        String street1 = o1.getClientData().getClientAddress().getStreet();
        String street2 = o2.getClientData().getClientAddress().getStreet();
        String bulding1 = o1.getClientData().getClientAddress().getBuilding();
        String bulding2 = o2.getClientData().getClientAddress().getBuilding();
        String flat1 = o1.getClientData().getClientAddress().getFlat();
        String flat2 = o2.getClientData().getClientAddress().getFlat();
        if (district1.compareToIgnoreCase(district2)==0) {
             if (street1.compareToIgnoreCase(street2)==0){
                 if (bulding1.compareToIgnoreCase(bulding2)==0){
                     return flat1.compareToIgnoreCase(flat2);
                 } else return bulding1.compareToIgnoreCase(bulding2);
             } else return street1.compareToIgnoreCase(street2);
        } else return  district1.compareToIgnoreCase(district2);
    }
}
