
public class Disease {
	private int diseaseId;
	private int synonymId;
	private String name;
	private boolean active;
	
	public boolean isActive() {
		return active;
	}
	public void setActive(boolean active) {
		this.active = active;
	}
	public int getDiseaseId() {
		return diseaseId;
	}
	public void setDiseaseId(int diseaseId) {
		this.diseaseId = diseaseId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getSynonymId() {
		return synonymId;
	}
	public void setSynonymId(int synonymId) {
		this.synonymId = synonymId;
	}
	
	
}
