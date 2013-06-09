package umltemporal;

public abstract class UmlElement {

	protected String id;

	public String getID() {
		return id;
	}

	public UmlElement(String id) {
		this.id = id;
	}
}
