package beans;

import enums.VehicleType;

public class Vehicle {
	
	private Integer id;
	private String brand;
	private String model;
	private VehicleType vehicleType;
	private String regPlate;
	private Integer yearOfManufacture;
	private Boolean inUse;
	private String note;
	private Integer deliveryID;
	private Boolean deleted;

	public String toFile() {
		return System.getProperty("line.separator") + id + ";" + brand + ";" + model + ";" + vehicleType + ";" + regPlate + ";" + yearOfManufacture + ";" + inUse + ";" + note + ";" + deliveryID + ";" + deleted;
	}
	
	public Vehicle() {
		
	}
	
	public Vehicle(Integer id, String brand, String model, VehicleType vehicleType, String regPlate, Integer yearOfManufacture,
			Boolean inUse, String note, Integer deliveryID, Boolean deleted) {
		this.id = id;
		this.brand = brand;
		this.model = model;
		this.vehicleType = vehicleType;
		this.regPlate = regPlate;
		this.yearOfManufacture = yearOfManufacture;
		this.inUse = inUse;
		this.note = note;
		this.deliveryID = deliveryID;
		this.deleted = deleted;
	}
	
	public final Integer getId() {
		return id;
	}

	public final void setId(Integer id) {
		this.id = id;
	}

	public final String getBrand() {
		return brand;
	}

	public final void setBrand(String brand) {
		this.brand = brand;
	}

	public final String getModel() {
		return model;
	}

	public final void setModel(String model) {
		this.model = model;
	}

	public final VehicleType getVehicleType() {
		return vehicleType;
	}

	public final void setVehicleType(VehicleType vehicleType) {
		this.vehicleType = vehicleType;
	}

	public final String getRegPlate() {
		return regPlate;
	}

	public final void setRegPlate(String regPlate) {
		this.regPlate = regPlate;
	}

	public final Integer getYearOfManufacture() {
		return yearOfManufacture;
	}

	public final void setYearOfManufacture(Integer yearOfManufacture) {
		this.yearOfManufacture = yearOfManufacture;
	}

	public final Boolean getInUse() {
		return inUse;
	}

	public final void setInUse(Boolean inUse) {
		this.inUse = inUse;
	}

	public final String getNote() {
		return note;
	}

	public final void setNote(String note) {
		this.note = note;
	}

	public final Integer getDeliveryID() {
		return deliveryID;
	}

	public final void setDeliveryID(Integer deliveryID) {
		this.deliveryID = deliveryID;
	}

	public final Boolean getDeleted() {
		return deleted;
	}

	public final void setDeleted(Boolean deleted) {
		this.deleted = deleted;
	}

	@Override
	public String toString() {
		return "Vehicle [id=" + id + ", brand=" + brand + ", model=" + model + ", vehicleType=" + vehicleType
				+ ", regPlate=" + regPlate + ", yearOfManufacture=" + yearOfManufacture + ", inUse=" + inUse + ", note="
				+ note + ", deliveryID=" + deliveryID + ", deleted=" + deleted + "]";
	}
	
}
