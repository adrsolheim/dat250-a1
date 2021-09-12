package no.hvl.dat250.jpa.experiment2;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Pincode {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String pincode;
    private int count;
    @OneToMany(
            mappedBy = "Pincode",
            cascade = CascadeType.PERSIST
    )
    private List<CreditCard> creditCardList = new ArrayList<>();

    public Pincode() { this.count = 0; }
    public Pincode(String pincode) {
        this.pincode = pincode;
        this.count = 0;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addCreditcard(CreditCard creditCard) {
        creditCardList.add(creditCard);
        creditCard.setPincode(this);
        this.count++;
    }

    public void removeCreditcard(CreditCard creditCard) {
        creditCardList.remove(creditCard);
        creditCard.setPincode(null);
        this.count--;
    }


    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<CreditCard> getCreditCardList() {
        return creditCardList;
    }

    public void setCreditCardList(List<CreditCard> creditCardList) {
        this.creditCardList = creditCardList;
    }

    @Override
    public String toString() {
        return "PIN: " + pincode;
    }

}
