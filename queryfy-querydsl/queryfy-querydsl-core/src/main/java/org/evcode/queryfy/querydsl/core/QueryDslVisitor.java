/*
 * Copyright 2017 EVCode
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.evcode.queryfy.querydsl.core;

import com.querydsl.core.QueryModifiers;
import com.querydsl.core.types.*;
import org.evcode.queryfy.core.Visitor;
import org.evcode.queryfy.core.operator.OrderOperatorType;
import org.evcode.queryfy.core.parser.ast.*;
import org.evcode.queryfy.querydsl.core.converter.ExpressionOperationResolver;

import java.util.*;

public class QueryDslVisitor implements Visitor<Predicate, QueryDslContext> {

    private final ExpressionOperationResolver resolver;

    public QueryDslVisitor(ExpressionOperationResolver resolver) {
        this.resolver = resolver;
    }

    @Override
    public Expression visit(ProjectionNode node, QueryDslContext context) {
        Map<Path, List<Expression>> expressions = new HashMap<>();
        for (String selector : node.getSelectors()) {
            Expression extractedPath = ExpressionUtils.extract(context.resolveProjectionPath(selector));

            if (extractedPath instanceof Path) {
                Path path = (Path) extractedPath;

                if (path.getMetadata().isRoot()) {
                    expressions.putIfAbsent(path, new ArrayList<>());
                    expressions.get(path).add(path);
                    continue;
                }

                Path parentPath = path.getMetadata().getParent();
                expressions.putIfAbsent(parentPath, new ArrayList<>());
                expressions.get(parentPath).add(path);
            }
        }

        List<Expression> projections = expressions.getOrDefault(context.getEntityPath(),
                new LinkedList<>());

        expressions.remove(context.getEntityPath());
        for (Map.Entry<Path, List<Expression>> rootPath : expressions.entrySet()) {
            projections.add(Projections.fields(rootPath.getKey().getType(),
                    rootPath.getValue().toArray(new Expression[0]))
                    .as(rootPath.getKey().getMetadata().getName()));
        }

        return Projections.fields(context.getEntityPath().getType(),
                projections.toArray(new Expression[0]));
    }

    @Override
    public Predicate visit(AndNode node, QueryDslContext context) {
        Predicate right = node.getRightOperation().accept(this, context);
        Predicate left = node.getLeftOperation().accept(this, context);
        return ExpressionUtils.and(left, right);
    }

    @Override
    public Predicate visit(OrNode node, QueryDslContext context) {
        Predicate right = node.getRightOperation().accept(this, context);
        Predicate left = node.getLeftOperation().accept(this, context);
        return ExpressionUtils.or(left, right);
    }

    @Override
    public Predicate visit(FilterNode node, QueryDslContext context) {
        Expression path = context.resolveQueryPath(node.getSelector());

        return resolver.apply(path)
                .map(op -> op.apply(node))
                .orElseThrow(() -> new UnsupportedOperationException("Operation not supported '" + node.getOperator().name() + "'"));
    }


    @Override
    public List<OrderSpecifier> visit(OrderNode node, QueryDslContext context) {
        List<OrderSpecifier> orders = new LinkedList<>();

        node.getOrderSpecifiers().forEach(p -> {
            Order order = p.getOperator() == OrderOperatorType.DESC ? Order.DESC : Order.ASC;
            orders.add(new OrderSpecifier(order, context.resolveProjectionPath(p.getSelector())));
        });

        return orders;
    }

    @Override
    public QueryModifiers visit(LimitNode node, QueryDslContext context) {
        return new QueryModifiers(node.getLimit(), node.getOffset());
    }
}
