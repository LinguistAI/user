package app.linguistai.bmvp.service.gamification;

import app.linguistai.bmvp.exception.NotFoundException;
import app.linguistai.bmvp.exception.SomethingWentWrongException;
import app.linguistai.bmvp.model.User;
import app.linguistai.bmvp.model.gamification.UserXP;
import app.linguistai.bmvp.repository.IAccountRepository;
import app.linguistai.bmvp.repository.gamification.IUserXPRepository;
import app.linguistai.bmvp.response.gamification.RLeaderboardXP;
import app.linguistai.bmvp.response.gamification.RUserXPRanking;
import app.linguistai.bmvp.consts.LeaderboardConsts;
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
            // Fetch the global XP ranking for the logged-in user
            Long loggedUserRanking = xpRepository.findGlobalUserRankByEmail(email);
            if (loggedUserRanking == null) {
                throw new NotFoundException("User's global XP ranking", true);
            }
            // If page number is not given, then return the page where the user is present
            if (page == null) {
                page = getPageNumberByRanking(loggedUserRanking, size);
            }

            // Create pageable object with the given page number and size for pagination
            Pageable pageable = PageRequest.of(page, size, Sort.by(LeaderboardConsts.XP_SORT_FIELD).descending());
            // Fetch page of user XP records sorted by experience
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
            // Fetch user details based on email
            User loggedUser = accountRepository.findUserByEmail(email).orElse(null);
            if (loggedUser == null) {
                throw new NotFoundException(User.class.getSimpleName(), true);
            }

            // Fetch the ranking of the logged-in user among friends
            Long loggedUserRanking = xpRepository.findFriendsUserRankByEmail(loggedUser.getId());
            if (loggedUserRanking == null) {
                throw new NotFoundException("User's XP ranking among friends", true);
            }
            // If page number is not given, then return the page where the user is present
            if (page == null) {
                page = getPageNumberByRanking(loggedUserRanking, size);
            }

            // Create pageable object with the given page number and size for pagination
            Pageable pageable = PageRequest.of(page, size);
            // Fetch page of friends' user XP records sorted by experience
            Page<UserXP> userXPPage = xpRepository.findTopFriendsByExperience(loggedUser.getId(), pageable);

            // Build response object
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

    // Helper method to build the leaderboard response
    private RLeaderboardXP buildLeaderboardResponse(Long loggedUserRanking, Page<UserXP> userXPPage) {
        List<RUserXPRanking> userXPRankings = new ArrayList<>();
        // Calculate the first user on the given page's rank by "number of previous users + 1" = page no * page size + 1
        int rank = userXPPage.getNumber() * userXPPage.getSize() + 1;

        // Iterate through user XP records of the given page to build ranking response objects
        for (UserXP userXP : userXPPage.getContent()) {
            User user = userXP.getUser();

            RUserXPRanking ranking = RUserXPRanking.builder()
                    .user(user)
                    .experience(userXP.getExperience())
                    .ranking((long) rank++)
                    .build();

            userXPRankings.add(ranking);
        }

        // Construct and return the leaderboard response
        return RLeaderboardXP.builder()
                .XPRankings(userXPRankings)
                .loggedUserXPRanking(loggedUserRanking)
                .totalPages(userXPPage.getTotalPages())
                .currentPage(userXPPage.getNumber())
                .build();
    }

    // Helper method to calculate page number based on ranking
    private Integer getPageNumberByRanking(Long loggedUserRanking, Integer size) {
        return (int) ((loggedUserRanking - 1) / size);
    }
}