package domain.dto;

import lombok.Data;

@Data
public class UserLoginRequest { // 로그인시 사용되는 DTO
	private String loginId;
	private String password;

}
