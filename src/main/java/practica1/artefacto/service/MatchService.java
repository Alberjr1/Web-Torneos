package practica1.artefacto.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practica1.artefacto.model.Match;
import practica1.artefacto.model.Team;
import practica1.artefacto.model.Tournament;
import practica1.artefacto.repository.MatchRepository;
import practica1.artefacto.repository.TeamRepository;
import practica1.artefacto.repository.TournamentRepository;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;
    
    @Autowired
    private TeamRepository teamRepository;
    
    @Autowired
    private TournamentRepository tournamentRepository;

    public Match create(Match match) {
        // Validate date - prevent matches with dates in the past
        if (match.getDate() != null && !match.getDate().isEmpty()) {
            LocalDate matchDate = LocalDate.parse(match.getDate(), DateTimeFormatter.ISO_DATE);
            LocalDate today = LocalDate.now();
            
            if (matchDate.isBefore(today)) {
                throw new IllegalArgumentException("Cannot create a match with a date in the past. Match date: " 
                        + match.getDate() + ", Current date: " + today.format(DateTimeFormatter.ISO_DATE));
            }
        }
        
        // Validate that both teams exist
        Team team1 = teamRepository.findById(match.getTeam1Id())
            .orElseThrow(() -> new IllegalArgumentException("Team 1 with ID " + match.getTeam1Id() + " not found"));
        
        Team team2 = teamRepository.findById(match.getTeam2Id())
            .orElseThrow(() -> new IllegalArgumentException("Team 2 with ID " + match.getTeam2Id() + " not found"));
        
        // Save the match
        Match savedMatch = matchRepository.save(match);
        
        // Update the tournament with the match ID and team IDs
        if (match.getTournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(match.getTournamentId())
                .orElseThrow(() -> new IllegalArgumentException("Tournament with ID " + match.getTournamentId() + " not found"));
            
            // Add match ID to tournament
            tournament.addMatchId(savedMatch.getId());
            
            // Add team IDs to tournament if not already present
            if (!tournament.getTeamIds().contains(team1.getId())) {
                tournament.addTeamId(team1.getId());
            }
            
            if (!tournament.getTeamIds().contains(team2.getId())) {
                tournament.addTeamId(team2.getId());
            }
            
            // Save updated tournament
            tournamentRepository.save(tournament);
        }
        
        return savedMatch;
    }

    public Match read(Long id) {
        return matchRepository.findById(id).orElse(null);
    }

    public Match update(Long id, Match match) {
        match.setId(id);
        return matchRepository.save(match);
    }

    public void delete(Long id) {
        // First get the match to access its tournament ID
        Match match = matchRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Match not found: " + id));
        
        // If the match has a tournament, update the tournament
        if (match.getTournamentId() != null) {
            Tournament tournament = tournamentRepository.findById(match.getTournamentId())
                .orElse(null);
            
            if (tournament != null) {
                Long tournamentId = tournament.getId();
                Long team1Id = match.getTeam1Id();
                Long team2Id = match.getTeam2Id();
                
                // Remove the match ID from the tournament's match IDs list
                tournament.removeMatchId(id);
                
                // After removing this match, check if team1 and team2 are still used in any other matches
                List<Match> remainingMatches = matchRepository.findByTournamentId(tournamentId);
                
                // Remove the current match from consideration (it's being deleted)
                remainingMatches.removeIf(m -> m.getId().equals(id));
                
                // Check if team1 is still used in any remaining match
                boolean team1StillUsed = remainingMatches.stream()
                    .anyMatch(m -> team1Id.equals(m.getTeam1Id()) || team1Id.equals(m.getTeam2Id()));
                
                // Check if team2 is still used in any remaining match
                boolean team2StillUsed = remainingMatches.stream()
                    .anyMatch(m -> team2Id.equals(m.getTeam1Id()) || team2Id.equals(m.getTeam2Id()));
                
                // Remove team1 from tournament if no longer used
                if (!team1StillUsed) {
                    tournament.removeTeamId(team1Id);
                }
                
                // Remove team2 from tournament if no longer used
                if (!team2StillUsed) {
                    tournament.removeTeamId(team2Id);
                }
                
                tournamentRepository.save(tournament);
            }
        }
        
        // Now delete the match
        matchRepository.deleteById(id);
    }

    public List<Match> getAll() {
        return matchRepository.findAll();
    }

    // This method allows partial updates to a Match object
    // by accepting a map of field names and their new values.

    public Match patch(Long id, Map<String, Object> updates) {
        Match match = matchRepository.findById(id).orElse(null);
        if (match != null) {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "date" -> match.setDate((String) value);
                    case "time" -> match.setTime((String) value);
                    case "team1Id" -> match.setTeam1Id((Long) value);
                    case "team2Id" -> match.setTeam2Id((Long) value);
                    case "tournamentId" -> match.setTournamentId((Long) value);
                }
            });
            matchRepository.save(match);
        }
        return match;
    }
    // This method retrieves all matches for a specific tournament
    // by filtering the matches map based on the tournamentId.
    
    public List<Match> getMatchesByTournamentId(Long tournamentId) {
        return matchRepository.findByTournamentId(tournamentId);
    }

    public Match getMatchWithTeams(Long id) {
        Match match = matchRepository.findById(id).orElse(null);
        if (match != null) {
            enrichMatchWithTeams(match);
        }
        return match;
    }

    public List<Match> getAllWithTeams() {
        List<Match> matches = matchRepository.findAll();
        matches.forEach(this::enrichMatchWithTeams);
        return matches;
    }

    private void enrichMatchWithTeams(Match match) {
        if (match.getTeam1Id() != null) {
            teamRepository.findById(match.getTeam1Id()).ifPresent(match::setTeam1);
        }
        if (match.getTeam2Id() != null) {
            teamRepository.findById(match.getTeam2Id()).ifPresent(match::setTeam2);
        }
    }

}
