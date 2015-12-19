#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${groupId}.bat;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.hibernate.HibernateException;

import ${groupId}.bat.LoadSwiftRef.ExitValue;

public class Main {

	/**
	 * Description:
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length != 1) {
			System.err.println("Le chemin du fichier de configuration doit être passé en argument.");
			System.exit(LoadSwiftRef.ExitValue.CONFIG_FILE_UNLOADABLE.value);
		}

		String lConfPath = args[0];
		Properties p = new Properties();
		try {
			p.load(new FileInputStream(lConfPath));
			for (Object lName : p.keySet()) {
				String lValue = p.getProperty((String)lName);
				System.setProperty((String)lName, lValue);
			}

			//			//			TEST DE CHARGE
			//			long t;
			//			StringBuilder s = new StringBuilder();
			//			for(int i=0; i < 5; i++) {
			//				t = System.currentTimeMillis();
			//
			//				swiftRefLoader.run();
			//
			//				t = System.currentTimeMillis() - t;
			//				s.append(t).append(";");
			//			}
			//			System.out.println(s.toString());
			//			//------------------------------------------------
			System.exit(LoadSwiftRef.run(lConfPath).value);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.err.println("Impossible de charger de fichier de configuration '"+lConfPath+"'");
			System.exit(ExitValue.CONFIG_FILE_UNLOADABLE.value);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Impossible de charger de fichier de configuration '"+lConfPath+"'");
			System.exit(ExitValue.CONFIG_FILE_UNLOADABLE.value);
		} catch (HibernateException e) {
			e.printStackTrace();
			System.exit(ExitValue.HIBERNATE_EXCEPTION.value);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(ExitValue.UNDEFINED.value);
		}
	}

}
