package dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import beans.Vehicle;
import enums.VehicleType;
import util.FileUtil;

public class VehicleDAO {

	private String path;
	
	private final String VEHICLES = "/vehicles.txt";
	
	private Map<Integer, Vehicle> vehicles = new HashMap<>();

	private FileUtil fileUtil;
	
	public VehicleDAO(String path) {
		this.path = path;
		fileUtil = new FileUtil();
		loadVehicles();
		savePeriodically();
	}
	

	private void savePeriodically() {
		final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
		executorService.scheduleAtFixedRate(() -> {
			
			fileUtil.saveAll(path + VEHICLES, allToFile());
			//System.out.println("vehicles saved");
		}, 5, 5, TimeUnit.SECONDS);
	}


	private List<String> allToFile() {
		List<String> vhc = new ArrayList<>();
		
		vehicles.values().forEach(v -> vhc.add(v.toFile()));
		
		return vhc;
	}


	public List<Vehicle> findAll(){
		return new ArrayList<>(vehicles.values());
	}
	
	private void loadVehicles() {
		List<String> lines = fileUtil.read(path + VEHICLES);
		
		for(String line : lines) {
			StringTokenizer st = new StringTokenizer(line, ";");
			if(line.equals("") || line == null) continue;
			while(st.hasMoreTokens()) {
				Integer id = Integer.parseInt(st.nextToken().trim());
				String brand = st.nextToken().trim();
				String model = st.nextToken().trim();
				VehicleType vehicleType = VehicleType.valueOf(st.nextToken().trim());
				String regPlate = st.nextToken().trim();
				Integer year = Integer.parseInt(st.nextToken().trim());
				Boolean inUse = st.nextToken().trim().equalsIgnoreCase("true") ? true : false;
				String note = st.nextToken().trim();
				Integer deliveryID = Integer.parseInt(st.nextToken().trim());
				Boolean deleted = st.nextToken().trim().equalsIgnoreCase("true") ? true : false;
				vehicles.put(id, new Vehicle(id, brand, model, vehicleType, regPlate, year, inUse, note, deliveryID, deleted));
			}
		}
			
	}


	public void deleteVehicle(Integer id) {
		Vehicle vehicle = findById(id);
		vehicle.setDeleted(!vehicle.getDeleted());
	}


	public Vehicle findById(Integer id) {

		return vehicles.get(id);
	}


	public List<Vehicle> getAll() {
		return vehicles.values().stream().collect(Collectors.toList());
	}


	public void addVehicle(Vehicle vehicle) {
		vehicle.setId(vehicles.size());
		vehicle.setDeleted(false);
		vehicle.setDeliveryID(-1);
		vehicle.setInUse(false);
		vehicles.put(vehicle.getId(), vehicle);
	}


	public void editVehicle(Vehicle vehicle) {
		
		Vehicle v = findById(vehicle.getId());
		v.setBrand(vehicle.getBrand());
		v.setModel(vehicle.getModel());
		v.setNote(vehicle.getNote());
		v.setRegPlate(vehicle.getRegPlate());
		v.setVehicleType(vehicle.getVehicleType());
		v.setYearOfManufacture(vehicle.getYearOfManufacture());
	}


	public Vehicle getMyVehicle(Integer id) {
		return vehicles.values().stream()
				.filter(v -> v.getDeliveryID() == id)
				.findFirst().orElse(null);
	}


	public List<Vehicle> getAvailable(Integer id) {
		return vehicles.values().stream()
				.filter(v -> v.getInUse() == false || v.getDeliveryID() == id)
				.collect(Collectors.toList());
	}


	public Vehicle takeOrReturn(Integer id, Integer vehicleId) {
		Vehicle v = vehicles.get(vehicleId);
		if(v.getInUse()) {
			v.setInUse(false);
			v.setDeliveryID(-1);
		} else {
			v.setInUse(true);
			v.setDeliveryID(id);
		}
		return v;
	}
	
	
}
