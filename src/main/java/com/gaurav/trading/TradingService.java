package com.gaurav.trading;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TradingService {

    private static final Map<String, BigDecimal> PRICES = Map.of(
            "TCS", new BigDecimal("3150.00"),
            "RELIANCE", new BigDecimal("1425.50"),
            "INFY", new BigDecimal("1480.25")
    );

    private final WalletRepository walletRepository;
    private final HoldingRepository holdingRepository;
    private final TradeTransactionRepository transactionRepository;

    public TradingService(WalletRepository walletRepository, HoldingRepository holdingRepository,
                          TradeTransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.holdingRepository = holdingRepository;
        this.transactionRepository = transactionRepository;
    }

    public List<StockResponse> stocks() {
        return List.of(
                new StockResponse("TCS", "Tata Consultancy Services", PRICES.get("TCS")),
                new StockResponse("RELIANCE", "Reliance Industries", PRICES.get("RELIANCE")),
                new StockResponse("INFY", "Infosys", PRICES.get("INFY"))
        );
    }

    public WalletResponse wallet(AppUser user) {
        return new WalletResponse(user.getUsername(), getWallet(user).getBalance());
    }

    @Transactional
    public TradeResponse buy(AppUser user, String symbol, int quantity) {
        String stockSymbol = normalizedSymbol(symbol);
        validateQuantity(quantity);
        BigDecimal price = priceFor(stockSymbol);
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantity));
        Wallet wallet = getWallet(user);

        if (wallet.getBalance().compareTo(totalAmount) < 0) {
            throw new IllegalArgumentException("Insufficient wallet balance");
        }

        wallet.setBalance(wallet.getBalance().subtract(totalAmount));
        Holding holding = holdingRepository.findByUserAndSymbol(user, stockSymbol)
                .orElse(new Holding(user, stockSymbol, 0));
        holding.setQuantity(holding.getQuantity() + quantity);

        walletRepository.save(wallet);
        holdingRepository.save(holding);
        transactionRepository.save(new TradeTransaction(user, TradeType.BUY, stockSymbol, quantity, price, totalAmount));
        return new TradeResponse("BUY successful", stockSymbol, quantity, price, totalAmount, wallet.getBalance());
    }

    @Transactional
    public TradeResponse sell(AppUser user, String symbol, int quantity) {
        String stockSymbol = normalizedSymbol(symbol);
        validateQuantity(quantity);
        Holding holding = holdingRepository.findByUserAndSymbol(user, stockSymbol)
                .orElseThrow(() -> new IllegalArgumentException("You do not own this stock"));

        if (holding.getQuantity() < quantity) {
            throw new IllegalArgumentException("You do not own enough shares to sell");
        }

        BigDecimal price = priceFor(stockSymbol);
        BigDecimal totalAmount = price.multiply(BigDecimal.valueOf(quantity));
        Wallet wallet = getWallet(user);
        wallet.setBalance(wallet.getBalance().add(totalAmount));

        if (holding.getQuantity() == quantity) {
            holdingRepository.delete(holding);
        } else {
            holding.setQuantity(holding.getQuantity() - quantity);
            holdingRepository.save(holding);
        }

        walletRepository.save(wallet);
        transactionRepository.save(new TradeTransaction(user, TradeType.SELL, stockSymbol, quantity, price, totalAmount));
        return new TradeResponse("SELL successful", stockSymbol, quantity, price, totalAmount, wallet.getBalance());
    }

    public Map<String, Integer> portfolio(AppUser user) {
        Map<String, Integer> portfolio = new LinkedHashMap<>();
        for (Holding holding : holdingRepository.findByUser(user)) {
            portfolio.put(holding.getSymbol(), holding.getQuantity());
        }
        return portfolio;
    }

    public List<TransactionResponse> transactions(AppUser user) {
        return transactionRepository.findByUserOrderByCreatedAtDesc(user).stream()
                .map(transaction -> new TransactionResponse(
                        transaction.getId(), transaction.getType(), transaction.getSymbol(), transaction.getQuantity(),
                        transaction.getPricePerShare(), transaction.getTotalAmount(), transaction.getCreatedAt()
                ))
                .toList();
    }

    private Wallet getWallet(AppUser user) {
        return walletRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Wallet was not found"));
    }

    private String normalizedSymbol(String symbol) {
        if (symbol == null) throw new IllegalArgumentException("Stock symbol is required");
        return symbol.trim().toUpperCase();
    }

    private BigDecimal priceFor(String symbol) {
        BigDecimal price = PRICES.get(symbol);
        if (price == null) throw new IllegalArgumentException("Stock not found");
        return price;
    }

    private void validateQuantity(int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Quantity must be greater than zero");
    }

    public record StockResponse(String symbol, String name, BigDecimal price) { }
    public record WalletResponse(String username, BigDecimal balance) { }
    public record TradeResponse(String message, String symbol, int quantity, BigDecimal pricePerShare,
                                BigDecimal totalAmount, BigDecimal remainingBalance) { }
    public record TransactionResponse(Long id, TradeType type, String symbol, int quantity,
                                      BigDecimal pricePerShare, BigDecimal totalAmount, java.time.Instant createdAt) { }
}
