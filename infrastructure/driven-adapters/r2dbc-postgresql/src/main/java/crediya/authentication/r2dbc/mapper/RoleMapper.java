package crediya.authentication.r2dbc.mapper;

import crediya.authentication.model.role.Role;
import crediya.authentication.r2dbc.entity.RoleEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    
    RoleMapper INSTANCE = Mappers.getMapper(RoleMapper.class);
    
    @Mapping(source = "roleId", target = "id")
    Role entityToDomain(RoleEntity entity);
    
    @Mapping(source = "id", target = "roleId")
    RoleEntity domainToEntity(Role role);
}