package dao;

import config.HibernateUtil;
import entities.Commentaire;
import entities.Membre;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class CommentaireImpl implements ICommentaire{
    private Session session;
    private Transaction transaction;

    public CommentaireImpl() {
        session = HibernateUtil.getSessionFactory().openSession();
    }
    @Override
    public int create(Commentaire commentaire) {
        int ok = 0;
        try {
            transaction = session.beginTransaction();
            session.save(commentaire);
            transaction.commit();
            ok = 1;
        }catch (Exception e){
            e.printStackTrace();
        }
        return ok;
    }

    @Override
    public List<Commentaire> getAll() {
        return session.createCriteria(Commentaire.class).list();
    }

    @Override
    public Commentaire get(int id) {
        return session.get(Commentaire.class,id);
    }

    @Override
    public int update(Commentaire commentaire) {
        int ok = 0;
        try {
            transaction = session.beginTransaction();
            session.merge(commentaire);
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
    public List<Commentaire> getCM(Membre membre) {
        List<Commentaire> commentaires = null;
        try {
            transaction = session.beginTransaction();
            String hqlQuery = "FROM Commentaire WHERE membre=:membreId";
            Query query = session.createQuery(hqlQuery);
            query.setParameter("membreId", membre);
            commentaires = query.list();
        }catch (Exception e){
            e.printStackTrace();
        }
        return commentaires;
    }
}
