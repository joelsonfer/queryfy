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
package org.evcode.queryfy.querydsl.jpa;

import com.querydsl.core.types.EntityPath;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import org.evcode.queryfy.core.parser.ParserConfig;
import org.evcode.queryfy.querydsl.core.QueryDslContext;
import org.evcode.queryfy.querydsl.core.QueryDslEvaluationResult;
import org.evcode.queryfy.querydsl.core.QueryDslEvaluator;
import org.evcode.queryfy.querydsl.core.converter.ExpressionOperationResolver;
import org.evcode.queryfy.querydsl.core.converter.ExpressionOperationResolverImpl;

import javax.persistence.EntityManager;
import java.util.List;

public class JPAQueryDslParser {

    private final EntityManager em;
    private final ExpressionOperationResolver expressionOperationResolver;

    public JPAQueryDslParser(EntityManager em) {
        this(em, new ExpressionOperationResolverImpl());
    }

    public JPAQueryDslParser(EntityManager em, ExpressionOperationResolver expressionOperationResolver) {
        this.em = em;
        this.expressionOperationResolver = expressionOperationResolver;
    }

    public QueryDslEvaluationResult parse(String expression, QueryDslContext context) {
        return parse(expression, context, ParserConfig.DEFAULT);
    }

    public QueryDslEvaluationResult parse(String expression, QueryDslContext context, ParserConfig config) {
        QueryDslEvaluator evaluator = new QueryDslEvaluator(expressionOperationResolver);
        QueryDslEvaluationResult eval = evaluator.evaluate(expression, context, config);
        return eval;
    }

    public JPAEvaluatedQuery parseAndFind(String expression, QueryDslContext context) {
        return parseAndFind(expression, context, ParserConfig.DEFAULT);
    }

    public JPAEvaluatedQuery parseAndFind(String expression, QueryDslContext context, ParserConfig config) {
        QueryDslEvaluationResult evaluationResult = parse(expression, context, config);
        JPAEvaluatedQuery jpaQuery = new JPAEvaluatedQuery(em, evaluationResult, context.getEntityPath());
        return apply(jpaQuery, context, evaluationResult);
    }

    public <T extends JPAQuery> T parseAndApply(T query, String expression, QueryDslContext context) {
        return parseAndApply(query, expression, context, ParserConfig.DEFAULT);
    }

    public <T extends JPAQuery> T parseAndApply(T query, String expression, QueryDslContext context, ParserConfig config) {
        QueryDslEvaluationResult evaluationResult = parse(expression, context, config);
        return apply(query, context, evaluationResult);
    }

    public <T extends JPAQuery> T apply(T query, QueryDslContext context, QueryDslEvaluationResult evaluationResult) {
        if (context.getEntityPath() != null) {
            query.from(context.getEntityPath());
        }

        if (evaluationResult.getPredicate() != null) {
            query.where(evaluationResult.getPredicate());
        }

        if (evaluationResult.getQueryModifiers() != null) {
            query.restrict(evaluationResult.getQueryModifiers());
        }

        if (evaluationResult.getOrderSpecifiers() != null && !evaluationResult.getOrderSpecifiers().isEmpty()) {
            query.orderBy(evaluationResult.getOrderSpecifiers().toArray(new OrderSpecifier[0]));
        }

        return query;
    }

    public static class JPAEvaluatedQuery<RT> extends JPAQuery<RT> {
        private QueryDslEvaluationResult evaluationResult;
        private EntityPath entityPath;

        public JPAEvaluatedQuery(EntityManager em, QueryDslEvaluationResult evaluationResult, EntityPath entityPath) {
            super(em);
            this.evaluationResult = evaluationResult;
            this.entityPath = entityPath;
        }

        public QueryDslEvaluationResult getEvaluationResult() {
            return evaluationResult;
        }

        public <RT> List<RT> listWithProjections() {
            if (evaluationResult.getProjection() != null) {
                return super.select(evaluationResult.getProjection()).fetch();
            }
            return super.select(entityPath).fetch();
        }
    }
}
