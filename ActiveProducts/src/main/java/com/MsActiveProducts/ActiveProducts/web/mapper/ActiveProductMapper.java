package com.MsActiveProducts.ActiveProducts.web.mapper;

import com.MsActiveProducts.ActiveProducts.entity.ActiveProduct;
import com.MsActiveProducts.ActiveProducts.web.model.ActiveProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface ActiveProductMapper {

    ActiveProduct modelToEntity(ActiveProductModel model);

    ActiveProductModel entityToModel(ActiveProduct event);

    @Mapping(target  = "id", ignore = true)
    void update(@MappingTarget ActiveProduct entity, ActiveProduct updateEntity);

}
