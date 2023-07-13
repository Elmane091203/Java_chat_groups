package dao;

import entities.Membre;

public interface IMembre extends Repository<Membre> {
    Membre getM(String username, String password);
    Membre getU(String username);
}
