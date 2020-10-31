package org.evcode.queryfy.querydsl.core.converter;

import com.querydsl.core.types.Expression;

import java.util.Optional;
import java.util.function.Function;

public interface ExpressionOperationResolver extends Function<Expression<?>, Optional<? extends ExpressionOperation>> {
}
