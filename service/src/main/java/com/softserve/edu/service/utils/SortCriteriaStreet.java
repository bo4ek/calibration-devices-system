package com.softserve.edu.service.utils;

import com.softserve.edu.entity.catalogue.District;
import com.softserve.edu.entity.catalogue.Locality;
import com.softserve.edu.entity.catalogue.Region;
import com.softserve.edu.entity.catalogue.Street;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Root;


public enum SortCriteriaStreet {
    CITY() {
        public Order getSortOrder(Root<Street> root, Join<Street, Locality> localityJoin,
                                  Join<Locality, District> districtJoin, Join<District, Region> regionJoin,
                                  CriteriaBuilder cb, String sortOrder) {

            if (sortOrder.equalsIgnoreCase("asc")) {
                return cb.asc(localityJoin.get("designation"));
            } else {
                return cb.desc(localityJoin.get("designation"));
            }
        }
    },
    DISTRICT() {
        public Order getSortOrder(Root<Street> root, Join<Street, Locality> localityJoin,
                                  Join<Locality, District> districtJoin, Join<District, Region> regionJoin,
                                  CriteriaBuilder cb, String sortOrder) {

            if (sortOrder.equalsIgnoreCase("asc")) {
                return cb.asc(districtJoin.get("designation"));
            } else {
                return cb.desc(districtJoin.get("designation"));
            }
        }
    },

    REGION() {
        public Order getSortOrder(Root<Street> root, Join<Street, Locality> localityJoin,
                                  Join<Locality, District> districtJoin, Join<District, Region> regionJoin,
                                  CriteriaBuilder cb, String sortOrder) {

            if (sortOrder.equalsIgnoreCase("asc")) {
                return cb.asc(regionJoin.get("designation"));
            } else {
                return cb.desc(regionJoin.get("designation"));
            }
        }
    },
    STREETNAME() {
        public Order getSortOrder(Root<Street> root, Join<Street, Locality> localityJoin,
                                  Join<Locality, District> districtJoin, Join<District, Region> regionJoin,
                                  CriteriaBuilder cb, String sortOrder){
            if (sortOrder.equalsIgnoreCase("asc")) {
                return cb.asc(root.get("designation"));
            } else {
                return cb.desc(root.get("designation"));
            }
        }
    };

    public Order getSortOrder(Root<Street> root, Join<Street, Locality> localityJoin,
                              Join<Locality, District> districtJoin, Join<District, Region> regionJoin,
                              CriteriaBuilder cb, String sortOrder) {
        return null;
    }
}