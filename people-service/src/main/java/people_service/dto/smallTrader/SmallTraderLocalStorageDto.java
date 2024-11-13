package people_service.dto.smallTrader;

import people_service.model.SmallTrader;

public record SmallTraderLocalStorageDto(
        Long id,
        String role
) {
    public static SmallTraderLocalStorageDto fromSmallTrader(SmallTrader smallTrader) {
        return new SmallTraderLocalStorageDto(
                smallTrader.getId(),
                smallTrader.getRole().name()
        );
    }
}
