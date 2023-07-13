package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "commentaire_tb")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Commentaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idC;

    @Column(name = "message",length = 255)
    private String message;

    @Column(name = "dateC")
    private LocalDateTime dateC;
    @ManyToOne
    @JoinColumn(name = "membre")
    private Membre membre;

}
