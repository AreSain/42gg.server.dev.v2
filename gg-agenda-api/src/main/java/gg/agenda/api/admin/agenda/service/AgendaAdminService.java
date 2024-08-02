package gg.agenda.api.admin.agenda.service;

import static gg.utils.exception.ErrorCode.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import gg.admin.repo.agenda.AgendaAdminRepository;
import gg.admin.repo.agenda.AgendaTeamAdminRepository;
import gg.agenda.api.admin.agenda.controller.request.AgendaAdminUpdateReqDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaTeam;
import gg.utils.exception.custom.NotExistException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AgendaAdminService {

	private final AgendaAdminRepository agendaAdminRepository;

	private final AgendaTeamAdminRepository agendaTeamAdminRepository;

	@Transactional(readOnly = true)
	public List<Agenda> getAgendaRequestList(Pageable pageable) {
		return agendaAdminRepository.findAll(pageable).getContent();
	}

	@Transactional
	public void updateAgenda(UUID agendaKey, AgendaAdminUpdateReqDto agendaDto) {
		Agenda agenda = agendaAdminRepository.findByAgendaKey(agendaKey)
			.orElseThrow(() -> new NotExistException(AGENDA_NOT_FOUND));
		List<AgendaTeam> teams = agendaTeamAdminRepository.findAllByAgenda(agenda);

		agenda.updateInformation(agendaDto.getAgendaTitle(), agendaDto.getAgendaContents(),
			agendaDto.getAgendaPoster());
		agenda.updateIsOfficial(agendaDto.getIsOfficial());
		agenda.updateIsRanking(agendaDto.getIsRanking());
		agenda.updateAgendaStatus(agendaDto.getAgendaStatus());
		agenda.updateSchedule(agendaDto.getAgendaDeadLine(), agendaDto.getAgendaStartTime(),
			agendaDto.getAgendaEndTime());
		agenda.updateLocation(agendaDto.getAgendaLocation(), teams);
		agenda.updateAgendaCapacity(agendaDto.getAgendaMinTeam(), agendaDto.getAgendaMaxTeam(), teams);
		agenda.updateAgendaTeamCapacity(agendaDto.getAgendaMinPeople(), agendaDto.getAgendaMaxPeople(), teams);
	}
}