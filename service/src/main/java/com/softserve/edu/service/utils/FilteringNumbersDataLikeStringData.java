package com.softserve.edu.service.utils;

import org.hibernate.jpa.criteria.CriteriaBuilderImpl;
import org.hibernate.jpa.criteria.ParameterRegistry;
import org.hibernate.jpa.criteria.Renderable;
import org.hibernate.jpa.criteria.compile.RenderingContext;
import org.hibernate.jpa.criteria.expression.function.BasicFunctionExpression;
import org.hibernate.jpa.criteria.expression.function.FunctionExpression;

import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Selection;
import java.io.Serializable;

/**
 *
 * @param <Y> type that will be casted to String
 *           this class adds new functionality for searching data in DB.
 *           Use it when you need to use a "like" method from CriteriaBuilder.
 *           Class will filter digital(Integer, Long..) data like it's String.
 */

public class FilteringNumbersDataLikeStringData<Y extends Number> extends BasicFunctionExpression<String> implements FunctionExpression<String>, Serializable {
    public static final String FCT_NAME = "str";

    @Transient
    private final Selection<Y> selection;

    public FilteringNumbersDataLikeStringData(CriteriaBuilder criteriaBuilder, Selection<Y> selection) {
        super((CriteriaBuilderImpl) criteriaBuilder, String.class, FCT_NAME);
        this.selection = selection;
    }

    @Override
    public void registerParameters(ParameterRegistry registry) {
        Helper.possibleParameter(selection, registry);
    }

    @Override
    public String render(RenderingContext renderingContext) {
        return FCT_NAME + '(' + ((Renderable) selection).render(renderingContext) + ')';
    }
}
