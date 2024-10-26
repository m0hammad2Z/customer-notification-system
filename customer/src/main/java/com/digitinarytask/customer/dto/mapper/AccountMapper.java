package com.digitinarytask.customer.dto.mapper;

import com.digitinarytask.customer.domain.entity.Account;
import com.digitinarytask.customer.dto.domain.AccountDTO;
import org.mapstruct.*;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Named("toDTO")
    @Mappings({
        @Mapping(target = "customerId", source = "customer.id")
    })
    AccountDTO toDTO(Account account);

    @Named("toEntity")
    @InheritInverseConfiguration(name = "toDTO")
    Account toEntity(AccountDTO accountDTO);
}
