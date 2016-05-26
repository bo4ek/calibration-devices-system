package com.softserve.edu.controller.calibrator.util;

import com.softserve.edu.entity.verification.Verification;

import java.util.Comparator;

public class BuildingAndFlatComparator implements Comparator<Verification> {

    @Override
    public int compare(Verification o1, Verification o2) {
        int building1 = Integer.parseInt(o1.getClientData().getClientAddress().getBuilding());
        int building2 = Integer.parseInt(o2.getClientData().getClientAddress().getBuilding());
        if (building1<building2){
            return -1;
        } else if (building1>building2) {
            return 1;
        } else if (building1==building2){
            return new FlatComparator().compare(o1, o2);
        }
        return 0;
    }
}

