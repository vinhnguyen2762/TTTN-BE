package people_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import people_service.enums.EmployeeRole;
import people_service.enums.Gender;

import java.time.LocalDate;

import static people_service.utils.PasswordHashing.hashPassword;

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
    private String email;
    private String phoneNumber;
    private Boolean status = true;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "small_trader_id")
    private SmallTrader smallTrader;
    private String password;
    private Boolean locked = false;
    private EmployeeRole role = EmployeeRole.EMPLOYEE;

    public Employee(String firstName, String lastName, LocalDate dateOfBirth, Gender gender, String address, String phoneNumber, String email, SmallTrader smallTrader) {
        String hashedPassword = hashPassword("123");

        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.gender = gender;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.smallTrader = smallTrader;
        this.password = hashedPassword;
    }
}
