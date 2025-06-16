package com.artrithm.backendapi.controller;

import com.artrithm.backendapi.dto.AddressDto;
import com.artrithm.backendapi.service.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    @GetMapping
    public ResponseEntity<List<AddressDto>> getAddresses(@PathVariable Long userId) {
        return ResponseEntity.ok(addressService.getAddresses(userId));
    }

    @PostMapping
    public ResponseEntity<Void> addAddress(@PathVariable Long userId, @RequestBody AddressDto dto) {
        addressService.addAddress(userId, dto);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{addressId}")
    public ResponseEntity<Void> deleteAddress(@PathVariable Long addressId) {
        addressService.deleteAddress(addressId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{addressId}/set-default")
    public ResponseEntity<Void> setDefault(@PathVariable Long addressId) {
        addressService.setDefaultAddress(addressId);
        return ResponseEntity.ok().build();
    }
}
