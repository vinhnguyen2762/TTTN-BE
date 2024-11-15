package people_service.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class ConfirmationTokenCustomer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String token;
    @Column(nullable = false)
    private LocalDateTime createdAt;
    @Column(nullable = false)
    private LocalDateTime expiresAt;
    private LocalDateTime confirmedAt;

    @ManyToOne
    @JoinColumn(nullable = false, name = "customer_id")
    private Customer customer;

    public ConfirmationTokenCustomer(String token, LocalDateTime createdAt, LocalDateTime expiresAt, Customer customer) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.customer = customer;
    }
}
