package gg.agenda.api.user.agendateam.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.response.TeamCreateResDto;
import gg.agenda.api.user.agendateam.service.AgendaTeamService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/team")
public class AgendaTeamController {
	private final AgendaTeamService agendaTeamService;

	/**
	 * 아젠다 팀 생성하기
	 * @param user 사용자 정보, teamCreateReqDto 팀 생성 요청 정보, agendaId 아젠다 아이디
	 * @return 만들어진 팀 KEY
	 */
	@PostMapping
	public ResponseEntity<TeamCreateResDto> agendaTeamAdd(@Parameter(hidden = true) @Login UserDto user,
		@RequestBody TeamCreateReqDto teamCreateReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		TeamCreateResDto teamCreateResDto = agendaTeamService.addAgendaTeam(user, teamCreateReqDto, agendaKey);
		return ResponseEntity.status(HttpStatus.CREATED).body(teamCreateResDto);
	}
}
