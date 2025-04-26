package practica1.artefacto.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
public class Tournament {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String date;
    private String location;

    // Keep the existing teamIds for backward compatibility if needed
    @ElementCollection
    private List<Long> teamIds = new ArrayList<>();

    @ElementCollection
    private List<Long> matchIds = new ArrayList<>();
    
    // Add the many-to-many relationship
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TournamentTeam> tournamentTeams = new HashSet<>();
    
    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    private List<Match> matches = new ArrayList<>();

    // Convenience method to add a team to the tournament
    public void addTeam(Team team) {
        TournamentTeam tournamentTeam = new TournamentTeam(this, team);
        tournamentTeams.add(tournamentTeam);
    }
    
    // Convenience method to remove a team from the tournament
    public void removeTeam(Team team) {
        tournamentTeams.removeIf(tournamentTeam -> tournamentTeam.getTeam().equals(team));
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public List<Long> getTeamIds() {
        return teamIds;
    }
    public void setTeamIds(List<Long> teamIds) {
        this.teamIds = teamIds;
    }
    public List<Long> getMatchIds() {
        return matchIds;
    }
    public void setMatchIds(List<Long> matchIds) {
        this.matchIds = matchIds;
    }
    public void addTeamId(Long teamId) {
        this.teamIds.add(teamId);
    }
    public void addMatchId(Long matchId) {
        this.matchIds.add(matchId);
    }
    public void removeTeamId(Long teamId) {
        this.teamIds.remove(teamId);
    }
    public void removeMatchId(Long matchId) {
        this.matchIds.remove(matchId);
    }
    public void clearTeamIds() {
        this.teamIds.clear();
    }
    public void clearMatchIds() {
        this.matchIds.clear();
    }
    public void clear() {
        this.teamIds.clear();
        this.matchIds.clear();
    }
    public void addTeams(List<Long> teamIds) {
        this.teamIds.addAll(teamIds);
    }
    
    // New getter and setter for tournamentTeams
    public Set<TournamentTeam> getTournamentTeams() {
        return tournamentTeams;
    }
    
    public void setTournamentTeams(Set<TournamentTeam> tournamentTeams) {
        this.tournamentTeams = tournamentTeams;
    }

    // Add getter and setter for matches
    public List<Match> getMatches() {
        return matches;
    }

    public void setMatches(List<Match> matches) {
        this.matches = matches;
    }
}
