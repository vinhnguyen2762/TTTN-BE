package people_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "supplier")
@Getter
@Setter
@NoArgsConstructor
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String firstName;
    private String lastName;
    private String address;
    private String email;
    private String phoneNumber;
    private String taxId;
    private Boolean status = true;
    private Long smallTraderId;

    public Supplier(String firstName, String lastName, String address, String email, String phoneNumber, String taxId, Long smallTraderId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.taxId = taxId;
        this.smallTraderId = smallTraderId;
    }
}
