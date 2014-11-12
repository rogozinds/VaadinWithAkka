package akka.simple;

import java.io.Serializable;

public class SubscriptionApprovement implements Serializable {
	/**
	 * 
	 */
	public enum APPROVED{YES,NO};
	private static final long serialVersionUID = -3785277031328074488L;
	private String message;
	private String approved;
	public SubscriptionApprovement(String message) {
		this.message=message;
	}
	
	public String getApproved() {
		return approved;
	}

	public void setApproved(String approved) {
		this.approved = approved;
	}

	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
}
