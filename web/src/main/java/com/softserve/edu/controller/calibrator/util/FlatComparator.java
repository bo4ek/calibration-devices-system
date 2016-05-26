package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.entity.verification.Verification;

import java.util.Comparator;

public class FlatComparator implements Comparator<Verification> {

    @Override
    public int compare(Verification o1, Verification o2) {
        int flat1 = Integer.parseInt(o1.getClientData().getClientAddress().getFlat());
        int flate2 = Integer.parseInt(o2.getClientData().getClientAddress().getFlat());
        if (flat1<flate2){
            return -1;
        } else if (flat1>flate2) {
            return 1;
        } else return 0;
    }
}
