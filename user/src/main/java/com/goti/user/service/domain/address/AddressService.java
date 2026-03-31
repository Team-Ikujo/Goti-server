package com.goti.user.service.domain.address;

import com.goti.user.domain.entity.user.AddressEntity;
import com.goti.user.domain.entity.user.MemberEntity;
import com.goti.user.dto.response.AddressRegisterResponse;

import java.util.Optional;

public interface AddressService {

	AddressRegisterResponse register(
		String zipCode, String baseAddress, String detailAddress, MemberEntity member
	);

	Optional<AddressEntity> findAddress(MemberEntity member);
}
