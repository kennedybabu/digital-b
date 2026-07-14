package mapper;


import dto.CustomerRequest;
import dto.CustomerResponse;
import dto.CutsomerCreatedResponse;
import dto.UpdateCustomerRequest;
import model.Customer;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;


@Mapper(componentModel = "spring")
public interface CustomerMapper {
    Customer toEntity(CustomerRequest request);

    CustomerResponse toResponse(Customer customer);

    CutsomerCreatedResponse toCreateResponse(Customer customer);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateCustomerFromRequest(UpdateCustomerRequest request, @MappingTarget Customer customer);
}
