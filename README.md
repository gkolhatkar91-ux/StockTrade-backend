# Paper Trading Backend

Java 21, Spring Boot, Spring Security, JWT, and MySQL based paper-trading backend.

## Before running

Set `DB_PASSWORD` in the IntelliJ run configuration to the local MySQL password. The database is `trading_db`.

## Public endpoints

- `GET /api/health`
- `GET /api/stocks`
- `POST /api/auth/register`
- `POST /api/auth/login`

## Protected endpoints

Supply `Authorization: Bearer <token>`.

- `GET /api/wallet`
- `POST /api/buy` with `{ "symbol": "TCS", "quantity": 1 }`
- `POST /api/sell` with `{ "symbol": "TCS", "quantity": 1 }`
- `GET /api/portfolio`
- `GET /api/transactions`

This is a paper-trading learning project. Prices are demo values, not live market data, and it must not be used for real-money trading.
