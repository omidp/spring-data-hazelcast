/*
 * Copyright 2020 Hazelcast Inc.
 *
 * Licensed under the Hazelcast Community License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://hazelcast.com/hazelcast-community-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package org.springframework.data.hazelcast.repository.support;

import com.hazelcast.core.HazelcastInstance;
import org.springframework.data.hazelcast.repository.query.HazelcastPartTreeQuery;
import org.springframework.data.keyvalue.core.KeyValueOperations;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.repository.core.NamedQueries;
import org.springframework.data.repository.core.RepositoryMetadata;
import org.springframework.data.repository.query.QueryLookupStrategy;
import org.springframework.data.repository.query.QueryMethodEvaluationContextProvider;
import org.springframework.data.repository.query.RepositoryQuery;
import org.springframework.data.repository.query.parser.AbstractQueryCreator;
import org.springframework.util.Assert;

import java.lang.reflect.Method;

/**
 * <p>
 * Ensures {@link HazelcastPartTreeQuery} is used for query preparation rather than {@link KeyValuePartTreeQuery} or
 * other alternatives.
 * </P>
 *
 * @author Neil Stevenson
 */
public class HazelcastQueryLookupStrategy
        implements QueryLookupStrategy {

    private final QueryMethodEvaluationContextProvider evaluationContextProvider;
    private final KeyValueOperations keyValueOperations;
    private final Class<? extends AbstractQueryCreator<?, ?>> queryCreator;
    private final HazelcastInstance hazelcastInstance;

    /**
     * <p>
     * Required constructor, capturing arguments for use in {@link #resolveQuery}.
     * </P>
     * <p>
     * Assertions copied from {@link KayValueRepositoryFactory.KeyValueQUeryLookupStrategy} which this class essentially
     * duplicates.
     * </P>
     *
     * @param key                       Not used
     * @param evaluationContextProvider For evaluation of query expressions
     * @param keyValueOperations        Bean to use for Key/Value operations on Hazelcast repos
     * @param queryCreator              Likely to be {@link HazelcastQueryCreator}
     * @param hazelcastInstance         Instance of Hazelcast
     */
    public HazelcastQueryLookupStrategy(QueryLookupStrategy.Key key,
                                        QueryMethodEvaluationContextProvider evaluationContextProvider,
                                        KeyValueOperations keyValueOperations,
                                        Class<? extends AbstractQueryCreator<?, ?>> queryCreator,
                                        HazelcastInstance hazelcastInstance) {

        Assert.notNull(evaluationContextProvider, "EvaluationContextProvider must not be null!");
        Assert.notNull(keyValueOperations, "KeyValueOperations must not be null!");
        Assert.notNull(queryCreator, "Query creator type must not be null!");
        Assert.notNull(hazelcastInstance, "HazelcastInstance must not be null!");

        this.evaluationContextProvider = evaluationContextProvider;
        this.keyValueOperations = keyValueOperations;
        this.queryCreator = queryCreator;
        this.hazelcastInstance = hazelcastInstance;
    }

    /**
     * <p>
     * Use {@link HazelcastPartTreeQuery} for resolving queries against Hazelcast repositories.
     * </P>
     *
     * @param Method,             the query method
     * @param RepositoryMetadata, not used
     * @param ProjectionFactory,  not used
     * @param NamedQueries,       not used
     * @return A mechanism for querying Hazelcast repositories
     */
    public RepositoryQuery resolveQuery(Method method, RepositoryMetadata metadata, ProjectionFactory projectionFactory,
                                        NamedQueries namedQueries) {

        HazelcastQueryMethod queryMethod = new HazelcastQueryMethod(method, metadata, projectionFactory);

        if (queryMethod.hasAnnotatedQuery()) {
            return new StringBasedHazelcastRepositoryQuery(queryMethod, hazelcastInstance);
        }

        return new HazelcastPartTreeQuery(queryMethod, evaluationContextProvider, this.keyValueOperations, this.queryCreator);
    }

}
