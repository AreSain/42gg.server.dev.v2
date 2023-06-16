package com.gg.server.domain.user.controller;

import com.gg.server.domain.user.dto.*;
import com.gg.server.domain.user.service.UserService;
import com.gg.server.domain.user.type.RoleType;
import com.gg.server.global.security.config.properties.AppProperties;
import com.gg.server.global.security.cookie.CookieUtil;
import com.gg.server.global.security.jwt.utils.TokenHeaders;
import com.gg.server.global.utils.ApplicationYmlRead;
import com.gg.server.global.utils.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import java.io.IOException;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/pingpong/users")
public class UserController {
    private final UserService userService;
    private final AppProperties appProperties;
    private final ApplicationYmlRead applicationYmlRead;

    @PostMapping("/accesstoken")
    public ResponseEntity<UserAccessTokenDto> generateAccessToken(@RequestParam String refreshToken, HttpServletResponse response) {
        UserJwtTokenDto result = userService.regenerate(refreshToken);
        CookieUtil.addCookie(response, TokenHeaders.REFRESH_TOKEN, result.getRefreshToken(),
                (int)(appProperties.getAuth().getRefreshTokenExpiry() / 1000), applicationYmlRead.getDomain());
        return new ResponseEntity<>(new UserAccessTokenDto(result.getAccessToken()), HttpStatus.CREATED);
    }

    @GetMapping
    UserNormalDetailResponseDto getUserNormalDetail(@Parameter(hidden = true) @Login UserDto user){
        Boolean isAdmin = user.getRoleType() == RoleType.ADMIN;
        return new UserNormalDetailResponseDto(user.getIntraId(), user.getImageUri(), isAdmin);
    }

    @GetMapping("/live")
    UserLiveResponseDto getUserLiveDetail(@Parameter(hidden = true) @Login UserDto user) {
        return userService.getUserLiveDetail(user);
    }

    @GetMapping("/searches")
    UserSearchResponseDto searchUsers(@RequestParam String intraId){
        List<String> intraIds = userService.findByPartOfIntraId(intraId);
        return new UserSearchResponseDto(intraIds);
    }

    @GetMapping("/{intraId}")
    public UserDetailResponseDto getUserDetail(@PathVariable String intraId){
        return userService.getUserDetail(intraId);
    }

    @GetMapping("/{intraId}/rank")
    public UserRankResponseDto getUserRank(@PathVariable String intraId, @RequestParam Long season){
        return userService.getUserRankDetail(intraId, season);
    }

    @GetMapping("/{intraId}/historics")
    public UserHistoryResponseDto getUserHistory(@PathVariable String intraId, @RequestParam Long season) {
        return userService.getUserHistory(intraId, season);
    }

    @PutMapping("{intraId}")
    public ResponseEntity doModifyUser (@Valid @RequestBody UserModifyRequestDto userModifyRequestDto, @PathVariable String intraId) {
        userService.updateUser(userModifyRequestDto.getRacketType(), userModifyRequestDto.getStatusMessage(),
                userModifyRequestDto.getSnsNotiOpt(), intraId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    /**
     *기존 카카오 유저 42 로그인 인증
     */
    @GetMapping("/oauth/42")
    public void addOauthFortyTwo(HttpServletResponse response, @ModelAttribute @Valid UserAuthorizationDto authDto) throws IOException {
        CookieUtil.addCookie(response, TokenHeaders.ACCESS_TOKEN, authDto.getAccessToken(), 100000, applicationYmlRead.getDomain());
        response.sendRedirect(applicationYmlRead.getFrontUrl() + "/oauth2/authorization/42");
    }
    /**
     *기존 42user 카카오 로그인 인증
     */
    @GetMapping("/oauth/kakao")
    public void addOauthKakao(HttpServletResponse response, @ModelAttribute @Valid UserAuthorizationDto authDto) throws IOException {
        CookieUtil.addCookie(response, TokenHeaders.ACCESS_TOKEN, authDto.getAccessToken(),100000, applicationYmlRead.getDomain());
        response.sendRedirect(applicationYmlRead.getFrontUrl() + "/oauth2/authorization/kakao");
    }

    @DeleteMapping("/oauth/kakao")
    public void deleteOauthKakao(@Parameter(hidden = true) @Login UserDto user) {
        userService.deleteKakaoId(user.getId());
    }


}
