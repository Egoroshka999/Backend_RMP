# Load tester

- Конфиги в [AppConfig](/src/main/kotlin/config/AppConfig.kt)
- [TokenProvider](/src/main/kotlin/auth/TokenProvider.kt) инициализируется и получает токен для запросов, где он нужен
- Запросы - залогиниться, получить данные по сну, по активностям, добавить данные по сну, по активностям - [HealthRequest](/src/main/kotlin/models/HealthRequest.kt)
- Dto результатов тестирования - [LoadTestResult](/src/main/kotlin/models/LoadTestResult.kt)
- Рандомный генератор запросов (выборка из существующих) - [RequestGenerator](/src/main/kotlin/services/RequestGenerator.kt)
- Отправитель запросов - [RequestHandler](/src/main/kotlin/services/RequestHandler.kt)
- Сборщик статистики по запросам - [StatisticsCollector](/src/main/kotlin/services/StatisticsCollector.kt)
- Запускатор тестов - [LoadTester](/src/main/kotlin/tester/LoadTester.kt)
- Логгер в файл и LogService - [ResultLogger](/src/main/kotlin/utils/ResultLogger.kt)
