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
public class ConfirmationToken {
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
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "small_trader_id", referencedColumnName = "id")
    private SmallTrader smallTrader;

    public ConfirmationToken(String token, LocalDateTime createdAt, LocalDateTime expiresAt, SmallTrader smallTrader) {
        this.token = token;
        this.createdAt = createdAt;
        this.expiresAt = expiresAt;
        this.smallTrader = smallTrader;
    }
}
