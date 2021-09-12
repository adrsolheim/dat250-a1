package no.hvl.dat250.jpa.experiment2;

import javax.persistence.*;

@Entity
public class CreditCard {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private int number;
    private int limit;
    private int balance;
    @ManyToOne
    private Person person;
    @ManyToOne
    private Pincode pincode;
    @ManyToOne
    private Bank bank;

    public CreditCard() {}
    public CreditCard(int number, int limit, int balance) {
        this.number = number;
        this.limit = limit;
        this.balance = balance;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Pincode getPincode() {
        return pincode;
    }

    public void setPincode(Pincode pincode) {
        this.pincode = pincode;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }

    @Override
    public String toString() {
        return "(" +
                Long.toString(id)+ ", " +
                Integer.toString(number) + ", " +
                Integer.toString(limit) + ", " +
                Integer.toString(balance) + ", " +
                pincode +
                ")";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CreditCard )) return false;
        return id != null && id.equals(((CreditCard) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
