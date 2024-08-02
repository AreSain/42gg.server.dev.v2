package gg.agenda.api.user.ticket;

import static gg.data.agenda.type.Location.*;
import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import gg.agenda.api.user.ticket.controller.response.TicketCountResDto;
import gg.agenda.api.user.ticket.controller.response.TicketHistoryResDto;
import gg.data.agenda.Agenda;
import gg.data.agenda.AgendaProfile;
import gg.data.agenda.Ticket;
import gg.data.user.User;
import gg.repo.agenda.AgendaTeamRepository;
import gg.repo.agenda.TicketRepository;
import gg.utils.TestDataUtils;
import gg.utils.annotation.IntegrationTest;
import gg.utils.dto.PageRequestDto;
import gg.utils.fixture.agenda.AgendaFixture;
import gg.utils.fixture.agenda.AgendaProfileFixture;
import gg.utils.fixture.agenda.AgendaTeamFixture;
import gg.utils.fixture.agenda.AgendaTeamProfileFixture;
import gg.utils.fixture.agenda.TicketFixture;

@IntegrationTest
@AutoConfigureMockMvc
@Transactional
public class TicketControllerTest {
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TestDataUtils testDataUtils;
	@Autowired
	private TicketRepository ticketRepository;
	@Autowired
	private AgendaTeamRepository agendaTeamRepository;
	@Autowired
	private AgendaFixture agendaFixture;
	@Autowired
	private AgendaTeamFixture agendaTeamFixture;
	@Autowired
	private AgendaProfileFixture agendaProfileFixture;
	@Autowired
	private AgendaTeamProfileFixture agendaTeamProfileFixture;
	@Autowired
	private TicketFixture ticketFixture;
	User seoulUser;
	User gyeongsanUser;
	String seoulUserAccessToken;
	String gyeongsanUserAccessToken;
	AgendaProfile seoulUserAgendaProfile;
	AgendaProfile gyeongsanUserAgendaProfile;

	@Nested
	@DisplayName("Apporve되어 있지 않은 티켓 생성 테스트")
	class AddTeamTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaProfileFixture.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaProfileFixture.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@Test
		@DisplayName("티켓 생성 성공")
		void addTicketSetupSuccess() throws Exception {
			//given && when
			mockMvc.perform(
					post("/agenda/ticket")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
			// then
			Ticket createdTicket = ticketRepository.findByAgendaProfileId(seoulUserAgendaProfile.getId())
				.orElseThrow();
			assertThat(createdTicket.getAgendaProfile().getId()).isEqualTo(seoulUserAgendaProfile.getId());
			assertThat(createdTicket.getIsApproved()).isFalse();
			assertThat(createdTicket.getIsUsed()).isFalse();
		}

		@Test
		@DisplayName("404 티켓 생성 실패 - 프로필이 존재하지 않는 경우")
		void addTicketSetupFailToNotFoundProfile() throws Exception {
			//given
			User notExistUser = testDataUtils.createNewUser();
			String notExistUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(notExistUser);
			//when
			mockMvc.perform(
					post("/agenda/ticket")
						.header("Authorization", "Bearer " + notExistUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}

		@Test
		@DisplayName("409 티켓 생성 실패 - 이미 티켓이 존재하는 경우")
		void addTicketSetupFailToAnotherTicketSet() throws Exception {
			//given
			ticketFixture.createNotApporveTicket(seoulUserAgendaProfile);
			//when
			mockMvc.perform(
					post("/agenda/ticket")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
		}
	}

	@Nested
	@DisplayName("티켓 개수 확인 테스트")
	class FindTicketCountTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaProfileFixture.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaProfileFixture.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@Test
		@DisplayName("200 티켓 개수 확인 성공")
		void findTicketCountSuccess() throws Exception {
			//given
			ticketFixture.createTicket(seoulUserAgendaProfile);
			ticketFixture.createTicket(seoulUserAgendaProfile);
			//when
			String res = mockMvc.perform(
					get("/agenda/ticket")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TicketCountResDto result = objectMapper.readValue(res, TicketCountResDto.class);
			//then
			assertThat(result.getTicketCount()).isEqualTo(2);
		}

		@Test
		@DisplayName("200 티켓 개수 확인 성공 - 티켓이 없는 경우")
		void findTicketCountSuccessToEmptyTicket() throws Exception {
			//when
			String res = mockMvc.perform(
					get("/agenda/ticket")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TicketCountResDto result = objectMapper.readValue(res, TicketCountResDto.class);
			//then
			assertThat(result.getTicketCount()).isEqualTo(0);
		}

		@Test
		@DisplayName("404 티켓 개수 확인 실패 - 프로필이 존재하지 않는 경우")
		void findTicketCountFailToNotFoundProfile() throws Exception {
			//given
			User notExistUser = testDataUtils.createNewUser();
			String notExistUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(notExistUser);
			//when
			mockMvc.perform(
					get("/agenda/ticket")
						.header("Authorization", "Bearer " + notExistUserAccessToken)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}
	}

	@Nested
	@DisplayName("티켓 히스토리 조회 테스트")
	class FindTicketHistoryTest {
		@BeforeEach
		void beforeEach() {
			seoulUser = testDataUtils.createNewUser();
			seoulUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(seoulUser);
			seoulUserAgendaProfile = agendaProfileFixture.createAgendaProfile(seoulUser, SEOUL);
			gyeongsanUser = testDataUtils.createNewUser();
			gyeongsanUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(gyeongsanUser);
			gyeongsanUserAgendaProfile = agendaProfileFixture.createAgendaProfile(gyeongsanUser, GYEONGSAN);
		}

		@ParameterizedTest
		@ValueSource(ints = {1, 2, 3, 4, 5})
		@DisplayName("200 티켓 히스토리 조회 성공")
		void findTicketHistorySuccess(int page) throws Exception {
			//given
			for (int i = 0; i < 23; i++) {
				ticketFixture.createTicket(seoulUserAgendaProfile);
			}
			PageRequestDto req = new PageRequestDto(page, 5);
			String content = objectMapper.writeValueAsString(req);
			//when
			String res = mockMvc.perform(
					get("/agenda/ticket/history")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.param("page", String.valueOf(page))
						.content(content)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TicketHistoryResDto[] result = objectMapper.readValue(res, TicketHistoryResDto[].class);
			//then
			assertThat(result).hasSize(((page - 1) * 5) < 23
				? Math.min(5, 23 - (page - 1) * 5) : 0);
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - approve 되어있지 않은 경우")
		void findTicketHistorySuccessToNotApprove() throws Exception {
			//given
			ticketFixture.createTicket(seoulUserAgendaProfile, false, false, null, null);
			PageRequestDto req = new PageRequestDto(1, 5);
			String content = objectMapper.writeValueAsString(req);
			//when
			String res = mockMvc.perform(
					get("/agenda/ticket/history")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.content(content)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TicketHistoryResDto[] result = objectMapper.readValue(res, TicketHistoryResDto[].class);
			//then
			assertThat(result).hasSize(1);
			assertThat(result[0].getIssuedFrom()).isEqualTo("42Intra");
			assertThat(result[0].getUsedTo()).isEqualTo("NotApproved");
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - approve 되어있고 used 되어있는 경우")
		void findTicketHistorySuccessToUsed() throws Exception {
			//given
			Agenda seoulAgenda = agendaFixture.createAgenda(SEOUL);
			Ticket ticket = ticketFixture.createTicket(seoulUserAgendaProfile, true, true, null,
				seoulAgenda.getAgendaKey());
			ticketRepository.save(ticket);
			PageRequestDto req = new PageRequestDto(1, 5);
			String content = objectMapper.writeValueAsString(req);
			//when
			String res = mockMvc.perform(
					get("/agenda/ticket/history")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.content(content)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TicketHistoryResDto[] result = objectMapper.readValue(res, TicketHistoryResDto[].class);
			//then
			assertThat(result).hasSize(1);
			assertThat(result[0].getIssuedFrom()).isEqualTo("42Intra");
			assertThat(result[0].getUsedTo()).isEqualTo(seoulAgenda.getTitle());
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - approve 되어있고 used 되어있지 않은 경우")
		void findTicketHistorySuccessToNotUsed() throws Exception {
			//given
			Agenda seoulAgenda = agendaFixture.createAgenda(SEOUL);
			Ticket ticket = ticketFixture.createTicket(seoulUserAgendaProfile, true, false, null,
				null);
			ticketRepository.save(ticket);
			PageRequestDto req = new PageRequestDto(1, 5);
			String content = objectMapper.writeValueAsString(req);
			//when
			String res = mockMvc.perform(
					get("/agenda/ticket/history")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.content(content)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TicketHistoryResDto[] result = objectMapper.readValue(res, TicketHistoryResDto[].class);
			//then
			assertThat(result).hasSize(1);
			assertThat(result[0].getIssuedFrom()).isEqualTo("42Intra");
			assertThat(result[0].getUsedTo()).isEqualTo("NotUsed");
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - refund 되어있고 used 되어있지 않은 경우")
		void findTicketHistorySuccessToRefund() throws Exception {
			//given
			Agenda seoulAgenda = agendaFixture.createAgenda(SEOUL);
			Ticket ticket = ticketFixture.createTicket(seoulUserAgendaProfile, true, false, seoulAgenda.getAgendaKey(),
				null);
			ticketRepository.save(ticket);
			PageRequestDto req = new PageRequestDto(1, 5);
			String content = objectMapper.writeValueAsString(req);
			//when
			String res = mockMvc.perform(
					get("/agenda/ticket/history")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.content(content)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TicketHistoryResDto[] result = objectMapper.readValue(res, TicketHistoryResDto[].class);
			//then
			assertThat(result).hasSize(1);
			assertThat(result[0].getIssuedFrom()).isEqualTo(seoulAgenda.getTitle());
			assertThat(result[0].getUsedTo()).isEqualTo("NotUsed");
		}

		@Test
		@DisplayName("200 티켓 히스토리 조회 성공 - 티켓이 없는 경우")
		void findTicketHistorySuccessToEmptyTicket() throws Exception {
			//given
			PageRequestDto req = new PageRequestDto(1, 5);
			String content = objectMapper.writeValueAsString(req);
			//when
			String res = mockMvc.perform(
					get("/agenda/ticket/history")
						.header("Authorization", "Bearer " + seoulUserAccessToken)
						.content(content)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
			TicketHistoryResDto[] result = objectMapper.readValue(res, TicketHistoryResDto[].class);
			//then
			assertThat(result).isEmpty();
		}

		@Test
		@DisplayName("404 티켓 히스토리 조회 실패 - 프로필이 존재하지 않는 경우")
		void findTicketHistoryFailToNotFoundProfile() throws Exception {
			//given
			User notExistUser = testDataUtils.createNewUser();
			String notExistUserAccessToken = testDataUtils.getLoginAccessTokenFromUser(notExistUser);
			PageRequestDto req = new PageRequestDto(1, 5);
			String content = objectMapper.writeValueAsString(req);
			//when
			mockMvc.perform(
					get("/agenda/ticket/history")
						.header("Authorization", "Bearer " + notExistUserAccessToken)
						.content(content)
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
		}
	}
}