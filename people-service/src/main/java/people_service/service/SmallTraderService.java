package people_service.service;

import people_service.dto.smallTrader.SmallTraderAdminDto;
import people_service.dto.smallTrader.SmallTraderUpdateDto;
import people_service.model.ChangePasswordRequest;
import people_service.model.SmallTrader;

import java.util.List;

public interface SmallTraderService {
    public String signUpUser(SmallTrader smallTrader);
    public void enableAppUser(String email);
    public List<SmallTraderAdminDto> getAllSmallTraderAdmin();
    public SmallTraderAdminDto updateSmallTrader(Long id, SmallTraderUpdateDto smallTraderUpdateDto);
    public SmallTraderAdminDto deleteSmallTrader(Long id);
    public SmallTraderAdminDto changeAccountStatus(Long id);
    public Long changePassword(Long id, ChangePasswordRequest changePasswordRequest);
    public SmallTraderAdminDto findById(Long id);
    public Long countSmallTraderByStatusTrue();
}
