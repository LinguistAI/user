package app.linguistai.bmvp.model.gamification.store;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@Entity
@Table(name = "store_item")
@NoArgsConstructor
@AllArgsConstructor
public class StoreItem {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @NotBlank
    @Column(name = "type", nullable = false, unique = true)
    private String type;

    @NotBlank
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull
    @Column(name = "gem_price", nullable = false)
    private Long gemPrice;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    public StoreItem(String type, String description, Long gemPrice, boolean enabled) {
        this.type = type;
        this.description = description;
        this.gemPrice = gemPrice;
        this.enabled = enabled;
    }
}