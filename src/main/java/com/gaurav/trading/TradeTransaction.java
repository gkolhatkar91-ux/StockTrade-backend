package com.gaurav.trading;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trade_transactions")
public class TradeTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private AppUser user;

    @Enumerated(EnumType.STRING)
    private TradeType type;

    private String symbol;
    private int quantity;
    private BigDecimal pricePerShare;
    private BigDecimal totalAmount;
    private Instant createdAt;

    protected TradeTransaction() {
    }

    public TradeTransaction(AppUser user, TradeType type, String symbol, int quantity,
                            BigDecimal pricePerShare, BigDecimal totalAmount) {
        this.user = user;
        this.type = type;
        this.symbol = symbol;
        this.quantity = quantity;
        this.pricePerShare = pricePerShare;
        this.totalAmount = totalAmount;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public TradeType getType() { return type; }
    public String getSymbol() { return symbol; }
    public int getQuantity() { return quantity; }
    public BigDecimal getPricePerShare() { return pricePerShare; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public Instant getCreatedAt() { return createdAt; }
}
