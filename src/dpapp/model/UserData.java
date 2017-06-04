package dpapp.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

/*
 * 
 * All user data is stored with three fields: title, description, data.
 *  - title : the "key" identifying the data (not necessarily unique).
 *  - description : a short description of the data stored (optional).
 *  - data : the field holding the actual data.
 * 
 * @author  Binyamin Kisch
 * @version 1.1
 * @since   2017-05-15
 */

@Entity
public class UserData {
	
	@Id @GeneratedValue
	private long userDataId;
	private String title;
	private String description;
	@Lob
	private String userData;
	
	
	public UserData() {}
	
	public UserData(String title, String description, String userData) {
		this.title = title;
		this.description = description;
		this.userData = userData;
	}
	
	public UserData(int userDataId, String title, String description, String userData) {
		this.userDataId = userDataId;
		this.title = title;
		this.description = description;
		this.userData = userData;
	}
	
	
	public long getUserDataId() {
		return userDataId;
	}
	public void setUserDataId(long userDataId) {
		this.userDataId = userDataId;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getUserData() {
		return userData;
	}
	public void setUserData(String userData) {
		this.userData = userData;
	}
	
}
