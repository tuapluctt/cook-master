package vn.hvt.cook_master.controller;


import com.nimbusds.jose.JOSEException;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import vn.hvt.cook_master.dto.request.AuthenticationRequest;
import vn.hvt.cook_master.dto.request.IntrospectRequest;
import vn.hvt.cook_master.dto.request.LogoutRequest;
import vn.hvt.cook_master.dto.request.RefreshRequest;
import vn.hvt.cook_master.dto.response.ApiResponse;
import vn.hvt.cook_master.dto.response.AuthenticationResponse;
import vn.hvt.cook_master.dto.response.IntrospecResponse;
import vn.hvt.cook_master.service.AuthenticationService;
//import vn.hvt.identity_service.dto.request.AuthenticationRequest;
//import vn.hvt.identity_service.dto.request.IntrospectRequest;
//import vn.hvt.identity_service.dto.request.LogoutRequest;
//import vn.hvt.identity_service.dto.request.RefreshRequest;
//import vn.hvt.identity_service.dto.response.ApiResponse;
//import vn.hvt.identity_service.dto.response.AuthenticationResponse;
//import vn.hvt.identity_service.dto.response.IntrospecResponse;
//import vn.hvt.identity_service.service.AuthenticationService;

import java.text.ParseException;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationController {
    AuthenticationService authenticationService;

    @PostMapping("/token")
    ApiResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var authenticated = authenticationService.authenticate(request);

        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticated)
                .build();
    }

    @PostMapping("/logout")
    ApiResponse<Void> logout(@RequestBody LogoutRequest request)
            throws ParseException, JOSEException {
        authenticationService.logout(request);
        return ApiResponse.<Void>builder()
                .build();

    }

    @PostMapping("/introspect")
    ApiResponse<IntrospecResponse> authenticate(@RequestBody IntrospectRequest request)
            throws ParseException, JOSEException {
        var authenticated = authenticationService.introspect(request);
        return ApiResponse.<IntrospecResponse>builder()
                .result(authenticated)
                .build();

    }

    @PostMapping("/refresh")
    ApiResponse<AuthenticationResponse> refresh(@RequestBody RefreshRequest request)
            throws ParseException, JOSEException {

        var authenticated = authenticationService.refeshToken(request);
        return ApiResponse.<AuthenticationResponse>builder()
                .result(authenticated)
                .build();
    }
}
