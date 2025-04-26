package practica1.artefacto.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;

@Entity
public class Match {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String date;
    private String time;
    
    @Column(name = "team1_id")
    private Long team1Id;
    
    @Column(name = "team2_id")
    private Long team2Id;
    
    @Column(name = "tournament_id")
    private Long tournamentId;
    
    // Add JPA relationship with Tournament
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", insertable = false, updatable = false)
    @JsonIgnore
    private Tournament tournament;
    
    // Add transient fields for full team objects
    @Transient
    @JsonIgnore
    private Team team1;
    
    @Transient
    @JsonIgnore
    private Team team2;

    // Getters and Setters
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public Long getTeam1Id() {
        return team1Id;
    }
    public void setTeam1Id(Long team1Id) {
        this.team1Id = team1Id;
    }
    public Long getTeam2Id() {
        return team2Id;
    }
    public void setTeam2Id(Long team2Id) {
        this.team2Id = team2Id;
    }
    public Long getTournamentId() {
        return tournamentId;
    }
    public void setTournamentId(Long tournamentId) {
        this.tournamentId = tournamentId;
    }

    // Add new getters and setters for team objects
    public Team getTeam1() {
        return team1;
    }
    
    public void setTeam1(Team team1) {
        this.team1 = team1;
        if (team1 != null) {
            this.team1Id = team1.getId();
        }
    }
    
    public Team getTeam2() {
        return team2;
    }
    
    public void setTeam2(Team team2) {
        this.team2 = team2;
        if (team2 != null) {
            this.team2Id = team2.getId();
        }
    }

    // Add getter and setter for tournament
    public Tournament getTournament() {
        return tournament;
    }
    
    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
        if (tournament != null) {
            this.tournamentId = tournament.getId();
        }
    }
}
