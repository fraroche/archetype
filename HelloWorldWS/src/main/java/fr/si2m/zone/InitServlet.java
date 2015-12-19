package fr.si2m.zone;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.si2m.zone.erreurs.GestionErreurs;
import fr.si2m.zone.utils.ApplicativeProperties;
import fr.si2m.zone.utils.DateUtils;
import fr.si2m.zone.utils.PropertiesLoader;

/**
 * Servlet implementation class InitServlet
 */
public class InitServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final Logger logger = LoggerFactory.getLogger(InitServlet.class);

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public InitServlet() {
		super();
	}

	/**
	 * @see Servlet#init(ServletConfig)
	 */
	@Override
	public void init(ServletConfig config) throws ServletException {
		System.out.println("InitServlet.init(): Debut de l'initialisation");

		PropertiesLoader.initialize("application.properties");
		if (PropertiesLoader.isInitialized()) {
			PropertiesLoader pl = PropertiesLoader.getInstance();
			String msgErreursPath = pl.getString(ApplicativeProperties.MSG_ERREURS_PATH.toString());
			String logConfPath = pl.getString(ApplicativeProperties.LOG_CONF_PATH.toString());
			String formatDate = pl.getString(ApplicativeProperties.DATE_FORMAT.toString());

			GestionErreurs.initialize(msgErreursPath);
			if (GestionErreurs.isInitialized()) {
				DateUtils.initialize(formatDate);
			}
		}
		System.out.println("InitServlet.init(): Initialisation terminée");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
