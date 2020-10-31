package org.evcode.queryfy.querydsl.core.converter;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.StringExpression;
import org.evcode.queryfy.core.operator.SelectorOperatorType;
import org.evcode.queryfy.core.operator.StringOperatorType;
import org.evcode.queryfy.core.parser.ast.FilterNode;

public class StringExpressionOperation extends ComparableExpressionOperation<String> {

    public StringExpressionOperation(StringExpression property) {
        super(property);
    }

    public Predicate apply(FilterNode node) {
        if (node.getOperator() == StringOperatorType.LIKE) {
            return this.like(getNodeValue(this.getProperty(), node, 0));
        }
        if (node.getOperator() == StringOperatorType.NOT_LIKE) {
            return this.notLike(getNodeValue(getProperty(), node, 0));
        }
        if (node.getOperator() == SelectorOperatorType.IS_EMPTY) {
            return this.isEmpty();
        }
        if (node.getOperator() == SelectorOperatorType.IS_NOT_EMPTY) {
            return this.isNotEmpty();
        }
        return super.apply(node);
    }

    public Predicate isNotEmpty() {
        return getProperty().isNotEmpty();
    }

    public Predicate isEmpty() {
        return getProperty().isEmpty();
    }

    public Predicate like(String value) {
        return getProperty().like(value);
    }

    public Predicate notLike(String value) {
        return getProperty().notLike(value);
    }

    public StringExpression getProperty() {
        return (StringExpression) this.property;
    }
}
