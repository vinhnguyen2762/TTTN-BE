package people_service.model;

import jakarta.persistence.*;
import lombok.*;
import people_service.enums.Gender;
import people_service.enums.SmallTraderRole;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "small_trader")
@Getter
@Setter
@NoArgsConstructor
public class SmallTrader {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    @Column(unique = true)
    private String phoneNumber;
    @Column(unique = true)
    private String email;
    private Boolean status = false;
    private String password;
    private SmallTraderRole role = SmallTraderRole.USER;
    private Boolean locked = false;
//    @OneToOne(mappedBy = "smallTrader")
//    private ConfirmationToken confirmationToken;
    @OneToMany(mappedBy = "smallTrader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<Producer> producerList = new ArrayList<>();
//    @OneToMany(mappedBy = "smallTrader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Supplier> supplierList = new ArrayList<>();
    @OneToMany(mappedBy = "smallTrader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Customer> customerList = new ArrayList<>();
    @OneToMany(mappedBy = "smallTrader", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Employee> employeeList = new ArrayList<>();

    public SmallTrader(String firstName, String lastName, LocalDate dateOfBirth, Gender gender, String address, String phoneNumber, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.password = password;
    }
}
