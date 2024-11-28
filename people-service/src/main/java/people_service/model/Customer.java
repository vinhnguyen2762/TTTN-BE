package people_service.model;

import jakarta.persistence.*;
import lombok.*;
import people_service.enums.Gender;

import java.time.LocalDate;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String email;
    private String phoneNumber;
    private Boolean status = true;
    private Long smallTraderId;

    public Customer(String firstName, String lastName, LocalDate dateOfBirth, Gender gender, String address, String phoneNumber, String email, Long smallTraderId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.smallTraderId = smallTraderId;
    }
}
