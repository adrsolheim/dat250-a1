package no.hvl.dat250.jpa.experiment2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    @ManyToOne
    private Address address;
    // all the JPA entity state transitions (e.g., persist, merge, remove)
    // are passed from the parent entity to child entities.
    @OneToMany(
            mappedBy = "Person",
            // all the JPA entity state transitions (e.g., persist, merge, remove)
            // are passed from the parent entity to child entities.
            cascade = CascadeType.ALL,
            // remove creditcards when they are no longer referenced by a person
            orphanRemoval = true
    )
    private List<CreditCard> creditCardList = new ArrayList<>();

    public Person() {}
    public Person(String name) {
        this.name = name;
    }

    public void addCreditcard(CreditCard creditCard) {
        creditCardList.add(creditCard);
        creditCard.setPerson(this);
    }

    public void removeCreditcard(CreditCard creditCard) {
        creditCardList.remove(creditCard);
        creditCard.setPerson(null);
    }

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

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public List<CreditCard> getCreditCardList() {
        return creditCardList;
    }

    public void setCreditCardList(List<CreditCard> creditCardList) {
        this.creditCardList = creditCardList;
    }

    @Override
    public String toString() {
        return name + " living at: " + address + "\nCredit cards: " + creditCardList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Person )) return false;
        return id != null && id.equals(((Person) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
