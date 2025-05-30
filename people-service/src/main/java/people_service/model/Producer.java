//package people_service.model;
//
//import jakarta.persistence.*;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//import people_service.enums.Gender;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.List;
//
//@Entity
//@Table(name = "debtor")
//@Getter
//@Setter
//@NoArgsConstructor
//public class Producer {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String firstName;
//    private String lastName;
//    private Gender gender;
//    private String address;
//    private String email;
//    private String phoneNumber;
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "small_trader_id")
//    private SmallTrader smallTrader;
//    private Boolean status = true;
//    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<DebtDetail> debtDetails = new ArrayList<>();
//
//    public Producer(String firstName, String lastName, Gender gender, String address, String phoneNumber, String email, SmallTrader smallTrader) {
//        this.firstName = firstName;
//        this.lastName = lastName;
//        this.gender = gender;
//        this.address = address;
//        this.phoneNumber = phoneNumber;
//        this.email = email;
//        this.smallTrader = smallTrader;
//    }
//}
