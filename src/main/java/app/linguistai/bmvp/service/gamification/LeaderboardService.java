package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.UserXP;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.IUserXPRepository;
import app.linguistai.bmvp.response.gamification.RLeaderboardXP;
import app.linguistai.bmvp.response.gamification.RUserXPRanking;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class LeaderboardService {
    private final IAccountRepository accountRepository;
    private final IUserXPRepository xpRepository;

    @Transactional
    public RLeaderboardXP getTopUsersByExperience(String email, Integer page, int size) throws Exception {
        try {
            Long loggedUserRanking = xpRepository.findGlobalUserRankByEmail(email);
            if (loggedUserRanking == null) {
                throw new NotFoundException("User's global XP ranking", true);
            }
            // If page number is not given, then return the page where the user is present
            if (page == null) {
                page = getPageNumberByEmail(loggedUserRanking, size);
            }

            Pageable pageable = PageRequest.of(page, size, Sort.by("experience").descending());
            Page<UserXP> userXPPage = xpRepository.findAll(pageable);

            RLeaderboardXP response = buildLeaderboardResponse(loggedUserRanking, userXPPage);
            log.info("Global XP leaderboard fetched for user with email {} (Page {}, Size {}). ", email, page, size);
            return response;
        } catch (NotFoundException e) {
            log.error("User's global XP ranking with email {} not found.", email);
            throw e;
        } catch (Exception e) {
            log.error("Get global XP leaderboard failed for email {} (Page {}, Size {}). ", email, page, size, e);
            throw new SomethingWentWrongException();
        }
    }

    @Transactional
    public RLeaderboardXP getTopFriendsByExperience(String email, Integer page, int size) throws Exception {
        try {
            User loggedUser = accountRepository.findUserByEmail(email).orElse(null);
            if (loggedUser == null) {
                throw new NotFoundException(User.class.getSimpleName(), true);
            }

            Long loggedUserRanking = xpRepository.findFriendsUserRankByEmail(loggedUser.getId());
            if (loggedUserRanking == null) {
                throw new NotFoundException("User's XP ranking among friends", true);
            }
            // If page number is not given, then return the page where the user is present
            if (page == null) {
                page = getPageNumberByEmail(loggedUserRanking, size);
            }

            Pageable pageable = PageRequest.of(page, size);
            Page<UserXP> userXPPage = xpRepository.findTopFriendsByExperience(loggedUser.getId(), pageable);

            RLeaderboardXP response = buildLeaderboardResponse(loggedUserRanking, userXPPage);
            log.info("Friends XP leaderboard fetched for user with email {} (Page {}, Size {}). ", email, page, size);
            return response;
        } catch (NotFoundException e) {
            if (e.getObject().equals(User.class.getSimpleName())) {
                log.error("When fetching friends XP leaderboard, user is not found with email {}", email);
            } else {
                log.error("User's XP ranking among friends with email {} not found.", email);
            }
            throw e;
        } catch (Exception e) {
            log.error("Get friends XP leaderboard failed for email {} (Page {}, Size {}). ", email, page, size, e);
            throw new SomethingWentWrongException();
        }
    }

    private RLeaderboardXP buildLeaderboardResponse(Long loggedUserRanking, Page<UserXP> userXPPage) {
        List<RUserXPRanking> userXPRankings = new ArrayList<>();
        int rank = userXPPage.getNumber() * userXPPage.getSize() + 1;

        for (UserXP userXP : userXPPage.getContent()) {
            User user = userXP.getUser();

            RUserXPRanking ranking = RUserXPRanking.builder()
                    .user(user)
                    .experience(userXP.getExperience())
                    .ranking((long) rank++)
                    .build();

            userXPRankings.add(ranking);
        }

        return RLeaderboardXP.builder()
                .XPRankings(userXPRankings)
                .loggedUserXPRanking(loggedUserRanking)
                .totalPages(userXPPage.getTotalPages())
                .currentPage(userXPPage.getNumber())
                .build();
    }

    private Integer getPageNumberByEmail(Long loggedUserRanking, Integer size) {
        return (int) ((loggedUserRanking - 1) / size);
    }
}