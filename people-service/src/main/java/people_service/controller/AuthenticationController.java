package people_service.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import people_service.dto.smallTrader.SmallTraderAdminDto;
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
}
