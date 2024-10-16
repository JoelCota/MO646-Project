package activity;
import activity.FlightBookingSystem.BookingResult;
import activity.FraudDetectionSystem.FraudCheckResult;
import activity.FraudDetectionSystem.Transaction;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightBookingSystemTest {

    // 1. Prueba de reserva sin asientos suficientes
    @Test
     void testInsufficientSeats() {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        BookingResult result = bookingSystem.bookFlight(5, LocalDateTime.now(), 3, 500.00, 50, false, LocalDateTime.now().plusDays(2), 0);

        // Verifica que la reserva falla debido a falta de asientos
        assertFalse(result.confirmation);
        assertEquals(0, result.totalPrice);
    }

    // 2. Prueba de reserva con precio dinámico y sin otras condiciones especiales
    @Test
     void testDynamicPricing() {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        BookingResult result = bookingSystem.bookFlight(2, LocalDateTime.now(), 100, 500.00, 50, false, LocalDateTime.now().plusDays(2), 0);

        // Verifica que el precio se calcula correctamente usando la fórmula de pricing dinámico
        double expectedPrice = 500.00 * (50 / 100.0) * 0.8 * 2;
        assertTrue(result.confirmation);
        assertEquals(expectedPrice, result.totalPrice);
    }

    // 3. Prueba de tarifa de última hora
    @Test
     void testLastMinuteFee() {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        BookingResult result = bookingSystem.bookFlight(1, LocalDateTime.now(), 100, 500.00, 50, false, LocalDateTime.now().plusHours(12), 0);

        // Verifica que se aplica la tarifa de última hora
        double expectedPrice = (500.00 * (50 / 100.0) * 0.8) + 100;
        assertTrue(result.confirmation);
        assertEquals(expectedPrice, result.totalPrice);
    }

    // 4. Prueba de descuento por grupo
    @Test
     void testGroupDiscount() {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        BookingResult result = bookingSystem.bookFlight(5, LocalDateTime.now(), 100, 500.00, 50, false, LocalDateTime.now().plusDays(2), 0);

        // Verifica que se aplica el descuento del 5% por grupo
        double expectedPrice = (500.00 * (50 / 100.0) * 0.8 * 5) * 0.95;
        assertTrue(result.confirmation);
        assertEquals(expectedPrice, result.totalPrice);
    }

    // 5. Prueba de redención de puntos de recompensa
    @Test
     void testRewardPointsRedemption() {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        BookingResult result = bookingSystem.bookFlight(1, LocalDateTime.now(), 100, 500.00, 50, false, LocalDateTime.now().plusDays(2), 5000);

        // Verifica que los puntos de recompensa son utilizados para reducir el precio
        double expectedPrice = (500.00 * (50 / 100.0) * 0.8) - (5000 * 0.01);
        assertTrue(result.confirmation);
        assertTrue(result.pointsUsed);
        assertEquals(expectedPrice, result.totalPrice);
    }

    // 6. Prueba de cancelación con reembolso completo
    @Test
     void testCancellationFullRefund() {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        BookingResult result = bookingSystem.bookFlight(1, LocalDateTime.now(), 100, 500.00, 50, true, LocalDateTime.now().plusDays(3), 0);

        // Verifica que se otorga un reembolso completo si la cancelación se realiza con más de 48 horas de antelación
        double expectedRefund = 500.00 * (50 / 100.0) * 0.8;
        assertFalse(result.confirmation);
        assertEquals(expectedRefund, result.refundAmount);
    }

    // 7. Prueba de cancelación con reembolso parcial
    @Test
     void testCancellationPartialRefund() {
        FlightBookingSystem bookingSystem = new FlightBookingSystem();
        BookingResult result = bookingSystem.bookFlight(1, LocalDateTime.now(), 100, 500.00, 50, true, LocalDateTime.now().plusHours(30), 0);

        double expectedRefund = (500.00 * (50 / 100.0) * 0.8) * 0.5;
        assertFalse(result.confirmation);
        assertEquals(expectedRefund, result.refundAmount);
    }

    @Test
    void testBookingWithExactSeatsAvailable() {
    FlightBookingSystem bookingSystem = new FlightBookingSystem();
    BookingResult result = bookingSystem.bookFlight(5, LocalDateTime.now(), 5, 500.00, 50, false, LocalDateTime.now().plusDays(2), 0);
    assertTrue(result.confirmation);
    assertEquals((500.00 * (50 / 100.0) * 0.8) * 5 -50 , result.totalPrice); // Verifica el precio calculado
}

    @Test
    void testBookingWithMinimalPassengers() {
    FlightBookingSystem bookingSystem = new FlightBookingSystem();
    BookingResult result = bookingSystem.bookFlight(1, LocalDateTime.now(), 1, 500.00, 50, false, LocalDateTime.now().plusDays(2), 0);
    assertTrue(result.confirmation);
    assertEquals(500.00 * (50 / 100.0) * 0.8, result.totalPrice);
}

@Test
void testBookingWithPassengersJustUnderSeatLimit() {
    FlightBookingSystem bookingSystem = new FlightBookingSystem();
    // Prueba con 4 pasajeros, justo debajo del límite de asientos (5)
    BookingResult result = bookingSystem.bookFlight(4, LocalDateTime.now(), 5, 500.00, 50, false, LocalDateTime.now().plusDays(2), 0);
    assertTrue(result.confirmation);
    assertEquals(500.00 * (50 / 100.0) * 0.8 * 4, result.totalPrice);
}

@Test
void testBookingWithPassengersJustOverSeatLimit() {
    FlightBookingSystem bookingSystem = new FlightBookingSystem();
    // Prueba con 6 pasajeros, superando el límite de asientos disponibles
    BookingResult result = bookingSystem.bookFlight(6, LocalDateTime.now(), 5, 500.00, 50, false, LocalDateTime.now().plusDays(2), 0);
    assertFalse(result.confirmation); // Reserva debe fallar
}



}
