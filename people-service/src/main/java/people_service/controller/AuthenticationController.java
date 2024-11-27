package people_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.customer.CustomerAdminDto;
import people_service.dto.smallTrader.SmallTraderCodeDto;
import people_service.dto.smallTrader.SmallTraderForgetPasswordDto;
import people_service.dto.smallTrader.SmallTraderLocalStorageDto;
import people_service.model.AuthenticationRequest;
import people_service.model.RegistrationRequest;
import people_service.service.AuthService;
import people_service.service.RegistrationService;

@RestController
@RequestMapping("/api/v1/auth")
@AllArgsConstructor
public class AuthenticationController {

    private final RegistrationService registrationService;
    private final AuthService authService;

    @PostMapping("/authenticate")
    public ResponseEntity<SmallTraderLocalStorageDto> authenticate(@RequestBody AuthenticationRequest request) {
        SmallTraderLocalStorageDto rs = authService.login(request);
        return ResponseEntity.ok(rs);
    }

    @PostMapping("/register")
    private ResponseEntity<Long> register(@RequestBody RegistrationRequest request) {
        Long rs = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rs);
    }

    @GetMapping("/confirm")
    private String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }

    @GetMapping("/send-code")
    private ResponseEntity<String> sendCode(@RequestParam("email") String email) {
        String rs = authService.sendCodeToEmail(email);
        return ResponseEntity.ok().body(rs);
    }

    @PostMapping("/check-code")
    private ResponseEntity<Long> checkCode(@RequestBody SmallTraderCodeDto smallTraderCodeDto) {
        Long rs = authService.checkCodeEmail(smallTraderCodeDto);
        return ResponseEntity.ok().body(rs);
    }

    @PostMapping("/forget-password")
    private ResponseEntity<Long> forgetPassword(@RequestBody SmallTraderForgetPasswordDto smallTraderForgetPasswordDto) {
        Long rs = authService.changeForgetPassword(smallTraderForgetPasswordDto);
        return ResponseEntity.ok().body(rs);
    }

    @PostMapping("/confirm-password")
    private ResponseEntity<Long> confirmPassword(@RequestBody SmallTraderForgetPasswordDto smallTraderForgetPasswordDto) {
        Long rs = authService.confirmPassword(smallTraderForgetPasswordDto);
        return ResponseEntity.ok().body(rs);
    }
}
