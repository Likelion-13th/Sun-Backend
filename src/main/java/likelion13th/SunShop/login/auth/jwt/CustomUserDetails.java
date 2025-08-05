package likelion13th.SunShop.login.auth.jwt;

import likelion13th.SunShop.domain.Order;
import likelion13th.SunShop.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {
    private final User user;

    // ✅ 사용자 엔티티 반환
    public User getUser() { return user; }

    // ✅ providerId 반환
    public String getProviderId() { return user.getProviderId(); }

    public int getMaxMileage() {
        return user.getMaxMileage();
    }

    public void useMileage(int availableMileage) {
        user.useMileage(availableMileage);
    }

    public void addMileage(int mileage) {
        user.addMileage(mileage);
    }

    public void updateRecentTotal(int finalPrice) {
        user.updateRecentTotal(finalPrice);
    }

    public void addOrder(Order order) {
        user.addOrder(order);
    }

    public Collection<Order> getOrders() {
        return user.getOrders();
    }

    // ✅ UserDetails 인터페이스 구현
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 권한 관리 안하면 null 또는 빈 리스트 반환
    }

    @Override
    public String getPassword() {
        return null; // OAuth2 기반 로그인이라 비밀번호 없음
    }

    @Override
    public String getUsername() {
        return user.getProviderId(); // 로그인 식별자: providerId 사용
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // 계정 만료 안 됨
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // 계정 잠김 없음
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // 자격 증명 만료 안 됨
    }

    @Override
    public boolean isEnabled() {
        return true; // 활성화된 사용자로 처리
    }
}
