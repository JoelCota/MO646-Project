package activity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SmartEnergyManagementSystem {
    private static final String HEATING = "Heating"; // Constante para "Heating"
    private static final String COOLING = "Cooling"; // Constante para "Cooling"

    public static class DeviceSchedule {
        String deviceName;
        LocalDateTime scheduledTime;

        public DeviceSchedule(String deviceName, LocalDateTime scheduledTime) {
            this.deviceName = deviceName;
            this.scheduledTime = scheduledTime;
        }
    }

    public static class EnergyManagementResult {
        Map<String, Boolean> deviceStatus;
        boolean energySavingMode;
        boolean temperatureRegulationActive;
        double totalEnergyUsed;
        
        public EnergyManagementResult(Map<String, Boolean> deviceStatus, boolean energySavingMode,
                boolean temperatureRegulationActive, double totalEnergyUsed) {
            this.deviceStatus = deviceStatus;
            this.energySavingMode = energySavingMode;
            this.temperatureRegulationActive = temperatureRegulationActive;
            this.totalEnergyUsed = totalEnergyUsed;
        }
    }

    public EnergyManagementResult manageEnergy(double currentPrice, double priceThreshold,
            Map<String, Integer> devicePriorities,
            LocalDateTime currentTime, double currentTemperature, double[] desiredTemperatureRange,
            double energyUsageLimit, double totalEnergyUsedToday, List<DeviceSchedule> scheduledDevices) {

        Map<String, Boolean> deviceStatus = new HashMap<>();
        boolean energySavingMode = false;
        boolean temperatureRegulationActive = false;

        // Activar modo de ahorro de energía
        energySavingMode = activateEnergySavingMode(currentPrice, priceThreshold, devicePriorities, deviceStatus);

        // Modo nocturno
        applyNightMode(currentTime, devicePriorities, deviceStatus);

        // Regulación de temperatura
        temperatureRegulationActive = regulateTemperature(currentTemperature, desiredTemperatureRange, deviceStatus);

        // Apagar dispositivos si se alcanza el límite de energía
        totalEnergyUsedToday = shutDownDevicesAtEnergyLimit(totalEnergyUsedToday, energyUsageLimit, devicePriorities,
                deviceStatus);

        // Manejar dispositivos programados
        handleScheduledDevices(currentTime, scheduledDevices, deviceStatus);

        return new EnergyManagementResult(deviceStatus, energySavingMode, temperatureRegulationActive,
                totalEnergyUsedToday);
    }

    // Método 1: Activar el modo de ahorro de energía
    private boolean activateEnergySavingMode(double currentPrice, double priceThreshold,
            Map<String, Integer> devicePriorities,
            Map<String, Boolean> deviceStatus) {
        boolean energySavingMode = false;
        if (currentPrice > priceThreshold) {
            energySavingMode = true;
            for (Map.Entry<String, Integer> entry : devicePriorities.entrySet()) {
                if (entry.getValue() > 1) { // Prioridades > 1 son baja prioridad
                    deviceStatus.put(entry.getKey(), false);
                } else {
                    deviceStatus.put(entry.getKey(), true); // Alta prioridad se mantiene encendida
                }
            }
        } else {
            // Mantener todos los dispositivos encendidos
            for (String device : devicePriorities.keySet()) {
                deviceStatus.put(device, true);
            }
        }
        return energySavingMode;
    }

    // Método 2: Aplicar el modo nocturno (11 PM - 6 AM)
    private void applyNightMode(LocalDateTime currentTime, Map<String, Integer> devicePriorities,
            Map<String, Boolean> deviceStatus) {
        if (currentTime.getHour() >= 23 || currentTime.getHour() < 6) {
            for (String device : devicePriorities.keySet()) {
                if (!device.equals("Security") && !device.equals("Refrigerator")) {
                    deviceStatus.put(device, false);
                }
            }
        }
    }

    // Método 3: Regular la temperatura
    private boolean regulateTemperature(double currentTemperature, double[] desiredTemperatureRange,
            Map<String, Boolean> deviceStatus) {
        boolean temperatureRegulationActive = false;
       

        if (currentTemperature < desiredTemperatureRange[0]) {
            deviceStatus.put(HEATING, true);
            deviceStatus.put(COOLING, false);
            temperatureRegulationActive = true;
        } else if (currentTemperature > desiredTemperatureRange[1]) {
            deviceStatus.put(COOLING, true);
            deviceStatus.put(HEATING, false);
            temperatureRegulationActive = true;
        } else {
            deviceStatus.put(HEATING, false);
            deviceStatus.put(COOLING, false);
        }
        return temperatureRegulationActive;
    }

    // Método 4: Apagar dispositivos al alcanzar el límite de energía
    private double shutDownDevicesAtEnergyLimit(double totalEnergyUsedToday, double energyUsageLimit,
            Map<String, Integer> devicePriorities,
            Map<String, Boolean> deviceStatus) {
        while (totalEnergyUsedToday >= energyUsageLimit && deviceStatus.containsValue(true)) {
            for (Map.Entry<String, Integer> entry : devicePriorities.entrySet()) {
                if (entry.getValue() > 1 && deviceStatus.get(entry.getKey())) { // Baja prioridad
                    deviceStatus.put(entry.getKey(), false);
                    totalEnergyUsedToday -= 1; // Simular la reducción de energía
                }else{
                    deviceStatus.put(entry.getKey(), true);
                }
            }
        }
        return totalEnergyUsedToday;
    }

    // Método 5: Manejar los dispositivos programados
    private void handleScheduledDevices(LocalDateTime currentTime, List<DeviceSchedule> scheduledDevices,
            Map<String, Boolean> deviceStatus) {
        for (DeviceSchedule schedule : scheduledDevices) {
            if (schedule.scheduledTime.equals(currentTime)) {
                deviceStatus.put(schedule.deviceName, true);
            }
        }
    }
}
