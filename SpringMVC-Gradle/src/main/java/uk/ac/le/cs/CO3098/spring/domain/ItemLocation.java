package uk.ac.le.cs.CO3098.spring.domain;

import javax.persistence.Entity;

import uk.ac.le.cs.CO3098.spring.domain.BookmarkEntity.Type;

@Entity
public class ItemLocation extends Item{
	
	public ItemLocation(){}
	
	public double getLatitude() {
		return Latitude;
	}
	public void setLatitude(double latitude) {
		Latitude = latitude;
	}
	public double getLongitude() {
		return longitude;
	}
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	public ItemLocation(String name,double latitude, double longitude) {
		super(name);
		setType(Type.LOCATION);
		Latitude = latitude;
		this.longitude = longitude;
	}
	
	double Latitude;
	double longitude;

}
