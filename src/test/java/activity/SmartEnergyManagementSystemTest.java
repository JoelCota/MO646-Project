package activity;
import activity.SmartEnergyManagementSystem.DeviceSchedule;
import activity.SmartEnergyManagementSystem.EnergyManagementResult;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

 class SmartEnergyManagementSystemTest {

    // // 2. Prueba de  activar el modo de ahorro de energía cuando el precio supera el umbral
    @Test
     void testNoEnergySavingMode() {
        SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Heating", 1);
        devicePriorities.put("Lights", 2);
        devicePriorities.put("Appliances", 3);

        // Simula un precio actual inferior al umbral
        EnergyManagementResult result = energySystem.manageEnergy(0.25, 0.20, devicePriorities, LocalDateTime.now(), 21.0, new double[]{20.0, 24.0}, 50, 30, new ArrayList<>());

        // Verifica que el modo de ahorro de energía se activa y los dispositivos se apagan
        assertTrue(result.energySavingMode);
        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
        assertFalse(result.deviceStatus.get("Heating"));
    }

    // 3. Prueba de activación del modo nocturno
    @Test
     void testNightMode() {
        SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Security", 1);
        devicePriorities.put("Lights", 2);
        devicePriorities.put("Appliances", 3);
        devicePriorities.put("Refrigerator", 4);

        // Simula que el tiempo es 11:30 PM
        EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities, LocalDateTime.of(2024, 10, 1, 23, 30), 21.0, new double[]{20.0, 24.0}, 50, 30, new ArrayList<>());

        // Verifica que solo los dispositivos esenciales permanecen encendidos
        assertFalse(result.deviceStatus.get("Lights"));
        assertFalse(result.deviceStatus.get("Appliances"));
        assertTrue(result.deviceStatus.get("Security"));
        assertTrue(result.deviceStatus.get("Refrigerator"));
    }

     // 3. Prueba de activación del modo nocturno
     @Test
     void testNoNightMode() {
         SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
         Map<String, Integer> devicePriorities = new HashMap<>();
         devicePriorities.put("Security", 1);
         devicePriorities.put("Lights", 2);
         devicePriorities.put("Appliances", 3);
         devicePriorities.put("Refrigerator", 4);
 
         EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities,  LocalDateTime.of(2024, 10, 1, 12, 30), 21.0, new double[]{20.0, 24.0}, 50, 30, new ArrayList<>());
 
         // Verifica que solo los dispositivos esenciales permanecen encendidos
         assertTrue(result.deviceStatus.get("Lights"));
         assertTrue(result.deviceStatus.get("Appliances"));
         assertTrue(result.deviceStatus.get("Security"));
         assertTrue(result.deviceStatus.get("Refrigerator"));
     }

    // 4. Prueba de regulación de temperatura
    @Test
     void testTemperatureRegulation() {
        SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Heating", 1);
        devicePriorities.put("Cooling", 2);

        // Simula una temperatura inferior al rango deseado
        EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities, LocalDateTime.now(), 18.0, new double[]{20.0, 24.0}, 50, 30, new ArrayList<>());

        // Verifica que la calefacción se activa
        assertTrue(result.temperatureRegulationActive);
        assertTrue(result.deviceStatus.get("Heating"));
        assertFalse(result.deviceStatus.get("Cooling"));
    }

    @Test
     void testTemperatureRegulationCooling() {
        SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Heating", 1);
        devicePriorities.put("Cooling", 2);

        // Simula una temperatura inferior al rango deseado
        EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities, LocalDateTime.now(), 25.0, new double[]{20.0, 24.0}, 50, 30, new ArrayList<>());

        // Verifica que la calefacción se activa
        assertTrue(result.temperatureRegulationActive);
        assertFalse(result.deviceStatus.get("Heating"));
        assertTrue(result.deviceStatus.get("Cooling"));
    }

    // 1. Prueba de apagado de dispositivos cuando se alcanza el límite de energía
    @Test
     void testShutdownDevicesAtEnergyLimit() {
        SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Heating", 1);   // Alta prioridad
        devicePriorities.put("Lights", 2);    // Baja prioridad
        devicePriorities.put("Appliances", 3); // Baja prioridad

        // Simula el uso de energía total justo por encima del límite
        double energyUsageLimit = 5;  // Límite de uso de energía
        double totalEnergyUsedToday = 6; // Uso total de energía

        EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities, 
            LocalDateTime.now(), 21.0, new double[]{20.0, 24.0}, energyUsageLimit, totalEnergyUsedToday, new ArrayList<>());

        // Verifica que los dispositivos de baja prioridad se apagan
        assertFalse(result.deviceStatus.get("Lights"), "Lights should be turned off");
        assertFalse(result.deviceStatus.get("Appliances"), "Appliances should be turned off");
        assertTrue(result.deviceStatus.get("Heating"), "Heating should remain on");
    }

     // 1. Prueba de apagado de dispositivos cuando se alcanza el límite de energía
     @Test
      void testShutdownDevicesAtEnergyLimitAllPrior() {
         SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
         Map<String, Integer> devicePriorities = new HashMap<>();
         devicePriorities.put("Heating", 2);   // Alta prioridad
         devicePriorities.put("Lights", 1);    // Baja prioridad
         devicePriorities.put("Appliances", 3); // Baja prioridad
 
         // Simula el uso de energía total justo por encima del límite
         double energyUsageLimit = 5;  // Límite de uso de energía
         double totalEnergyUsedToday = 6; // Uso total de energía
 
         EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities, 
             LocalDateTime.now().plusHours(6), 21.0, new double[]{20.0, 24.0}, energyUsageLimit, totalEnergyUsedToday, new ArrayList<>());
 
         // Verifica que los dispositivos de baja prioridad se apagan
         assertTrue(result.deviceStatus.get("Lights"));
         assertTrue(result.deviceStatus.get("Appliances"));
         assertFalse(result.deviceStatus.get("Heating"), "Heating should remain on");
     }

     // 1. Prueba de apagado de dispositivos cuando se alcanza el límite de energía
     @Test
      void testNotShutdownDevicesAtEnergyLimit() {
         SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
         Map<String, Integer> devicePriorities = new HashMap<>();
         devicePriorities.put("Heating", 1);   // Alta prioridad
         devicePriorities.put("Lights", 2);    // Baja prioridad
         devicePriorities.put("Appliances", 3); // Baja prioridad
 
         // Simula el uso de energía total justo por encima del límite
         double energyUsageLimit = 5;  // Límite de uso de energía
         double totalEnergyUsedToday = 4; // Uso total de energía
 
         EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities, 
             LocalDateTime.now().plusHours(6), 19.0, new double[]{20.0, 24.0}, energyUsageLimit, totalEnergyUsedToday, new ArrayList<>());
 
         // Verifica que los dispositivos de baja prioridad se apagan
         assertTrue(result.deviceStatus.get("Lights"));
         assertTrue(result.deviceStatus.get("Appliances"));
         assertTrue(result.deviceStatus.get("Heating"));
     }

    // 6. Prueba de dispositivos programados
    @Test
     void testScheduledDevices() {
        SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Oven", 1);

        // Programa el dispositivo para activarse a las 18:00
        List<DeviceSchedule> schedules = Arrays.asList(new DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 1, 18, 0)));

        // Simula que el tiempo actual coincide con el tiempo programado
        EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities, LocalDateTime.of(2024, 10, 1, 18, 0), 21.0, new double[]{20.0, 24.0}, 50, 30, schedules);

        // Verifica que el dispositivo programado se enciende en el tiempo correcto
        assertTrue(result.deviceStatus.get("Oven"));
    }

    // 6. Prueba de dispositivos programados
    @Test
     void testNotScheduledDevices() {
        SmartEnergyManagementSystem energySystem = new SmartEnergyManagementSystem();
        Map<String, Integer> devicePriorities = new HashMap<>();
        devicePriorities.put("Oven", 1);

        // Programa el dispositivo para activarse a las 18:00
        List<DeviceSchedule> schedules = Arrays.asList(new DeviceSchedule("Oven", LocalDateTime.of(2024, 10, 1, 19, 0)));

        // Simula que el tiempo actual coincide con el tiempo programado
        EnergyManagementResult result = energySystem.manageEnergy(0.15, 0.20, devicePriorities, LocalDateTime.of(2024, 10, 1, 18, 0), 21.0, new double[]{20.0, 24.0}, 50, 30, schedules);

        // Verifica que el dispositivo programado se enciende en el tiempo correcto
        assertTrue(result.deviceStatus.get("Oven"));
    }
}
