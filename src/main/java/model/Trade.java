package model;

public class Trade {

	private String id;
	private String cardId;
	private String type;
	private String status;
	private int mnimumDamage;
	private String initiator;
	private String approver;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getCardId() {
		return cardId;
	}
	public void setCardId(String cardId) {
		this.cardId = cardId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public int getMnimumDamage() {
		return mnimumDamage;
	}
	public void setMnimumDamage(int mnimumDamage) {
		this.mnimumDamage = mnimumDamage;
	}
	public String getInitiator() {
		return initiator;
	}
	public void setInitiator(String initiator) {
		this.initiator = initiator;
	}
	public String getApprover() {
		return approver;
	}
	public void setApprover(String approver) {
		this.approver = approver;
	}
	
}
