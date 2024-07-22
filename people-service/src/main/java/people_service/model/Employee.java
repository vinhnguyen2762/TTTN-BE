package people_service.model;

import jakarta.persistence.*;
import lombok.*;
import people_service.enums.Gender;
import people_service.enums.UserRole;

import java.time.LocalDate;

@Entity
@Table(name = "employee")
@Getter
@Setter
@NoArgsConstructor
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Gender gender;
    private String address;
    private String phoneNumber;
    @Column(unique = true)
    private String email;
    private Boolean status = false;
    private String password;
    private UserRole role = UserRole.USER;
    private Boolean locked = false;

    public Employee(String firstName, String lastName, LocalDate dateOfBirth, Gender gender, String address, String phoneNumber, String email, String password) {
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
