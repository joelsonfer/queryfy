package org.evcode.queryfy.querydsl.core.converter;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.ComparableExpression;
import org.evcode.queryfy.core.operator.ComparisionOperatorType;
import org.evcode.queryfy.core.parser.ast.FilterNode;

public class ComparableExpressionOperation<T extends Comparable<?>> extends SimpleExpressionOperation<T> {

    public ComparableExpressionOperation(ComparableExpression<T> property) {
        super(property);
    }

    @Override
    public Predicate apply(FilterNode node) {
        if (node.getOperator() == ComparisionOperatorType.GREATER) {
            return this.gt(getNodeValue(property, node, 0));
        }
        if (node.getOperator() == ComparisionOperatorType.GREATER_EQUAL) {
            return this.goe(getNodeValue(property, node, 0));
        }
        if (node.getOperator() == ComparisionOperatorType.LOWER) {
            return this.lt(getNodeValue(property, node, 0));
        }
        if (node.getOperator() == ComparisionOperatorType.LOWER_EQUAL) {
            return this.loe(getNodeValue(property, node, 0));
        }
        return super.apply(node);
    }

    private Predicate loe(T value) {
        return getProperty().loe(value);
    }

    private Predicate lt(T value) {
        return getProperty().lt(value);
    }

    private Predicate goe(T value) {
        return getProperty().goe(value);
    }

    private Predicate gt(T value) {
        return getProperty().gt(value);
    }

    public ComparableExpression<T> getProperty() {
        return (ComparableExpression<T>) this.property;
    }
}
