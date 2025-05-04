package practica1.artefacto.model;

import jakarta.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "tournament_team")
public class TournamentTeam implements Serializable {
    
    @EmbeddedId
    private TournamentTeamId id;
    
    @ManyToOne
    @MapsId("tournamentId")
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    
    @ManyToOne
    @MapsId("teamId")
    @JoinColumn(name = "team_id")
    private Team team;
    
    private String registrationDate;
    
    public TournamentTeam() {}
    
    public TournamentTeam(Tournament tournament, Team team) {
        this.tournament = tournament;
        this.team = team;
        this.id = new TournamentTeamId(tournament.getId(), team.getId());
    }
    
    public TournamentTeamId getId() {
        return id;
    }
    
    public void setId(TournamentTeamId id) {
        this.id = id;
    }
    
    public Tournament getTournament() {
        return tournament;
    }
    
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }
    
    public Team getTeam() {
        return team;
    }
    
    public void setTeam(Team team) {
        this.team = team;
    }
    
    public String getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }
}
