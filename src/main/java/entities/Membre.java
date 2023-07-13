package entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Entity
@Table(name = "membre_tb")
@Getter
@Setter
@NoArgsConstructor
public class Membre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idM;

    @Column(name = "username",unique = true, length = 200)
    private String username;

    @Column(name = "password", length = 200)
    private String password;

    @OneToMany(mappedBy = "membre")
    private List<Commentaire> commentaires;

    public static String Hash(String mdp){

        try {
            // Création d'une instance de MessageDigest avec l'algorithme MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // Conversion de la chaîne de caractères en tableau de bytes
            byte[] inputBytes = mdp.getBytes();

            // Calcul du hash MD5 des bytes
            byte[] hashBytes = md.digest(inputBytes);

            // Conversion du hash en représentation hexadécimale
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xFF & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
//            Mot de passe cripter MD5
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return mdp;
    }
}
