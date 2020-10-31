package org.evcode.queryfy.querydsl.core.converter;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.SimpleExpression;
import org.evcode.queryfy.core.operator.ComparisionOperatorType;
import org.evcode.queryfy.core.operator.ListOperatorType;
import org.evcode.queryfy.core.operator.SelectorOperatorType;
import org.evcode.queryfy.core.parser.ast.FilterNode;

import java.util.Collection;

public class SimpleExpressionOperation<T> implements ExpressionOperation {

    protected final SimpleExpression<T> property;

    public SimpleExpressionOperation(SimpleExpression<T> property) {
        this.property = property;
    }

    public Predicate apply(FilterNode node) {
        if (node.getOperator() == ComparisionOperatorType.EQUAL) {
            return this.eq(getNodeValue(property, node, 0));
        }
        if (node.getOperator() == ComparisionOperatorType.NOT_EQUAL) {
            return this.ne(getNodeValue(property, node, 0));
        }
        if (node.getOperator() == ListOperatorType.IN) {
            return this.in(getNodeValues(property, node));
        }
        if (node.getOperator() == ListOperatorType.NOT_IN) {
            return this.notIn(getNodeValues(property, node));
        }
        if (node.getOperator() == SelectorOperatorType.IS_NULL) {
            return this.isNull();
        }
        if (node.getOperator() == SelectorOperatorType.IS_NOT_NULL) {
            return this.isNotNull();
        }
        throw new UnsupportedOperationException("Operation not supported '" + node.getOperator().name() + "'");
    }

    public Predicate eq(T value) {
        return this.property.eq(value);
    }

    public Predicate ne(T value) {
        return this.property.ne(value);
    }

    public Predicate in(Collection<T> value) {
        return this.property.in(value);
    }

    public Predicate notIn(Collection<T> value) {
        return this.property.notIn(value);
    }

    public Predicate isNull() {
        return this.property.isNull();
    }

    public Predicate isNotNull() {
        return this.property.isNotNull();
    }


    public boolean isValid() {
        return true;
    }

    public Expression<T> getProperty() {
        return this.property;
    }
}
