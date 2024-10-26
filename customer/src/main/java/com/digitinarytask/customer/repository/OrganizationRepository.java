package com.digitinarytask.customer.repository;

import com.digitinarytask.customer.domain.entity.Organization;
import com.digitinarytask.customer.repository.custom.OrganizationRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>, OrganizationRepositoryCustom {

}
