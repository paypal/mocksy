package org.mocksy.server.http;

/*
 * Copyright 2009, PayPal
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

import java.io.File;
import java.net.URL;
import java.util.logging.LogManager;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.mocksy.config.xml.XmlRulesetConfig;
import org.mocksy.rules.Ruleset;
import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.ContextHandlerCollection;
import org.mortbay.jetty.nio.SelectChannelConnector;
import org.mortbay.jetty.security.SslSocketConnector;
import org.mortbay.jetty.servlet.ServletHolder;
import org.mortbay.jetty.webapp.WebAppContext;
import org.mortbay.resource.FileResource;

/**
 * HTTP server to process Mocksy requests.
 *  
 * @author Saleem Shafi
 */
public class MocksyServer {
	private Ruleset ruleset;
	private Server server;
	private boolean admin;
	private String keystore;
	private String storepass;
	private int port;

	/**
	 * Create a server that will process request with the given Ruleset
	 * and running on the given port.
	 * 
	 * @param rules the Ruleset to process requests with
	 * @param port the port to run the server on
	 */
	public MocksyServer(Ruleset rules, int port) {
		this.ruleset = rules;
		this.port = port;
	}

	/**
	 * Specify whether or not to run the admin portion of the server.
	 * @param admin
	 */
	void startAdminPort(boolean admin) {
		this.admin = admin;
	}

	/**
	 * Set the keystore location and password.  If the location exists
	 * the server will open an SSL port using this as the keystore.
	 * 
	 * @param keystore the keystore to use for a secure server
	 * @param password the keystore password
	 */
	void setKeystore(String keystore, String password) {
		this.keystore = keystore;
		this.storepass = password;
	}

	/**
	 * Starts the Mocksy server.
	 * 
	 * @throws Exception
	 */
	public synchronized void start() throws Exception {
		if ( this.server == null ) {
			this.server = new Server();
			ContextHandlerCollection contexts = new ContextHandlerCollection();
			this.server.setHandler( contexts );

			if ( this.admin ) {
				// open up the admin connector
				Connector adminConnector = setupAdminConnector();
				this.server.addConnector( adminConnector );

				WebAppContext adminContext = new WebAppContext();
				adminContext.setContextPath( "/" );
				// display Ruleset in XML
				ServletHolder rulesServlet = new ServletHolder(
				        new RulesServlet( this.ruleset ) );
				adminContext.addServlet( rulesServlet, "/rules" );

				// show log files
				adminContext.setBaseResource( new FileResource( new File( "." )
				        .toURI().toURL() ) );
				adminContext.setConnectorNames( new String[] { adminConnector
				        .getName() } );
				contexts.addHandler( adminContext );
				WebAppContext logsContext = new WebAppContext();
				logsContext.setContextPath( "/logs" );
				logsContext.setBaseResource( new FileResource(
				        new File( "logs" ).toURI().toURL() ) );
				logsContext.setConnectorNames( new String[] { adminConnector
				        .getName() } );
				contexts.addHandler( logsContext );

			}

			// setup main connector
			Connector requestConnector = this.setupRequestConnector();
			this.server.addConnector( requestConnector );
			WebAppContext requestContext = new WebAppContext();
			requestContext.setContextPath( "/" );
			// process all requests with Ruleset
			ServletHolder requestServlet = new ServletHolder(
			        new RequestServlet( this.ruleset ) );
			requestContext.addServlet( requestServlet, "/*" );
			requestContext.setBaseResource( new FileResource( new File( "." )
			        .toURI().toURL() ) );
			requestContext.setConnectorNames( new String[] { requestConnector
			        .getName() } );
			contexts.addHandler( requestContext );
		}
		this.server.start();
	}

	/**
	 * Stops the server and frees up the port again.
	 * 
	 * @throws Exception if server cannot be shutdown
	 */
	public void stop() throws Exception {
		this.server.stop();
	}

	/**
	 * Creates the main request-processing connector on the 
	 * appropriate port.  If the keystore is specified and valid,
	 * the port will use SSL.
	 * 
	 * @return the main connector
	 */
	protected Connector setupRequestConnector() {
		Connector connector = new SelectChannelConnector();
		if ( this.keystore != null ) {
			File keystore = new File( this.keystore );
			if ( keystore.exists() && keystore.isFile() ) {
				SslSocketConnector sslConnector = new SslSocketConnector();
				sslConnector.setKeystore( keystore.getAbsolutePath() );
				sslConnector.setPassword( this.storepass );
				connector = sslConnector;
			}
		}
		connector.setPort( this.port );
		return connector;
	}

	/**
	 * Creates the admin connector on the next port after the
	 * main one.
	 * 
	 * @return the admin connector
	 */
	protected Connector setupAdminConnector() {
		Connector connector = new SelectChannelConnector();
		connector.setPort( this.port + 1 );
		return connector;
	}

	/**
	 * Main method that starts the server.  Run it with the -h switch
	 * for usage info.
	 * 
	 * @param args the program arguments
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		configureLogger();
		CommandLine line = parseCommandLine( args );

		String rulesLocation = line.getOptionValue( 'r', "." );
		String keystore = line.getOptionValue( 'k', "./keystore.jks" );
		String password = line.getOptionValue( 'P', "password" );
		boolean startAdminServlet = line.hasOption( 'a' );
		int port = Integer.parseInt( line.getOptionValue( 'p', "8080" ) );

		Ruleset mocksy = getRuleset( rulesLocation );
		MocksyServer server = new MocksyServer( mocksy, port );
		server.startAdminPort( startAdminServlet );
		server.setKeystore( keystore, password );
		server.start();
	}

	/**
	 * Parses the program arguments into an Apache CLI CommandLine. If the
	 * -h switch is present, the usage info will be printed to System.out.
	 * If anything else goes wrong, the exception will be printed.
	 * 
	 * @param args the program arguments
	 * @return an Apache CLI CommandLine
	 */
	static CommandLine parseCommandLine(String[] args) {
		CommandLineParser parser = new GnuParser();
		Options options = new Options();
		options.addOption( "h", "help", false, "show this message" );
		options.addOption( "r", "ruleset", true,
		        "root configuration directory (default: working directory)" );
		options.addOption( "p", "port", true,
		        "port that server will listen on (default: 8080)" );
		options
		        .addOption( "k", "keystore", true,
		                "location of the keystore, if running in SSL (default: ./keystore.jks)" );
		options.addOption( "P", "password", true,
		        "password to open the keystore (default: password)" );
		options.addOption( "a", "admin", false,
		        "open admin port (one higher than request port)" );

		CommandLine line = null;
		boolean help = true;
		try {
			line = parser.parse( options, args );
			help = line.hasOption( "h" );
		}
		catch ( ParseException e ) {
			e.printStackTrace();
		}
		if ( help ) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp( "java -jar mocksy-server.jar", options, true );
			System.exit( 0 );
		}
		return line;
	}

	/**
	 * Return a Ruleset found at the given location.  If the location
	 * starts with 'http', we'll load it as a URL; otherwise, we
	 * assume it's a local file.
	 * 
	 * @param rulesLocation the location of the ruleset
	 * @return the Ruleset
	 * @throws Exception if there's a problem loading the Ruleset
	 */
	static Ruleset getRuleset(String rulesLocation) throws Exception {
		Ruleset mocksy = null;
		if ( rulesLocation.startsWith( "http" ) ) {
			mocksy = new XmlRulesetConfig( new URL( rulesLocation ) )
			        .getRuleset();
		}
		else {
			mocksy = new XmlRulesetConfig( new File( rulesLocation ) )
			        .getRuleset();
		}
		return mocksy;
	}

	public static void configureLogger() {
		try {
			// ridiculous workaround for
			// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6244047
			new File( "logs" ).mkdirs();

			LogManager.getLogManager().readConfiguration(
			        MocksyServer.class
			                .getResourceAsStream( "logging.properties" ) );
		}
		catch ( Exception ex ) {
			ex.printStackTrace();
		}
	}
}
