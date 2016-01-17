package com.softserve.edu.service.utils.filter;

import com.softserve.edu.entity.device.Device;
import com.softserve.edu.service.utils.filter.internal.Comparison;
import com.softserve.edu.service.utils.filter.internal.Condition;
import com.softserve.edu.service.utils.filter.internal.Type;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.Logger;

public class Filter implements Specification {

    private List<Condition> conditions;

    public List<Condition> getConditions() {
        return conditions;
    }

    public Filter(List<Condition> conditions) {
        this.conditions = conditions;
    }

    public Filter() {
        conditions = new ArrayList<>();
    }

    public Filter(Map<String, Object> searchKeys) {
        conditions = new ArrayList<>();
        for (Map.Entry<String, Object> entry : searchKeys.entrySet()) {
            if (entry.getValue() instanceof String) {
                this.addCondition(new Condition.Builder()
                        .setComparison(Comparison.like)
                        .setField(entry.getKey())
                        .setValue(entry.getValue())
                        .build());
            } else if (entry.getValue() instanceof List) {

                this.addConditionList(buildBetweenDatesPredicate(entry.getKey(), (List) entry.getValue()));
            } else if (entry.getValue() instanceof Enum) {
                new Condition.Builder()
                        .setComparison(Comparison.eq)
                        .setField(entry.getKey())
                        .setValue(entry.getValue().toString())
                        .build();
            } else {
                this.addCondition(new Condition.Builder()
                        .setComparison(Comparison.eq)
                        .setField(entry.getKey())
                        .setValue(entry.getValue())
                        .build());
            }
        }
    }

    public void addCondition(Condition condition) {
        this.conditions.add(condition);
    }

    public void addConditionList(List<Condition> conditions) {
        this.conditions.addAll(conditions);
    }

    public Filter createFilterFromSearchKeys(Map<String, Object> searchKeys) {
        Filter filter = new Filter();
        for (Map.Entry<String, Object> entry : searchKeys.entrySet()) {
            if (entry.getValue() instanceof String) {
                filter.addCondition(new Condition.Builder()
                        .setComparison(Comparison.like)
                        .setField(entry.getKey())
                        .setValue(entry.getValue())
                        .build());
            } else if (entry.getValue() instanceof Map) {
                filter.addCondition(new Condition.Builder()
                        .setComparison(Comparison.ge)
                        .setField(entry.getKey())
                        .setValue(((Map) entry.getValue()).get("startDate"))
                        .build());
                filter.addCondition(new Condition.Builder()
                        .setComparison(Comparison.le)
                        .setField(entry.getKey())
                        .setValue(((Map) entry.getValue()).get("endDay"))
                        .build());
            } else {
                filter.addCondition(new Condition.Builder()
                        .setComparison(Comparison.eq)
                        .setField(entry.getKey())
                        .setValue(entry.getValue())
                        .build());
            }
        }
        return filter;
    }

    @Override
    public Predicate toPredicate(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = buildPredicates(root, criteriaQuery, criteriaBuilder);
        return predicates.size() > 1
                ? criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]))
                : predicates.get(0);
    }

    private List<Predicate> buildPredicates(Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
//       return conditions.stream().map(this::buildPredicate).collect(toList());
        List<Predicate> predicates = new ArrayList<>();
        conditions.forEach(condition -> predicates.add(buildPredicate(condition, root, criteriaQuery, criteriaBuilder)));
        return predicates;
    }


    private static List<Condition> buildBetweenDatesPredicate(String name, List<Date> dateList) {
        List<Condition> conditions = new ArrayList<>();
        conditions.add(new Condition.Builder()
                .setType(Type.date)
                .setComparison(Comparison.ge)
                .setField(name)
                .setValue(dateList.stream().min(Date::compareTo).get())
                .build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.le)
                .setType(Type.date)
                .setField(name)
                .setValue(dateList.stream().max(Date::compareTo).get())
                .build());
        return conditions;
    }

    private static List<Condition> buildBetweenDatesPredicateFromStrings(String name, List<String> stringDateList) throws ParseException {
        List<Condition> conditions = new ArrayList<>();
        DateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        ArrayList<Date> dateList = new ArrayList<>();
        for (String stringDate : stringDateList) {
            dateList.add(df.parse(stringDate));
        }
        conditions.add(new Condition.Builder()
                .setType(Type.date)
                .setComparison(Comparison.ge)
                .setField(name)
                .setValue(dateList.stream().min(Date::compareTo).get())
                .build());
        conditions.add(new Condition.Builder()
                .setComparison(Comparison.le)
                .setType(Type.date)
                .setField(name)
                .setValue(dateList.stream().max(Date::compareTo).get())
                .build());
        return conditions;
    }


    public Predicate buildPredicate(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        switch (condition.comparison) {
            case eq:
                return buildEqualsPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            case gt:
                return buildGreaterThanPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            case ge:
                return buildGreaterEqualPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            case lt:
                return buildLessThanPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            case le:
                return buildLessEqualPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            case ne:
                return buildNotEqualPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            case isnull:
                return buildIsNullPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            case like:
                return buildLikePredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            case in:
                return buildInPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
            default:
                return buildEqualsPredicateToCriteria(condition, root, criteriaQuery, criteriaBuilder);
        }
    }

    private Predicate buildInPredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (condition.type == Type.collection) {
            Set<Device.DeviceType> deviceTypeList = new HashSet<>();
            deviceTypeList.add((Device.DeviceType) condition.value);
            return root.join(condition.field).in(deviceTypeList);
        } else {
            return null;
        }
    }

    private Predicate buildEqualsPredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (condition.type == Type.enumerated) {
            return criteriaBuilder.equal(root.get(condition.field).as(String.class), condition.value.toString());
        } else if (condition.type == Type.bool) {
            return criteriaBuilder.equal(root.get(condition.field), Boolean.parseBoolean(condition.value.toString()));

        } else if (condition.type == Type.devicetype) {
            Join join = root.join(condition.field);
            return criteriaBuilder.equal(join.get("value"), condition.value);
        } else return criteriaBuilder.equal(root.get(condition.field), condition.value);
    }

    private Predicate buildLikePredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (condition.type == Type.user) {
            Join join = root.join(condition.field);
            Predicate searchByName = criteriaBuilder.like(join.get("firstName"),
                    "%" + condition.value + "%");
            Predicate searchByLastName = criteriaBuilder.like(join.get("lastName"),
                    "%" + condition.value + "%");
            Predicate searchByMiddleName = criteriaBuilder.like(join.get("middleName"),
                    "%" + condition.value + "%");
            Predicate searchPredicateByEmployeeName = criteriaBuilder.or(searchByName, searchByMiddleName,
                    searchByLastName);
            return searchByLastName;
        } else if (condition.type == Type.clientdata) {
            Predicate searchByClientFirstName = criteriaBuilder.like(root.get(condition.field).get("firstName"), "%" + condition.value + "%");
            Predicate searchByClientLastName = criteriaBuilder.like(root.get(condition.field).get("lastName"), "%" + condition.value + "%");
            Predicate searchByClientMiddleName = criteriaBuilder.like(root.get(condition.field).get("middleName"), "%" + condition.value + "%");
            Predicate searchPredicateByClientFullName = criteriaBuilder.or(searchByClientFirstName, searchByClientLastName, searchByClientMiddleName);
            return searchByClientLastName;
        }
        return criteriaBuilder.like(root.get(condition.field), "%" + condition.value + "%");
    }

    private Predicate buildGreaterThanPredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (condition.type.equals(Type.date)) {
            return criteriaBuilder.greaterThan(root.<Date>get(condition.field), (Date) condition.value);
        } else throw new RuntimeException();
    }

    private Predicate buildLessThanPredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (condition.type.equals(Type.date)) {
            return criteriaBuilder.lessThan(root.<Date>get(condition.field), (Date) condition.value);
        } else throw new RuntimeException();
    }

    private Predicate buildGreaterEqualPredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (condition.type.equals(Type.date)) {
            return criteriaBuilder.greaterThanOrEqualTo(root.<Date>get(condition.field), (Date) condition.value);
        } else throw new RuntimeException();
    }

    private Predicate buildLessEqualPredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        if (condition.type.equals(Type.date)) {
            return criteriaBuilder.lessThanOrEqualTo(root.<Date>get(condition.field), (Date) condition.value);
        } else throw new RuntimeException();
    }

    private Predicate buildNotEqualPredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.notEqual(root.get(condition.field), condition.value);
    }

    private Predicate buildIsNullPredicateToCriteria(Condition condition, Root root, CriteriaQuery criteriaQuery, CriteriaBuilder criteriaBuilder) {
        return criteriaBuilder.isNull(root.get(condition.field));
    }

    public static class FilterBuilder {
        private final Logger logger = Logger.getLogger(Filter.class.getSimpleName());

        private List<Condition> conditions;

        public FilterBuilder() {
            conditions = new ArrayList<>();
        }

        public FilterBuilder setSearchList(List<Map<String, Object>> searchList) {
            for (Map<String, Object> entry : searchList) {
                Type selectedType = Type.valueOf(entry.get("type").toString().toLowerCase());
                if (selectedType == Type.string || selectedType == Type.clientdata || selectedType == Type.user) {
                    this.conditions.add(new Condition.Builder()
                            .setComparison(Comparison.like)
                            .setField(entry.get("key").toString())
                            .setValue(entry.get("value"))
                            .setType(selectedType)
                            .build());
                } else if (entry.get("value") instanceof List) {
                    try {
                        this.conditions.addAll(buildBetweenDatesPredicateFromStrings(entry.get("key").toString(), (List) entry.get("value")));
                    } catch (ParseException e) {
                        logger.error("map error ", e);
                    }
                } else {
                    this.conditions.add(new Condition.Builder()
                            .setComparison(Comparison.eq)
                            .setField(entry.get("key").toString())
                            .setValue(entry.get("value"))
                            .setType(selectedType)
                            .build());
                }
            }
            return this;
        }

        public FilterBuilder setSearchMap(Map<String, Object> searchKeys) {
            for (Map.Entry<String, Object> entry : searchKeys.entrySet()) {
                if (entry.getValue() instanceof String) {
                    this.conditions.add(new Condition.Builder()
                            .setComparison(Comparison.like)
                            .setField(entry.getKey())
                            .setValue(entry.getValue())
                            .build());
                } else if (entry.getValue() instanceof List) {
                    this.conditions.addAll(buildBetweenDatesPredicate(entry.getKey(), (List) entry.getValue()));
                } else {
                    this.conditions.add(new Condition.Builder()
                            .setComparison(Comparison.eq)
                            .setField(entry.getKey())
                            .setValue(entry.getValue())
                            .build());
                }
            }
            return this;
        }

        public Filter build() {
            return new Filter(conditions);
        }
    }
}
