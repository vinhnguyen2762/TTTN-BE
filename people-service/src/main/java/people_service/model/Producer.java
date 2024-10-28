package people_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import people_service.enums.Gender;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "producer")
@Getter
@Setter
@NoArgsConstructor
public class Producer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private Gender gender;
    private String address;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String phoneNumber;
    private Long smallTraderId;
    private Boolean status = true;
    @OneToMany(mappedBy = "producer", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<DebtDetail> debtDetails = new ArrayList<>();

    public Producer(String firstName, String lastName, Gender gender, String address, String phoneNumber, String email, Long smallTraderId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.smallTraderId = smallTraderId;
    }
}
