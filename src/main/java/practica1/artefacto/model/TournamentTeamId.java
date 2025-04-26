package practica1.artefacto.model;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class TournamentTeamId implements Serializable {
    
    private Long tournamentId;
    private Long teamId;
    
    // Default constructor
    public TournamentTeamId() {}
    
    // Constructor with fields
    public TournamentTeamId(Long tournamentId, Long teamId) {
        this.tournamentId = tournamentId;
        this.teamId = teamId;
    }
    
    // Getters and setters
    public Long getTournamentId() {
        return tournamentId;
    }
    
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }
    
    public Long getTeamId() {
        return teamId;
    }
    
    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }
    
    // Equals and hashCode methods for composite keys
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TournamentTeamId that = (TournamentTeamId) o;
        return Objects.equals(tournamentId, that.tournamentId) &&
               Objects.equals(teamId, that.teamId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(tournamentId, teamId);
    }
}
