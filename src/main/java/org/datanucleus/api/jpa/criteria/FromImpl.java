/**********************************************************************
Copyright (c) 2009 Andy Jefferson and others. All rights reserved.
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

Contributors:
   ...
**********************************************************************/
package org.datanucleus.api.jpa.criteria;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.persistence.criteria.CollectionJoin;
import javax.persistence.criteria.Fetch;
import javax.persistence.criteria.From;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.ListJoin;
import javax.persistence.criteria.MapJoin;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.SetJoin;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.ManagedType;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.Type;
import javax.persistence.metamodel.Attribute.PersistentAttributeType;

import org.datanucleus.api.jpa.metamodel.AttributeImpl;
import org.datanucleus.api.jpa.metamodel.CollectionAttributeImpl;
import org.datanucleus.api.jpa.metamodel.ListAttributeImpl;
import org.datanucleus.api.jpa.metamodel.MapAttributeImpl;
import org.datanucleus.api.jpa.metamodel.SetAttributeImpl;
import org.datanucleus.api.jpa.metamodel.SingularAttributeImpl;
import org.datanucleus.query.expression.ClassExpression;
import org.datanucleus.query.expression.DyadicExpression;
import org.datanucleus.query.expression.Expression;
import org.datanucleus.query.expression.JoinExpression;
import org.datanucleus.query.expression.PrimaryExpression;

/**
 * Implementation of JPA2 Criteria "From".
 * @param <Z> the parent type
 * @param <X> the type represented by this
 */
public class FromImpl<Z,X> extends PathImpl<Z,X> implements From<Z,X>
{
    private static final long serialVersionUID = -7138075290737454992L;
    protected java.util.Set<Join<X, ?>> joins;
    protected java.util.Set<Fetch<X, ?>> fetchJoins;
    protected Type<X> type;

    public FromImpl(CriteriaBuilderImpl cb, ManagedType<X> type)
    {
        super(cb, type.getJavaType());
        this.type = type;
    }

    public FromImpl(CriteriaBuilderImpl cb, PathImpl<?, Z> parent, AttributeImpl<? super Z, ?> type)
    {
        super(cb, parent, type, (Class<X>) type.getJavaType());
        this.type = (Type<X>)type.getType();
    }

    public Type<X> getAttributeType()
    {
        return type;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#getCorrelationParent()
     */
    public From<Z, X> getCorrelationParent()
    {
        // TODO Implement this
        throw new UnsupportedOperationException(
            "Not yet implemented. Provide a testcase that uses this and raise a JIRA issue attaching your testcase");
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#isCorrelated()
     */
    public boolean isCorrelated()
    {
        return getCorrelationParent() != null;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#getJoins()
     */
    public Set<Join<X, ?>> getJoins()
    {
        if (joins == null)
        {
            return Collections.EMPTY_SET;
        }
        return joins;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.CollectionAttribute, javax.persistence.criteria.JoinType)
     */
    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> attr, JoinType joinType)
    {
        CollectionJoin<X, Y> join = new CollectionJoinImpl<X, Y>(cb, this, (CollectionAttributeImpl) attr, joinType);
        if (joins == null)
        {
            joins = new HashSet<Join<X,?>>();
        }
        joins.add(join);
        return join;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.CollectionAttribute)
     */
    public <Y> CollectionJoin<X, Y> join(CollectionAttribute<? super X, Y> collection)
    {
        return join(collection, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.ListAttribute, javax.persistence.criteria.JoinType)
     */
    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> attr, JoinType joinType)
    {
        ListJoin<X, Y> join = new ListJoinImpl<X, Y>(cb, this, (ListAttributeImpl) attr, joinType);
        if (joins == null)
        {
            joins = new HashSet<Join<X,?>>();
        }
        joins.add(join);
        return join;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.ListAttribute)
     */
    public <Y> ListJoin<X, Y> join(ListAttribute<? super X, Y> list)
    {
        return join(list, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.MapAttribute, javax.persistence.criteria.JoinType)
     */
    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> attr, JoinType joinType)
    {
        MapJoin<X, K, V> join = new MapJoinImpl<X, K, V>(cb, this, (MapAttributeImpl) attr, joinType);
        if (joins == null)
        {
            joins = new HashSet<Join<X,?>>();
        }
        joins.add(join);
        return join;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.MapAttribute)
     */
    public <K, V> MapJoin<X, K, V> join(MapAttribute<? super X, K, V> map)
    {
        return join(map, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.SetAttribute, javax.persistence.criteria.JoinType)
     */
    public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> attr, JoinType joinType)
    {
        SetJoin<X, Y> join = new SetJoinImpl<X, Y>(cb, this, (SetAttributeImpl) attr, joinType);
        if (joins == null)
        {
            joins = new HashSet<Join<X,?>>();
        }
        joins.add(join);
        return join;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.SetAttribute)
     */
    public <Y> SetJoin<X, Y> join(SetAttribute<? super X, Y> set)
    {
        return join(set, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.SingularAttribute, javax.persistence.criteria.JoinType)
     */
    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> attr, JoinType joinType)
    {
        Join<X, Y> join = new JoinImpl<X, Y>(cb, this, (SingularAttributeImpl<X, Y>) attr, joinType);
        if (joins == null)
        {
            joins = new HashSet<Join<X, ?>>();
        }
        joins.add(join);
        return join;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(javax.persistence.metamodel.SingularAttribute)
     */
    public <Y> Join<X, Y> join(SingularAttribute<? super X, Y> singular)
    {
        return join(singular, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(java.lang.String, javax.persistence.criteria.JoinType)
     */
    @SuppressWarnings("hiding")
    public <X, Y> Join<X, Y> join(String attrName, JoinType joinType)
    {
        Attribute attr = getAttributeForAttributeName(attrName);
        if (attr instanceof SetAttributeImpl)
        {
            return join((SetAttribute)attr, joinType);
        }
        else if (attr instanceof ListAttribute)
        {
            return join((ListAttribute)attr, joinType);
        }
        else if (attr instanceof MapAttribute)
        {
            return join((MapAttribute)attr, joinType);
        }
        else if (attr instanceof CollectionAttribute)
        {
            return join((CollectionAttribute)attr, joinType);
        }
        else
        {
            return join((SingularAttribute)attr, joinType);
        }
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#join(java.lang.String)
     */
    @SuppressWarnings("hiding")
    public <X, Y> Join<X, Y> join(String attr)
    {
        return join(attr, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#joinCollection(java.lang.String, javax.persistence.criteria.JoinType)
     */
    @SuppressWarnings("hiding")
    public <X, Y> CollectionJoin<X, Y> joinCollection(String attrName, JoinType joinType)
    {
        Attribute attr = getAttributeForAttributeName(attrName);
        return join((CollectionAttribute)attr, joinType);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#joinCollection(java.lang.String)
     */
    @SuppressWarnings("hiding")
    public <X, Y> CollectionJoin<X, Y> joinCollection(String attrName)
    {
        return joinCollection(attrName, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#joinList(java.lang.String, javax.persistence.criteria.JoinType)
     */
    @SuppressWarnings("hiding")
    public <X, Y> ListJoin<X, Y> joinList(String attrName, JoinType joinType)
    {
        Attribute attr = getAttributeForAttributeName(attrName);
        return join((ListAttribute)attr, joinType);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#joinList(java.lang.String)
     */
    @SuppressWarnings("hiding")
    public <X, Y> ListJoin<X, Y> joinList(String attrName)
    {
        return joinList(attrName, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#joinMap(java.lang.String, javax.persistence.criteria.JoinType)
     */
    @SuppressWarnings("hiding")
    public <X, K, V> MapJoin<X, K, V> joinMap(String attrName, JoinType joinType)
    {
        Attribute attr = getAttributeForAttributeName(attrName);
        return join((MapAttribute)attr, joinType);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#joinMap(java.lang.String)
     */
    @SuppressWarnings("hiding")
    public <X, K, V> MapJoin<X, K, V> joinMap(String attrName)
    {
        return joinMap(attrName, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#joinSet(java.lang.String, javax.persistence.criteria.JoinType)
     */
    @SuppressWarnings("hiding")
    public <X, Y> SetJoin<X, Y> joinSet(String attrName, JoinType joinType)
    {
        Attribute attr = getAttributeForAttributeName(attrName);
        return join((SetAttribute)attr, joinType);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.From#joinSet(java.lang.String)
     */
    @SuppressWarnings("hiding")
    public <X, Y> SetJoin<X, Y> joinSet(String attr)
    {
        return joinSet(attr, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.FetchParent#getFetches()
     */
    public Set<Fetch<X, ?>> getFetches()
    {
        if (fetchJoins == null)
        {
            return Collections.EMPTY_SET;
        }
        return fetchJoins;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.FetchParent#fetch(javax.persistence.metamodel.PluralAttribute, javax.persistence.criteria.JoinType)
     */
    public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attr, JoinType joinType)
    {
        Fetch<X, Y> fetch = new FetchImpl<X, Y>(cb, this, (AttributeImpl<? super X, Y>) attr, joinType);
        if (fetchJoins == null)
        {
            fetchJoins = new HashSet<Fetch<X, ?>>();
        }
        fetchJoins.add(fetch);
        return fetch;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.FetchParent#fetch(javax.persistence.metamodel.PluralAttribute)
     */
    public <Y> Fetch<X, Y> fetch(PluralAttribute<? super X, ?, Y> attr)
    {
        return fetch(attr, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.FetchParent#fetch(javax.persistence.metamodel.SingularAttribute, javax.persistence.criteria.JoinType)
     */
    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attr, JoinType joinType)
    {
        Fetch<X, Y> fetch = new FetchImpl<X, Y>(cb, this, (SingularAttributeImpl<X, Y>) attr, joinType);
        if (fetchJoins == null)
        {
            fetchJoins = new HashSet<Fetch<X, ?>>();
        }
        fetchJoins.add(fetch);
        return fetch;
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.FetchParent#fetch(javax.persistence.metamodel.SingularAttribute)
     */
    public <Y> Fetch<X, Y> fetch(SingularAttribute<? super X, Y> attr)
    {
        return fetch(attr, JoinType.INNER);
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.FetchParent#fetch(java.lang.String, javax.persistence.criteria.JoinType)
     */
    @SuppressWarnings("hiding")
    public <X, Y> Fetch<X, Y> fetch(String attrName, JoinType joinType)
    {
        Attribute attr = getAttributeForAttributeName(attrName);
        if (attr instanceof SetAttributeImpl)
        {
            return fetch((SetAttribute)attr, joinType);
        }
        else if (attr instanceof ListAttribute)
        {
            return fetch((ListAttribute)attr, joinType);
        }
        else if (attr instanceof MapAttribute)
        {
            return fetch((MapAttribute)attr, joinType);
        }
        else if (attr instanceof CollectionAttribute)
        {
            return fetch((CollectionAttribute)attr, joinType);
        }
        else
        {
            return fetch((SingularAttribute)attr, joinType);
        }
    }

    /* (non-Javadoc)
     * @see javax.persistence.criteria.FetchParent#fetch(java.lang.String)
     */
    @SuppressWarnings("hiding")
    public <X, Y> Fetch<X, Y> fetch(String attrName)
    {
        return fetch(attrName, JoinType.INNER);
    }

    /**
     * Accessor for the underlying DataNucleus expression for this path.
     * Will be ClassExpression (FROM clause), or PrimaryExpression (otherwise).
     * @param from Whether this is for the from clause
     * @return The DataNucleus query expression
     */
    public org.datanucleus.query.expression.Expression getQueryExpression(boolean from)
    {
        if (from)
        {
            ClassExpression expr = new ClassExpression(getAlias());
            if (joins != null && !joins.isEmpty())
            {
                List tuples = new ArrayList();
                tuples.add(getAlias());

                Iterator<Join<X, ?>> iter = joins.iterator();
                Expression currentExpr = expr;
                while (iter.hasNext())
                {
                    Join<X, ?> join = iter.next();
                    org.datanucleus.query.expression.JoinExpression.JoinType jt = org.datanucleus.query.expression.JoinExpression.JoinType.JOIN_INNER;
                    if (join.getJoinType() == JoinType.LEFT)
                    {
                        jt = org.datanucleus.query.expression.JoinExpression.JoinType.JOIN_LEFT_OUTER;
                    }
                    else if (join.getJoinType() == JoinType.RIGHT)
                    {
                        jt = org.datanucleus.query.expression.JoinExpression.JoinType.JOIN_RIGHT_OUTER;
                    }

                    Attribute attr = join.getAttribute();
                    tuples.add(attr.getName());
                    PrimaryExpression primExpr = new PrimaryExpression(new ArrayList(tuples));
                    JoinExpression joinExpr = new JoinExpression(primExpr, join.getAlias(), jt);
                    if (currentExpr instanceof ClassExpression)
                    {
                        ((ClassExpression)currentExpr).setJoinExpression(joinExpr);
                    }
                    else
                    {
                        ((JoinExpression)currentExpr).setJoinExpression(joinExpr);
                    }
                    if (join.getOn() != null)
                    {
                        PredicateImpl onExpr = (PredicateImpl) join.getOn();
                        joinExpr.setOnExpression((DyadicExpression) onExpr.getQueryExpression());
                    }

                    currentExpr = joinExpr;
                }
            }

            if (fetchJoins != null && !fetchJoins.isEmpty())
            {
                List tuples = new ArrayList();
                tuples.add(getAlias());

                Iterator<Fetch<X, ?>> iter = fetchJoins.iterator();
                Expression currentExpr = expr;
                while (iter.hasNext())
                {
                    Fetch<X, ?> join = iter.next();
                    org.datanucleus.query.expression.JoinExpression.JoinType jt = org.datanucleus.query.expression.JoinExpression.JoinType.JOIN_INNER;
                    if (join.getJoinType() == JoinType.LEFT)
                    {
                        jt = org.datanucleus.query.expression.JoinExpression.JoinType.JOIN_LEFT_OUTER;
                    }
                    else if (join.getJoinType() == JoinType.RIGHT)
                    {
                        jt = org.datanucleus.query.expression.JoinExpression.JoinType.JOIN_RIGHT_OUTER;
                    }

                    Attribute attr = join.getAttribute();
                    tuples.add(attr.getName());
                    PrimaryExpression primExpr = new PrimaryExpression(new ArrayList(tuples));
                    JoinExpression joinExpr = new JoinExpression(primExpr, null, jt);
                    if (currentExpr instanceof ClassExpression)
                    {
                        ((ClassExpression)currentExpr).setJoinExpression(joinExpr);
                    }
                    else
                    {
                        ((JoinExpression)currentExpr).setJoinExpression(joinExpr);
                    }

                    currentExpr = joinExpr;
                }
            }
            return expr;
        }

        List tuples = new ArrayList();
        String alias = getAlias();
        if (alias != null)
        {
            // Specified with an alias, so just use the alias
            tuples.add(getAlias());
        }
        else
        {
            String fieldName = attribute.getMetadata().getName();
            if (parent != null)
            {
                // TODO What about multiple field usage "a.b.c" ?
                tuples.add(parent.getAlias());
                tuples.add(fieldName);
                return new PrimaryExpression(tuples);
            }
        }
        return new PrimaryExpression(tuples);
    }

    /**
     * Accessor for the underlying DataNucleus expression for this path.
     * @return The DataNucleus query expression
     */
    public org.datanucleus.query.expression.Expression getQueryExpression()
    {
        return getQueryExpression(false);
    }

    /**
     * Method to return a JPQL string form of the root expression.
     * @return The string form
     */
    public String toString()
    {
        return toString(false);
    }

    /**
     * Method to return a JPQL string form of the root expression.
     * @param from Whether this is for the FROM clause
     * @return The string form
     */
    public String toString(boolean from)
    {
        if (from)
        {
            // "mydomain.MyClass {alias} JOIN ..."
            StringBuilder str = new StringBuilder();
            str.append(getJavaType().getName());
            if (getAlias() != null)
            {
                str.append(" ").append(getAlias());
            }

            if (joins != null)
            {
                str.append(" ");
                Iterator<Join<X, ?>> iter = joins.iterator();
                StringBuilder joinAttrName = new StringBuilder(getAlias());
                while (iter.hasNext())
                {
                    Join<X, ?> join = iter.next();
                    JoinType type = join.getJoinType();
                    if (type == JoinType.INNER)
                    {
                        str.append("JOIN ");
                    }
                    else if (type == JoinType.LEFT)
                    {
                        str.append("LEFT JOIN ");
                    }
                    else if (type == JoinType.RIGHT)
                    {
                        str.append("RIGHT JOIN ");
                    }

                    Attribute<? super X, ?> attr = join.getAttribute();
                    joinAttrName.append('.').append(attr.getName());
                    str.append(joinAttrName.toString()).append(" ");

                    if (join.getAlias() != null)
                    {
                        str.append(join.getAlias());
                    }

                    Predicate onPred = join.getOn();
                    if (onPred != null)
                    {
                        str.append(" ");
                        str.append("ON ");
                        str.append(onPred.toString());
                    }
                }
            }

            if (fetchJoins != null)
            {
                str.append(" ");
                Iterator<Fetch<X, ?>> iter = fetchJoins.iterator();
                StringBuilder joinAttrName = new StringBuilder(getAlias());
                while (iter.hasNext())
                {
                    Fetch<X, ?> join = iter.next();
                    JoinType type = join.getJoinType();
                    if (type == JoinType.INNER)
                    {
                        str.append("JOIN FETCH ");
                    }
                    else if (type == JoinType.LEFT)
                    {
                        str.append("LEFT JOIN FETCH ");
                    }
                    else if (type == JoinType.RIGHT)
                    {
                        str.append("RIGHT JOIN FETCH ");
                    }

                    Attribute<? super X, ?> attr = join.getAttribute();
                    joinAttrName.append('.').append(attr.getName());
                    str.append(joinAttrName.toString()).append(" ");
                }
            }

            return str.toString();
        }
        else if (getAlias() != null)
        {
            // "{alias}"
            return getAlias();
        }
        else
        {
            // no alias
            return "(unaliased type=" + getJavaType().getName() + ")";
        }
    }

    protected Attribute getAttributeForAttributeName(String attrName)
    {
        if (attrName == null)
        {
            throw new IllegalArgumentException("Cannot join to null attribute");
        }
        StringTokenizer tokeniser = new StringTokenizer(attrName, ".");
        if (!tokeniser.hasMoreTokens())
        {
            throw new IllegalArgumentException("Cannot join to null attribute");
        }

        String token = tokeniser.nextToken();
        ManagedType currentType = (ManagedType)this.type;
        if (!token.equalsIgnoreCase(getAlias()))
        {
            // First token is not the alias of this type, so reset the tokeniser
            tokeniser = new StringTokenizer(attrName, ".");
        }

        AttributeImpl currentAttr = null;
        while (tokeniser.hasMoreTokens())
        {
            token = tokeniser.nextToken();
            currentAttr = (AttributeImpl)currentType.getAttribute(token);
            if (currentAttr == null)
            {
                throw new IllegalArgumentException("Unable to access attribute " + token + " of " + currentType + " for join");
            }

            if (tokeniser.hasMoreTokens())
            {
                if (currentAttr.getPersistentAttributeType() == PersistentAttributeType.BASIC)
                {
                    throw new IllegalArgumentException("Cannot resolve attribute " + attrName + " since " +
                        token + " is not a relation field and the attribute name goes beyond it!");
                }
                currentType = (ManagedType)currentAttr.getType();
            }
        }

        return currentAttr;
    }
}