package no.hvl.dat250.jpa.experiment2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @OneToMany(
            mappedBy = "Bank",
            // when loading a bank don't load all its creditcards
            // *unless* getCreditcards() is specifically called (performance)
            fetch = FetchType.EAGER,
            // all the JPA entity state transitions (e.g., persist, merge, remove)
            // are passed from the parent entity to child entities.
            cascade = CascadeType.ALL,
            // remove creditcards when they are no longer referenced by a person
            orphanRemoval = true
    )
    private List<CreditCard> creditCardList;

    public Bank() {}

    public Bank(String name) {
        this.name = name;
        creditCardList = new ArrayList<>();
    }

    public void addCreditcard(CreditCard creditCard) {
        creditCardList.add(creditCard);
        creditCard.setBank(this);
    }

    public void removeCreditcard(CreditCard creditCard) {
        creditCardList.remove(creditCard);
        creditCard.setBank(null);
    }

    @Override
    public String toString() {
        return "Bank [name=" + name + ", creditCards=" + creditCardList
                + "]";
    }
}
