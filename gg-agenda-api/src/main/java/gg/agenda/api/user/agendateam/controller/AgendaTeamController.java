package gg.agenda.api.user.agendateam.controller;

import java.util.Optional;
import java.util.UUID;

import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import gg.agenda.api.user.agendateam.controller.request.TeamCreateReqDto;
import gg.agenda.api.user.agendateam.controller.request.TeamKeyReqDto;
import gg.agenda.api.user.agendateam.controller.response.MyTeamSimpleResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamDetailsResDto;
import gg.agenda.api.user.agendateam.controller.response.TeamKeyResDto;
import gg.agenda.api.user.agendateam.service.AgendaTeamService;
import gg.agenda.api.user.ticket.service.TicketService;
import gg.auth.UserDto;
import gg.auth.argumentresolver.Login;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/agenda/team")
public class AgendaTeamController {
	private final AgendaTeamService agendaTeamService;
	private final TicketService ticketService;

	/**
	 * 내 팀 간단 정보 조회
	 * @param user 사용자 정보, agendaId 아젠다 아이디
	 * @return 내 팀 간단 정보
	 */
	@GetMapping("/my")
	public ResponseEntity<Optional<MyTeamSimpleResDto>> myTeamSimpleDetails(
		@Parameter(hidden = true) @Login UserDto user,
		@RequestParam("agenda_key") UUID agendaKey) {
		Optional<MyTeamSimpleResDto> myTeamSimpleResDto = agendaTeamService.detailsMyTeamSimple(user, agendaKey);
		if (myTeamSimpleResDto.isEmpty()) {
			return ResponseEntity.noContent().build();
		}
		return ResponseEntity.ok(myTeamSimpleResDto);
	}

	/*
	 * 아젠다 팀 상세 정보 조회
	 * @param user 사용자 정보, teamDetailsReqDto 팀 상세 정보 요청 정보, agendaId 아젠다 아이디
	 * @return 팀 상세 정보
	 */
	@GetMapping
	public ResponseEntity<TeamDetailsResDto> agendaTeamDetails(@Parameter(hidden = true) @Login UserDto user,
		@RequestBody @Valid TeamKeyReqDto teamKeyReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		TeamDetailsResDto teamDetailsResDto = agendaTeamService.detailsAgendaTeam(user, agendaKey, teamKeyReqDto);
		return ResponseEntity.ok(teamDetailsResDto);
	}

	/**
	 * 아젠다 팀 생성하기
	 * @param user 사용자 정보, teamCreateReqDto 팀 생성 요청 정보, agendaId 아젠다 아이디
	 * @return 만들어진 팀 KEY
	 */
	@PostMapping
	public ResponseEntity<TeamKeyResDto> agendaTeamAdd(@Parameter(hidden = true) @Login UserDto user,
		@RequestBody @Valid TeamCreateReqDto teamCreateReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		TeamKeyResDto teamKeyReqDto = agendaTeamService.addAgendaTeam(user, teamCreateReqDto, agendaKey);
		return ResponseEntity.status(HttpStatus.CREATED).body(teamKeyReqDto);
	}

	/**
	 * 아젠다 팀 확정하기
	 * @param user 사용자 정보, teamKeyReqDto 팀 KEY 요청 정보, agendaId 아젠다 아이디
	 */
	@PatchMapping("/confirm")
	public ResponseEntity<Void> confirmTeam(@Parameter(hidden = true) @Login UserDto user,
		@RequestBody @Valid TeamKeyReqDto teamKeyReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		agendaTeamService.confirmTeam(user, agendaKey, teamKeyReqDto.getTeamKey());
		return ResponseEntity.ok().build();
	}

	/**
	 * 아젠다 팀 나가기
	 * @param user 사용자 정보, teamKeyReqDto 팀 KEY 요청 정보, agendaId 아젠다 아이디
	 */
	@PatchMapping("/cancel")
	public ResponseEntity<Void> leaveAgendaTeam(@Parameter(hidden = true) @Login UserDto user,
		@RequestBody @Valid TeamKeyReqDto teamKeyReqDto, @RequestParam("agenda_key") UUID agendaKey) {
		agendaTeamService.agendaTeamLeave(user, agendaKey, teamKeyReqDto.getTeamKey());
		return ResponseEntity.noContent().build();
	}
}
