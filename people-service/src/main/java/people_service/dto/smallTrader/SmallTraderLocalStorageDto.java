package people_service.dto.smallTrader;

import people_service.model.SmallTrader;

public record SmallTraderLocalStorageDto(
        Long id,
        String role,
        String token
) {
    public static SmallTraderLocalStorageDto fromSmallTrader(SmallTrader smallTrader, String token) {
        return new SmallTraderLocalStorageDto(
                smallTrader.getId(),
                smallTrader.getRole().name(),
                token
        );
    }
}
