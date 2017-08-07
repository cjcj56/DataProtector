package dpapp.model;

import java.util.List;
import javax.persistence.Query;
import org.hibernate.Session;

import dpapp.view.AppAuthenticatorView;
import utils.CryptServices;

public class AppAuthenticatorImpl implements AppAuthenticator {

	private AppAuthenticatorView view = new AppAuthenticatorViewImpl(this);
	
	
	@Override
	public DataProtector login(String username, String password) {
		Credentials creds = validateUser(username, password);
		if(creds != null) {
			return new DataProtectorImpl(creds);
		} else {
			return null;
		}
	}

	@Override
	public void changeCreds(String userName, String oldPassword, String newPassword) {
		// TODO Auto-generated method stub

	}

	@Override
	public Credentials validateUser(String username, String password) {
		Credentials creds = null;
		Session session = DataProtectorImpl.openSession();
		String select, from, where, queryStr;
		select = "select new Credentials(credId, username, hasedPassword, salt new Keys(privateKey, publicKey, mod))";
		from = "from Credentials inner join Credentials.Keys";
		where = "where username = " + username;
		queryStr = select + " " + from + " " + where;
		Query query = session.createQuery(queryStr);
		List<Credentials> credsRows = query.getResultList();
		byte[] hashedPassword;
		for (Credentials credsRow : credsRows) {
			hashedPassword = Credentials.hashPassword(password, credsRow.getSalt());
			if (CryptServices.byteArrayComparator(hashedPassword, credsRow.getHashedPassword()) == 0) {
				creds = credsRow;
				break;
			}
		}
		session.close();
		return creds;
	}

	@Override
	public void createUser(String username, String password) {
		Session session = DataProtectorImpl.openSession();
		Credentials loggedCreds = new Credentials(username, password);
		session.save(loggedCreds);
		session.getTransaction().commit();
		session.close();
		new DataProtectorImpl(loggedCreds);
	}

	@Override
	public void deleteUser(String username, String password) {
		// TODO Auto-generated method stub

	}

}
