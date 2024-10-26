package com.digitinarytask.customer.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Abstract repository class to handle JPA criteria queries with specifications.
 */
@RequiredArgsConstructor
@Slf4j
public class AbstractSpecificationRepository<T> {
    protected final EntityManager entityManager;

    /**
     * Finds entities based on the search criteria.
     */
    @Transactional(readOnly = true)
    protected Page<T> findBySpecification(Specification<T> specification, PageRequest pageRequest, Class<T> entityClass) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> query = cb.createQuery(entityClass);
            Root<T> root = query.from(entityClass);

            applySpecification(specification, cb, query, root);
            applySorting(cb, query, root, pageRequest);

            TypedQuery<T> typedQuery = createPginiateTypedQuery(query, pageRequest);
            Long total = executeCountQuery(specification, entityClass);

            List<T> results = typedQuery.getResultList();
            return new PageImpl<>(results, pageRequest, total);
        } catch (NoResultException e) {
            return new PageImpl<>(Collections.emptyList(), pageRequest, 0);
        } catch (PersistenceException e) {
            log.error("Error executing findBySpecification query for {}: {}", entityClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Error executing findBySpecification query", e);
        }
    }

    /**
     * Finds the first entity based on the search criteria.
     */
    @Transactional(readOnly = true)
    protected Optional<T> findFirst(Specification<T> specification, Class<T> entityClass) {
        try {
            CriteriaBuilder cb = entityManager.getCriteriaBuilder();
            CriteriaQuery<T> query = cb.createQuery(entityClass);
            Root<T> root = query.from(entityClass);

            applySpecification(specification, cb, query, root);
            return Optional.ofNullable(entityManager.createQuery(query).setMaxResults(1).getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (PersistenceException e) {
            log.error("Error executing findFirst query for {}: {}", entityClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Error executing findFirst query", e);
        }
    }

    /**
     * Checks if an entity exists based on the search criteria.
     */
    @Transactional(readOnly = true)
    protected boolean existsBySpecification(Specification<T> specification, Class<T> entityClass) {
        try {
            CriteriaQuery<Long> query = createExistsQuery(specification, entityClass);
            return entityManager.createQuery(query).getSingleResult() > 0;
        } catch (NoResultException e) {
            return false;
        } catch (PersistenceException e) {
            log.error("Error executing existsBySpecification query for {}: {}", entityClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("Error executing existsBySpecification query", e);
        }
    }


    /**
     * Gets the path for sorting based on the property name.
     */
    protected Path<?> getSortPath(Root<T> root, String property) {
        if (!StringUtils.hasText(property)) {
            throw new IllegalArgumentException("Sort property cannot be empty");
        }

        if (property.contains(".")) {
            return getNestedPath(root, property.split("\\."));
        }
        return root.get(property);
    }

    /**
     * Finds nested path based on the property name.
     */
    private Path<?> getNestedPath(Root<T> root, String[] pathParts) {
        Path<?> path = root.get(pathParts[0]);
        for (int i = 1; i < pathParts.length; i++) {
            path = path.get(pathParts[i]);
        }
        return path;
    }


    /**
     * Applies the specification to the query.
     */
    private void applySpecification(Specification<T> specification, CriteriaBuilder cb, CriteriaQuery<T> query, Root<T> root) {
        if(specification != null){
            Predicate predicate = specification.toPredicate(root,query, cb);
            if(predicate != null){
                query.where(predicate);
            }
        }
    }

    /**
     * Applies sorting to the query.
     */
    private void applySorting(CriteriaBuilder cb, CriteriaQuery<T> query, Root<T> root, PageRequest pageRequest){
        if(pageRequest.getSort().isSorted()){
            query.orderBy(pageRequest.getSort().stream()
                .map(order -> order.getDirection().isAscending()?
                    cb.asc(getSortPath(root, order.getProperty())):
                    cb.desc(getSortPath(root, order.getProperty())))
                .toArray(Order[]::new));
        }
    }

    /**
     * Creates a paginated typed query.
     */
    private TypedQuery<T> createPginiateTypedQuery(CriteriaQuery<T> query, PageRequest pageRequest){
        TypedQuery<T> typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult((int) pageRequest.getOffset());
        typedQuery.setMaxResults(pageRequest.getPageSize());
        return typedQuery;
    }

    /**
     * Creates a query to check if an entity exists.
     */
    private CriteriaQuery<Long> createExistsQuery(Specification<T> specification, Class<T> entityClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> existsQuery = cb.createQuery(Long.class);
        Root<T> existsRoot = existsQuery.from(entityClass);
        existsQuery.select(cb.count(existsRoot));

        applySpecification(specification, cb, (CriteriaQuery<T>) existsQuery, existsRoot);

        return existsQuery;
    }

    /**
     * Executes a count query based on the specification.
     */
    private Long executeCountQuery(Specification<T> specification, Class<T> entityClass) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<T> countRoot = countQuery.from(entityClass);
        countQuery.select(cb.count(countRoot));

        applySpecification(specification, cb, (CriteriaQuery<T>) countQuery, countRoot);

        return entityManager.createQuery(countQuery).getSingleResult();
    }
}
