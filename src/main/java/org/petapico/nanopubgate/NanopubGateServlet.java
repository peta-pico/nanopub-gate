package org.petapico.nanopubgate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.repository.sparql.SPARQLRepository;
import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.Rio;
import org.nanopub.Nanopub;
import org.nanopub.NanopubImpl;
import org.nanopub.NanopubUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NanopubGateServlet extends HttpServlet {

	private static final long serialVersionUID = 0L;

	private SPARQLRepository repository;
	private String[] uriPatterns;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		setGeneralHeaders(resp);
		Nanopub np = null;
		try {
			np = new NanopubImpl(req.getInputStream(), Rio.getParserFormatForMIMEType(req.getContentType()).orElse(RDFFormat.TRIG));
		} catch (Exception ex) {
			resp.sendError(400, "Error reading nanopub: " + ex.getMessage());
		}
		if (np != null) {
			boolean include = false;
			List<Statement> statements = new ArrayList<>();
			for (Statement st : NanopubUtils.getStatements(np)) {
				if (!include) {
					for (String p : uriPatterns) { 
						if (st.getSubject().stringValue().equals(p)) include = true;
						if (st.getPredicate().stringValue().equals(p)) include = true;
						if (st.getObject().stringValue().equals(p)) include = true;
					}
				}
				statements.add(st);
			}
			if (include) {
				getRepository().getConnection().add(statements);
			}
		}
	}

	@Override
	protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		super.doOptions(req, resp);
		setGeneralHeaders(resp);
	}

	@Override
	public void init() throws ServletException {
		logger.info("Init");
		uriPatterns = System.getenv("NPG_URI_PATTERN").trim().split(" ");
	}

	private void setGeneralHeaders(HttpServletResponse resp) {
		resp.setHeader("Access-Control-Allow-Origin", "*");
	}

	private SPARQLRepository getRepository() {
		if (repository == null || !repository.isInitialized()) {
			repository = new SPARQLRepository(System.getenv("NPG_ENDPOINT_URL"));
			String username = System.getenv("NPG_ENDPOINT_USERNAME");
			String password = System.getenv("NPG_ENDPOINT_PASSWORD");
			if (username != null && password != null && !username.isEmpty() && !password.isEmpty())
				repository.setUsernameAndPassword(username, password);
			repository.initialize();
		}
		return repository;
	}

}
