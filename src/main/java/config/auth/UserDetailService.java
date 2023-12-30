package config.auth;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import domain.entity.User;
import lombok.RequiredArgsConstructor;
import repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserDetailService implements UserDetailsService {

	private final UserRepository userRepository; // 사용자 정보를 데이터베이스에서 조회

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// Spring Security에서 사용자의 로그인 아이디를 전달받아 해당 아이디를 기반으로 데이터베이스에서 사용자 정보를 조회
		User user = userRepository.findByLoginId(username).orElseThrow(() -> {
			return new UsernameNotFoundException("해당 유저를 찾을 수 없습니다.");
		});

		// Security의 세션에 유저 정보가 저장됨
		return new UserDetail(user);
	}
}
