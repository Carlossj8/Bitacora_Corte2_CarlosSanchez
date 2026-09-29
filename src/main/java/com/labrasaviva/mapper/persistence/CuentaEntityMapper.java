package com.labrasaviva.mapper.persistence;

import com.labrasaviva.model.domain.Cuenta;
import com.labrasaviva.model.entity.CuentaEntity;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CuentaEntityMapper {

    CuentaEntity toEntity(Cuenta domain);

    Cuenta toDomain(CuentaEntity entity);
}
