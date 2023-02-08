package com.MsPassiveProducts.PassiveProducts.web.mapper;

import com.MsPassiveProducts.PassiveProducts.entity.PassiveProduct;
import com.MsPassiveProducts.PassiveProducts.web.model.PassiveProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PassiveProductMapper {

    PassiveProduct modelToEntity (PassiveProductModel model);
    PassiveProductModel entityToModel(PassiveProduct event);

    @Mapping(target = "id", ignore = true)
    void update(@MappingTarget PassiveProduct entity, PassiveProduct updateEntity);

}
