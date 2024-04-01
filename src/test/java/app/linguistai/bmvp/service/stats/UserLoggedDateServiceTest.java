package app.linguistai.bmvp.service.stats;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.stats.UserLoggedDate;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.stats.IUserLoggedDateRepository;
import app.linguistai.bmvp.response.stats.RUserLoggedDate;
import app.linguistai.bmvp.utils.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class UserLoggedDateServiceTest {

	@Mock
	private IUserLoggedDateRepository userLoggedDateRepository;

	@Mock
	private IAccountRepository accountRepository;

	@InjectMocks
	private UserLoggedDateService userLoggedDateService;

	@DisplayName("When adding a new logged date for a valid user, verify it is saved")
	@Test
	void whenAddingNewLoggedDateThenVerifySaved() throws NotFoundException {
		User user = new User();
		user.setEmail("test@test.com");
		user.setId(UUID.randomUUID());

		java.util.Date loggedDate = new java.util.Date();

		when(accountRepository.findUserByEmail(user.getEmail()))
				.thenReturn(Optional.of(user));
		when(userLoggedDateRepository.findByUserAndLoggedDate(eq(user), any()))
				.thenReturn(Optional.empty());

		userLoggedDateService.addLoggedDateByEmailAndDate(user.getEmail(), loggedDate);

		verify(userLoggedDateRepository, times(1)).findByUserAndLoggedDate(eq(user), any());
		verify(userLoggedDateRepository, times(1)).save(any(UserLoggedDate.class));
	}

	@DisplayName("When user logs in on five different dates, check if all dates are returned")
	@Test
	void whenUserLogsInOnFiveDifferentDatesThenCheckIfAllDatesReturned() {
		User user = new User();
		user.setEmail("test@test.com");
		user.setId(UUID.randomUUID());

		Date date1 = Date.valueOf("2024-03-28");
		Date date2 = Date.valueOf("2024-03-29");
		Date date3 = Date.valueOf("2024-03-30");

		List<UserLoggedDate> loggedDates = new ArrayList<>();
		loggedDates.add(new UserLoggedDate(user, date1));
		loggedDates.add(new UserLoggedDate(user, date2));
		loggedDates.add(new UserLoggedDate(user, date3));

		when(userLoggedDateRepository.findByUserEmailOrderByLoggedDateDesc(user.getEmail()))
				.thenReturn(loggedDates);

		RUserLoggedDate returnedUserLoggedDates = userLoggedDateService.getLoggedDates(user.getEmail(), "desc", null);

		List<Date> returnedDates = returnedUserLoggedDates.getLoggedDates();

		assertEquals(loggedDates.size(), returnedDates.size());
		assertTrue(returnedDates.contains(date1));
		assertTrue(returnedDates.contains(date2));
		assertTrue(returnedDates.contains(date3));
	}

	@DisplayName("When adding a logged date for an unregistered user, throw NotFoundException")
	@Test
	void whenAddingLoggedDateForUnregisteredUserThenThrowNotFoundException() {
		String email = "unregistered@test.com";
		java.util.Date loggedDate = new java.util.Date();

		when(accountRepository.findUserByEmail(anyString()))
				.thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> {
			userLoggedDateService.addLoggedDateByEmailAndDate(email, loggedDate);
		});
	}

	@DisplayName("When adding a logged date for a user who logged in twice on the same day, do not add it again")
	@Test
	void whenAddingLoggedDateForUserWithDuplicateDateThenDoNotAddAgain() throws NotFoundException {
		User user = new User();
		user.setEmail("test@test.com");
		user.setId(UUID.randomUUID());

		java.util.Date loggedDate = Date.valueOf("2024-03-28");

		when(accountRepository.findUserByEmail(user.getEmail()))
				.thenReturn(Optional.of(user));
		when(userLoggedDateRepository.findByUserAndLoggedDate(eq(user), eq(DateUtils.convertUtilDateToSqlDate(loggedDate))))
				.thenReturn(java.util.Optional.of(new UserLoggedDate()));

		userLoggedDateService.addLoggedDateByEmailAndDate(user.getEmail(), loggedDate);

		verify(userLoggedDateRepository, never()).save(any(UserLoggedDate.class));

	}

	@Test
	@DisplayName("When fetching logged dates in ascending order, verify sorting")
	void testGetLoggedDatesAscendingOrderWithNullDaysLimit() {
		String sort = "asc";

		User user = new User();
		user.setEmail("test@test.com");
		user.setId(UUID.randomUUID());

		Date date1 = Date.valueOf("2024-03-28");
		Date date2 = Date.valueOf("2024-03-29");
		Date date3 = Date.valueOf("2024-03-30");

		List<UserLoggedDate> loggedDates = new ArrayList<>();
		loggedDates.add(new UserLoggedDate(user, date1));
		loggedDates.add(new UserLoggedDate(user, date3));
		loggedDates.add(new UserLoggedDate(user, date2));

		when(userLoggedDateRepository.findByUserEmailOrderByLoggedDateAsc(user.getEmail()))
				.thenReturn(loggedDates.stream()
						.sorted(Comparator.comparing(UserLoggedDate::getLoggedDate))
						.collect(Collectors.toList()));

		RUserLoggedDate returnedUserLoggedDates = userLoggedDateService.getLoggedDates(user.getEmail(), sort, null);

		List<Date> returnedDates = returnedUserLoggedDates.getLoggedDates();

		assertNotNull(returnedDates);
		for (int i = 0; i < returnedDates.size() - 1; i++) {
			assertTrue(returnedDates.get(i).before(returnedDates.get(i + 1)) || returnedDates.get(i).equals(returnedDates.get(i + 1)));
		}
		assertEquals(loggedDates.size(), returnedDates.size());
	}

	@DisplayName("When getting logged dates for a user who never logged in, verify empty list is returned")
	@Test
	void whenGettingLoggedDatesForUserNeverLoggedInThenVerifyEmptyList() throws NotFoundException {
		User user = new User();
		user.setEmail("test@test.com");
		user.setId(UUID.randomUUID());

		when(userLoggedDateRepository.findByUserEmailOrderByLoggedDateDesc(user.getEmail())).thenReturn(Collections.emptyList());

		RUserLoggedDate rUserLoggedDate = userLoggedDateService.getLoggedDates(user.getEmail(), null, null);

		assertTrue(rUserLoggedDate.getLoggedDates().isEmpty());
	}
}