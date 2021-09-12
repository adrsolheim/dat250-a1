package no.hvl.dat250.jpa.experiment2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String street;
    private int number;
    @OneToMany(
            mappedBy = "Address",
            cascade = CascadeType.ALL
    )
    private List<Person> personList = new ArrayList<>();

    public Address() {}
    public Address(String street, int number) {
        this.street = street;
        this.number = number;
    }

    public void addPerson(Person person) {
        personList.add(person);
        person.setAddress(this);
    }

    public void removePerson(Person person) {
        personList.remove(person);
        person.setAddress(null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public List<Person> getPersonList() {
        return personList;
    }

    public void setPersonList(List<Person> personList) {
        this.personList = personList;
    }

    @Override
    public String toString() {
        return street + ", " + Integer.toString(number);
    }
}
