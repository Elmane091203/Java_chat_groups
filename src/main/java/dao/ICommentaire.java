package dao;

import entities.Commentaire;
import entities.Membre;

import java.util.List;

public interface ICommentaire extends Repository<Commentaire> {

    List<Commentaire> getCM(Membre membre);
}
