package people_service.dto.smallTrader;

import people_service.model.SmallTrader;

public record SmallTraderLocalStorageDto(
        Long id,
        String token,
        String role
) {
    public static SmallTraderLocalStorageDto fromSmallTrader(Long id, String token, String role) {
        return new SmallTraderLocalStorageDto(
                id,
                token,
                role
        );
    }
}
