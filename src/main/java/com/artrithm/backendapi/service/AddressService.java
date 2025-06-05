package com.artrithm.backendapi.service;

import com.artrithm.backendapi.dto.AddressDto;
import com.artrithm.backendapi.model.Address;
import com.artrithm.backendapi.model.User;
import com.artrithm.backendapi.repository.AddressRepository;
import com.artrithm.backendapi.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public List<AddressDto> getAddresses(Long userId) {
        return addressRepository.findByUserId(userId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public void addAddress(Long userId, AddressDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        Address address = new Address();
        address.setUser(user);
        address.setZonecode(dto.getZonecode());
        address.setRoadAddress(dto.getRoadAddress());
        address.setJibunAddress(dto.getJibunAddress());
        address.setDetailAddress(dto.getDetailAddress());
        address.setReference(dto.getReference());
        address.setDefault(false);

        addressRepository.save(address);
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    public void setDefaultAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("주소를 찾을 수 없습니다."));
        addressRepository.resetDefaultAddress(address.getUser().getId());
        address.setDefault(true);
        addressRepository.save(address);
    }

    private AddressDto toDto(Address address) {
        AddressDto dto = new AddressDto();
        dto.setId(address.getId());
        dto.setZonecode(address.getZonecode());
        dto.setRoadAddress(address.getRoadAddress());
        dto.setJibunAddress(address.getJibunAddress());
        dto.setDetailAddress(address.getDetailAddress());
        dto.setReference(address.getReference());
        dto.setDefault(address.isDefault());
        return dto;
    }
}
