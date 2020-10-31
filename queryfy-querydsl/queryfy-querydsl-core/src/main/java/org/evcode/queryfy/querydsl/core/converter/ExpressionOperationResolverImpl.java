package org.evcode.queryfy.querydsl.core.converter;

import com.querydsl.core.types.Expression;
import com.querydsl.core.types.dsl.*;

import java.util.Optional;

public class ExpressionOperationResolverImpl implements ExpressionOperationResolver {

    @Override
    public Optional<? extends ExpressionOperation> apply(Expression<?> path) {
        if (path instanceof BooleanExpression) {
            return Optional.of(new BooleanExpressionOperation((BooleanExpression) path));
        }
        if (path instanceof StringExpression) {
            return Optional.of(new StringExpressionOperation((StringExpression) path));
        }
        if (path instanceof NumberExpression) {
            return Optional.of(new NumberExpressionOperation((NumberExpression) path));
        }
        if (path instanceof ComparableExpression) {
            return Optional.of(new ComparableExpressionOperation((ComparableExpression) path));
        }
        if (path instanceof SimpleExpression) {
            return Optional.of(new SimpleExpressionOperation((SimpleExpression) path));
        }
        return Optional.empty();
    }
}
