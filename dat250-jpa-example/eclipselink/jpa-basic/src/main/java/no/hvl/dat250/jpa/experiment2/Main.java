package no.hvl.dat250.jpa.experiment2;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import java.util.List;

public class Main {
    private static final String PERSISTENCE_UNIT_NAME = "banksystem";
    private static EntityManagerFactory factory;

    public static void main(String[] args) {
        factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
        EntityManager em = factory.createEntityManager();
        //resetDB(em);
        experiment2(em);
    }

    public static void resetDB(EntityManager em) {
        System.out.println("Removing banks from database..");
        removeBanks(em);
        System.out.println("Removing clients from database..");
        removeClients(em);
        em.close();
    }

    public static void experiment2(EntityManager em) {

        Query q = em.createQuery("select b from Bank b");
        List<Bank> bankList = q.getResultList();
        if (bankList.size() == 0) {
            System.out.println("No bank registered in database. Setting up a bank for testing..");
            System.out.println();
            createBank(em);
            bankList = q.getResultList();
        }
        for (Bank bank : bankList) {
            System.out.println(bank);
        }
        System.out.println("\n");

        q = em.createQuery("select p from Person p");
        List<Person> personList = q.getResultList();
        if (personList.size() == 0) {
            System.out.println("No clients registered in database. Setting up clients for testing..");
            System.out.println();
            createClients(em);
            personList = q.getResultList();
        }
        for (Person person : personList) {
            System.out.println(person);
        }
        em.close();
    }

    public static void createBank(EntityManager em) {
        em.getTransaction().begin();
        CreditCard creditCard1 = new CreditCard(1, 5000, 0);
        CreditCard creditCard2 = new CreditCard(2, 10000, 0);
        Pincode pincode = new Pincode("123");
        em.persist(pincode);
        Pincode pincode2 = new Pincode("123");
        em.persist(pincode2);
        creditCard1.setPincode(pincode);
        creditCard2.setPincode(pincode2);
        Bank bank = new Bank("Nordea");
        bank.addCreditcard(creditCard1);
        bank.addCreditcard(creditCard2);
        em.persist(bank);
        em.getTransaction().commit();
    }

    public static void removeBanks(EntityManager em) {
        em.getTransaction().begin();
        Query q = em.createQuery("select b from Bank b");
        List<Bank> bankList = q.getResultList();
        for (Bank bank : bankList) {
            em.remove(bank);
        }
        em.getTransaction().commit();
    }

    public static void createClients(EntityManager em) {
        Query q = em.createQuery("select c from CreditCard c where c.number=:n");
        q.setParameter("n", 1);
        CreditCard creditCard = (CreditCard) q.getSingleResult();
        em.getTransaction().begin();
        Person bob = new Person("Bob");
        Address address = new Address("Inndalsveien", 28);
        address.addPerson(bob);
        bob.addCreditcard(creditCard);

        em.persist(address);
        em.persist(bob);
        em.getTransaction().commit();
    }

    public static void removeClients(EntityManager em) {
        em.getTransaction().begin();
        Query q = em.createQuery("select p from Person p");
        List<Person> personList = q.getResultList();
        for (Person person : personList) {
            em.remove(person);
        }
        em.getTransaction().commit();
    }
}
