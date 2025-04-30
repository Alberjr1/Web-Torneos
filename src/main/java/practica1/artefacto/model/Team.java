package practica1.artefacto.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
public class Team {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String coach;
    private String badge;
    
    //many-to-many relationship
    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TournamentTeam> teamTournaments = new HashSet<>();

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
    public String getCoach() {
        return coach;
        }
    public void setCoach(String coach) {
        this.coach = coach;
    }
    public String getBadge() {
        return badge;
    }
    public void setBadge(String badge) {
        this.badge = badge;
    }

    // New getter and setter for teamTournaments
    public Set<TournamentTeam> getTeamTournaments() {
        return teamTournaments;
    }
    
    public void setTeamTournaments(Set<TournamentTeam> teamTournaments) {
        this.teamTournaments = teamTournaments;
    }
}
