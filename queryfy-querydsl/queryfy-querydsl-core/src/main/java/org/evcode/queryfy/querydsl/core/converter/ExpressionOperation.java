package org.evcode.queryfy.querydsl.core.converter;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EnumExpression;
import org.evcode.queryfy.core.parser.ast.FilterNode;

import java.util.LinkedList;
import java.util.List;

public interface ExpressionOperation {

    Predicate apply(FilterNode node);

    default <T> List<T> getNodeValues(Expression path, FilterNode node) {
        List args = new LinkedList();
        for (int i = 0; i < node.getArgs().size(); i++) {
            args.add(getNodeValue(path, node, i));
        }
        return args;
    }

    default <T> T getNodeValue(Expression<T> path, FilterNode node, Integer valueIndex) {
        if (path instanceof EnumExpression) {
            Object value = node.getArgs().get(valueIndex);
            for (Object item : path.getType().getEnumConstants()) {
                Enum enumValue = (Enum) item;
                if (value instanceof String && enumValue.name().equalsIgnoreCase(value.toString())) {
                    return (T) item;
                } else if (value instanceof Number && ((Number) value).intValue() == enumValue.ordinal()) {
                    return (T) item;
                }
            }
        }
        return (T) node.getArgs().get(valueIndex);
    }
}
