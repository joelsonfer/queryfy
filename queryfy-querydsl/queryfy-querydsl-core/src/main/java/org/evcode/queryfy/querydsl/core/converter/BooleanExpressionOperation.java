package org.evcode.queryfy.querydsl.core.converter;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import org.evcode.queryfy.core.operator.SelectorOperatorType;
import org.evcode.queryfy.core.parser.ast.FilterNode;

public class BooleanExpressionOperation extends ComparableExpressionOperation<Boolean> {

    public BooleanExpressionOperation(BooleanExpression property) {
        super(property);
    }

    public Predicate apply(FilterNode node) {
        if (node.getOperator() == SelectorOperatorType.IS_TRUE) {
            return isTrue();
        }
        if (node.getOperator() == SelectorOperatorType.IS_FALSE) {
            return isFalse();
        }
        return super.apply(node);
    }

    public BooleanExpression getProperty() {
        return (BooleanExpression) this.property;
    }

    public Predicate isTrue() {
        return this.getProperty().isTrue();
    }

    public Predicate isFalse() {
        return this.getProperty().isFalse();
    }
}
