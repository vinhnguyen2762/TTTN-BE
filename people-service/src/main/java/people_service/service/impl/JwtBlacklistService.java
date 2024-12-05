package people_service.service.impl;

import org.springframework.stereotype.Service;
import people_service.exception.FailedException;

import java.util.HashSet;
import java.util.Set;

public class JwtBlacklistService {
    // Sử dụng Set để lưu trữ các token bị blacklist
    private static Set<String> blacklistedTokens = new HashSet<>();

    // Thêm token vào blacklist khi người dùng đăng xuất
    public static void blacklistToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new FailedException("Token can not null");
        }
        blacklistedTokens.add(token);
    }

    // Kiểm tra token có nằm trong blacklist hay không
    public static boolean isTokenBlacklisted(String token) {
        return blacklistedTokens.contains(token);
    }
}
