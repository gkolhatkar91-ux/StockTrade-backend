package com.gaurav.trading;

import java.util.List;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TradingController {

    private final TradingService tradingService;
    private final AppUserRepository userRepository;

    public TradingController(TradingService tradingService, AppUserRepository userRepository) {
        this.tradingService = tradingService;
        this.userRepository = userRepository;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "Trading backend is running");
    }

    @GetMapping("/stocks")
    public List<TradingService.StockResponse> stocks() {
        return tradingService.stocks();
    }

    @GetMapping("/wallet")
    public TradingService.WalletResponse wallet(Authentication authentication) {
        return tradingService.wallet(currentUser(authentication));
    }

    @PostMapping("/buy")
    public TradingService.TradeResponse buy(Authentication authentication, @RequestBody TradeRequest request) {
        return tradingService.buy(currentUser(authentication), request.symbol(), request.quantity());
    }

    @PostMapping("/sell")
    public TradingService.TradeResponse sell(Authentication authentication, @RequestBody TradeRequest request) {
        return tradingService.sell(currentUser(authentication), request.symbol(), request.quantity());
    }

    @GetMapping("/portfolio")
    public Map<String, Integer> portfolio(Authentication authentication) {
        return tradingService.portfolio(currentUser(authentication));
    }

    @GetMapping("/transactions")
    public List<TradingService.TransactionResponse> transactions(Authentication authentication) {
        return tradingService.transactions(currentUser(authentication));
    }

    private AppUser currentUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new SecurityException("User was not found"));
    }

    public record TradeRequest(String symbol, int quantity) { }
}
