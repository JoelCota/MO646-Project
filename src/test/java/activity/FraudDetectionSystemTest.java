package activity;

import activity.FraudDetectionSystem.Transaction;
import activity.FraudDetectionSystem.FraudCheckResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FraudDetectionSystemTest {

    // 1. Prueba de transacción con monto alto
    @Test
    void testHighTransactionAmount() {
        FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
        Transaction currentTransaction = new Transaction(15000, LocalDateTime.now(), "USA");
        List<Transaction> previousTransactions = new ArrayList<>();
        List<String> blacklistedLocations = Arrays.asList("HighRiskCountry");

        FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        // Verifica que la transacción es marcada como fraudulenta y que requiere verificación
        assertTrue(result.isFraudulent);
        assertTrue(result.verificationRequired);
        assertEquals(50, result.riskScore);
    }
   // 1. Prueba de exceso de transacciones en una hora
   @Test
    void testExcessiveTransactionsInOneHour() {
       FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
       Transaction currentTransaction = new Transaction(5000, LocalDateTime.now(), "USA");
       
       // Crear más de 10 transacciones en la última hora
       List<Transaction> previousTransactions = new ArrayList<>();
       LocalDateTime now = LocalDateTime.now();
       for (int i = 0; i < 11; i++) {
           previousTransactions.add(new Transaction(100, now.minusMinutes(30 + i), "USA"));
       }
       
       List<String> blacklistedLocations = new ArrayList<>();

       FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
       
       // Verifica que la tarjeta se bloquea por transacciones excesivas
       assertTrue(result.isBlocked);
       assertEquals(30, result.riskScore);
   }

   // 2. Prueba de cambio de ubicación en menos de 30 minutos
   @Test
    void testLocationChangeWithinShortTime() {
       FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
       Transaction currentTransaction = new Transaction(5000, LocalDateTime.now(), "France");
       Transaction previousTransaction =new Transaction(5000, LocalDateTime.now().minusMinutes(10), "USA");
       // Transacción anterior en una ubicación diferente, realizada hace menos de 30 minutos
       List<Transaction> previousTransactions = Arrays.asList(previousTransaction);
       
       List<String> blacklistedLocations = new ArrayList<>();

       FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
       // Verifica que la transacción es marcada como fraudulenta
       assertTrue(result.isFraudulent);
       assertTrue(result.verificationRequired);
       assertEquals(20, result.riskScore);
   }

   @Test
    void testLocationNotChangeWithinShortTime() {
       FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
       Transaction currentTransaction = new Transaction(5000, LocalDateTime.now(), "USA");
       Transaction previousTransaction =new Transaction(5000, LocalDateTime.now().minusMinutes(10), "USA");
       // Transacción anterior en una ubicación diferente, realizada hace menos de 30 minutos
       List<Transaction> previousTransactions = Arrays.asList(previousTransaction);
       
       List<String> blacklistedLocations = new ArrayList<>();

       FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
       // Verifica que la transacción es marcada como fraudulenta
       assertFalse(result.isFraudulent);
       assertFalse(result.verificationRequired);
       assertNotEquals(20, result.riskScore);
   }

   // 3. Prueba de transacción normal (sin fraude)
   @Test
    void testNormalTransaction() {
       FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
       Transaction currentTransaction = new Transaction(500, LocalDateTime.now(), "USA");

       List<Transaction> previousTransactions = new ArrayList<>();
       List<String> blacklistedLocations = new ArrayList<>();

       FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

       // Verifica que la transacción no es fraudulenta y no se requiere verificação
       assertFalse(result.isFraudulent);
       assertFalse(result.verificationRequired);
       assertEquals(0, result.riskScore);
   }

    // 2. Prueba de transacciones excesivas en una hora
    @Test
    void testExcessiveTransactions() {
        FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
        Transaction currentTransaction = new Transaction(5000, LocalDateTime.now(), "USA");

        // Genera una lista con más de 10 transacciones en la última hora
        List<Transaction> previousTransactions = new ArrayList<>();
        LocalDateTime oneHourAgo = LocalDateTime.now().minusMinutes(30);
        for (int i = 0; i < 11; i++) {
            previousTransactions.add(new Transaction(100, oneHourAgo, "USA"));
        }

        List<String> blacklistedLocations = new ArrayList<>();

        FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        // Verifica que la tarjeta se bloquea por transacciones excesivas
        assertTrue(result.isBlocked);
        assertEquals(30, result.riskScore);
    }

     // 2. Prueba de transacciones excesivas en una hora
     @Test
     void testExcessiveTransactionsOverHour() {
         FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
         Transaction currentTransaction = new Transaction(5000, LocalDateTime.now(), "USA");
 
         // Genera una lista con más de 10 transacciones en la última hora
         List<Transaction> previousTransactions = new ArrayList<>();
         LocalDateTime oneHourAgo = LocalDateTime.now().minusMinutes(100);
         for (int i = 0; i < 11; i++) {
             previousTransactions.add(new Transaction(100, oneHourAgo, "USA"));
         }
 
         List<String> blacklistedLocations = new ArrayList<>();
 
         FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);
 
         // Verifica que la tarjeta se bloquea por transacciones excesivas
         assertFalse(result.isBlocked);
         assertNotEquals(30, result.riskScore);
     }
 

    // 4. Prueba de transacción desde una ubicación en lista negra
    @Test
    void testBlacklistedLocation() {
        FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
        Transaction currentTransaction = new Transaction(5000, LocalDateTime.now(), "HighRiskCountry");

        List<Transaction> previousTransactions = new ArrayList<>();
        List<String> blacklistedLocations = Arrays.asList("HighRiskCountry");

        FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertTrue(result.isBlocked);
        assertEquals(100, result.riskScore);
    }

    @Test
    void testTransactionAmountExactlyAtLimit() {    
    FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
    Transaction currentTransaction = new Transaction(10000, LocalDateTime.now(), "USA");
    List<Transaction> previousTransactions = new ArrayList<>();
    List<String> blacklistedLocations = Arrays.asList("HighRiskCountry");

    FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

    assertFalse(result.isFraudulent);
    assertFalse(result.verificationRequired);
}

    @Test
    void testNoFraudForSameLocationTransactions() {
        FraudDetectionSystem fraudSystem = new FraudDetectionSystem();
        Transaction currentTransaction = new Transaction(5000, LocalDateTime.now(), "USA");
        Transaction previousTransaction = new Transaction(5000, LocalDateTime.now().minusMinutes(10), "USA");

        List<Transaction> previousTransactions = Arrays.asList(previousTransaction);
        List<String> blacklistedLocations = new ArrayList<>();

        FraudCheckResult result = fraudSystem.checkForFraud(currentTransaction, previousTransactions, blacklistedLocations);

        assertFalse(result.isFraudulent);
        assertFalse(result.verificationRequired);
    }

}
