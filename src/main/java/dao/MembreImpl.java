package dao;

import config.HibernateUtil;
import entities.Membre;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class MembreImpl implements IMembre{
    private Session session;
    private Transaction transaction;

    public MembreImpl() {
        this.session = HibernateUtil.getSessionFactory().openSession();
    }

    @Override
    public int create(Membre membre) {
        int ok = 0;
        try {
            transaction = session.beginTransaction();
            membre.setPassword(Membre.Hash(membre.getPassword()));
            session.save(membre);
            transaction.commit();
            ok = 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    @Override
    public List<Membre> getAll() {
        return session.createCriteria(Membre.class).list();
    }

    @Override
    public Membre get(int id) {
        return session.get(Membre.class,id);
    }

    @Override
    public int update(Membre membre) {
        int ok = 0;
        try {
            transaction = session.beginTransaction();
            session.merge(membre);
            transaction.commit();
            ok = 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    @Override
    public int delete(int id) {
        int ok = 0;
        try {
            transaction = session.beginTransaction();
            session.delete(get(id));
            transaction.commit();
            ok = 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    @Override
    public Membre getM(String username, String password) {
        Membre membre = null;
        try {
            String hqlQuery = "FROM Membre WHERE username = :valeur1 AND password = :valeur2";
            Query query = session.createQuery(hqlQuery);
            query.setParameter("valeur1", username);
            query.setParameter("valeur2", Membre.Hash(password));
            membre = (Membre) query.uniqueResult();
        }catch (Exception e){
            e.printStackTrace();
        }
        return membre;
    }

    @Override
    public Membre getU(String username) {
        Membre membre = null;
        try {
            String hqlQuery = "FROM Membre WHERE username = :valeur1";
            Query query = session.createQuery(hqlQuery);
            query.setParameter("valeur1", username);
            membre = (Membre) query.uniqueResult();
        }catch (Exception e){
            e.printStackTrace();
        }
        return membre;
    }
}
