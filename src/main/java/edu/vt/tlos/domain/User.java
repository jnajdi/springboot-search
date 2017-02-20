package edu.vt.tlos.domain;

public class User implements Comparable<User>{
	
  public String userId;
  public String lastName;
  public String firstName;
  public String pid;
  public int roleId;
  public String email;
  public String roleName;
  public String sessionId;
  
  public User(String userId) {
	this.userId = userId;
  }

  public String toString() {
	  if (this.lastName != this.email)
		  return "\"" + this.lastName + ", " + this.firstName + "\"," + this.pid + "," + this.email + "," + this.roleName;
	  else
		  return this.email + "," + this.pid + "," + this.email + "," + this.roleName;
  }

  @Override
  public int compareTo(User o) {
	  if (this.lastName != null && o.lastName != null) {
			int res = this.lastName.compareToIgnoreCase(o.lastName);
			
			if (res != 0) {
				return res;
			}
			if (this.firstName != null && o.firstName != null) {
				return this.firstName.compareToIgnoreCase(o.firstName);
			}
		}
		return 0;		
  }
	  
  
}
