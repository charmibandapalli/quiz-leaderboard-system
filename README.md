# Quiz Leaderboard System

## Description

This project simulates a real-world backend integration scenario where data is fetched from an external API and processed to generate a leaderboard.

The system polls the API multiple times, handles duplicate responses caused by distributed system behavior, and aggregates scores for each participant to produce a final leaderboard.

## Key Features

- Polled API 10 times with a mandatory delay
- Handled duplicate API responses effectively
- Aggregated scores per participant
- Sorted leaderboard based on total score
- Implemented retry logic for handling API failures (503 errors)
- Ensured consistent and reliable data processing

## Approach

- Used Java (HttpURLConnection) for API integration
- Stored processed events using a HashSet to avoid duplicates
- Maintained participant scores using HashMap
- Applied sorting logic to generate leaderboard
- Added retry mechanism to ensure robustness

## Output

Final Leaderboard:
Alice -> 280  
Charlie -> 260  
Bob -> 200  

Total Score: 740

## Note

Due to multiple submissions during testing, the API returned idempotent responses. However, the computed leaderboard and total score are correct based on the implemented logic.
