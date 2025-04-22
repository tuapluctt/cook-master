package vn.hvt.cook_master.service;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import vn.hvt.cook_master.dto.request.AuthenticationRequest;
import vn.hvt.cook_master.dto.request.IntrospectRequest;
import vn.hvt.cook_master.dto.request.LogoutRequest;
import vn.hvt.cook_master.dto.request.RefreshRequest;
import vn.hvt.cook_master.dto.response.AuthenticationResponse;
import vn.hvt.cook_master.dto.response.IntrospecResponse;
import vn.hvt.cook_master.entity.InvalidateToken;
import vn.hvt.cook_master.entity.User;
import vn.hvt.cook_master.exception.AppException;
import vn.hvt.cook_master.exception.ErrorCode;
import vn.hvt.cook_master.repository.InvalidateTokenRepository;
import vn.hvt.cook_master.repository.UserRepository;

import java.text.ParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class AuthenticationService {
    UserRepository userRepository;
    InvalidateTokenRepository invalidateTokenRepository;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNING_KEY ;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);

        boolean auth = passwordEncoder.matches(request.getPasswordHash(), user.getPasswordHash());

        if (!auth) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        log.info("User {} authenticated successfully", user.getUsername());

        String token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }


    private String generateToken(User user)  {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issuer("https://example.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + VALID_DURATION * 1000))
                .claim("scope", builScope(user))
                .jwtID(UUID.randomUUID().toString())
                .build();

        Payload payload = new Payload(claimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            // Sign the JWS object with the MAC signer
            jwsObject.sign(new MACSigner(SIGNING_KEY));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException("Error signing JWT", e);
        }

    }

    public IntrospecResponse introspect(IntrospectRequest request) throws ParseException, JOSEException {
        try {
            var token = request.getToken();

            verifyToken(token, false);

            return IntrospecResponse.builder()
                    .valid(true)
                    .build();
        } catch (AppException e) {
            return IntrospecResponse.builder()
                    .valid(false)
                    .build();
        }
    }


    // lưu token đã logout vào db
    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        var token = verifyToken(request.getToken(), false);

        String jit = token.getJWTClaimsSet().getJWTID();
        Date  expirationTime = token.getJWTClaimsSet().getExpirationTime();

        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(jit)
                .expiryTime(expirationTime)
                .build();

        invalidateTokenRepository.save(invalidateToken);
    }

    private SignedJWT verifyToken(String token ,boolean refresh) throws JOSEException, ParseException {
        //  chữ ký của token
        JWSVerifier jwsVerifier = new MACVerifier(SIGNING_KEY.getBytes());

        SignedJWT signedJWT = SignedJWT.parse(token);

        // nếu là refresh token thì expirationTime = issueTime ( thời gian phát hành ) + REFRESHABLE_DURATION ( thời gian có thể refresh token )
        // nếu không thì lấy thời gian hết hạn từ token
        Date expirationTime =(refresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();



        var verify =  signedJWT.verify(jwsVerifier);

        // kiểm tra xem token có hợp lệ không và thời gian hết hạn của token
        if (!(verify && expirationTime.after(new Date()))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // kiểm tra xem token đã được logout chưa .
        if(invalidateTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // trả về token đã được xác thực
        return signedJWT;
    }

    private String builScope(User user) {
        // các scope của auth quy định các role cách nhau bằng dấu cách
        StringJoiner joiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(role -> {
                joiner.add("ROLE_"+role.getRoleName());

                if(!CollectionUtils.isEmpty(role.getPermissions())){
                    role.getPermissions().forEach(permission -> {
                        joiner.add(permission.getPermissionName());
                    });
                }
            });
        }
        return joiner.toString();
    }

    public AuthenticationResponse refeshToken(RefreshRequest token) throws ParseException, JOSEException {
        // xác thực token
        var signedJWT = verifyToken(token.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        var expirationTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidateToken invalidateToken = InvalidateToken.builder()
                .id(jit)
                .expiryTime(expirationTime)
                .build();

        invalidateTokenRepository.save(invalidateToken);

        var user = userRepository.findByUsername(signedJWT.getJWTClaimsSet().getSubject())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        var newToken = generateToken(user);

        return AuthenticationResponse.builder()
                .authenticated(true)
                .token(newToken)
                .build();
    }

}
